package helpers

import org.gradle.api.GradleException
import org.gradle.api.Project

internal data class HardcodedText(
    val line: Int,
    val text: String,
)

private val userFacingTextArgument =
    Regex("""\b(Text|text|title|description|contentDescription|supportingText|placeholder)\s*(=|\()\s*"((?:[^"\\]|\\.)*)"""")
private val stringTemplate = Regex("""\$\{[^}]*}|\$[A-Za-z_][A-Za-z0-9_]*""")
private val previewDeclaration = Regex("""^@\w*Preview\b|\bfun \w*Preview\w*\(""")

internal fun findHardcodedText(lines: List<String>): List<HardcodedText> {
    val found = mutableListOf<HardcodedText>()
    var depth = 0
    var previewPending = false
    var previewDepth: Int? = null

    lines.forEachIndexed { index, line ->
        val code = line.substringBefore("//").trim()
        if (previewDeclaration.containsMatchIn(code)) previewPending = true

        if (!previewPending && previewDepth == null) {
            userFacingTextArgument.findAll(code).forEach { match ->
                val literal = match.groupValues[3]
                if (stringTemplate.replace(literal, "").any(Char::isLetter)) found += HardcodedText(index + 1, literal)
            }
        }

        val opened = code.count { it == '{' }
        if (previewPending && opened > 0) {
            previewDepth = depth
            previewPending = false
        }
        depth += opened - code.count { it == '}' }
        if (previewDepth != null && depth <= previewDepth!!) previewDepth = null
    }
    return found
}

internal fun Project.configureHardcodedTextCheck() {
    val checkHardcodedText =
        tasks.register("checkHardcodedText") {
            group = "verification"
            description = "Fails when user-facing text is a Kotlin string literal instead of a string resource."
            val sources = fileTree("src/main") { include("**/*.kt") }
            inputs.files(sources)
            doLast {
                val findings =
                    sources.files.sorted().flatMap { file ->
                        findHardcodedText(file.readLines()).map { "${file.relativeTo(projectDir)}:${it.line}: \"${it.text}\"" }
                    }
                if (findings.isNotEmpty()) {
                    throw GradleException(
                        "User-facing text must come from strings.xml (values/ and values-sw/):\n" + findings.joinToString("\n"),
                    )
                }
            }
        }
    tasks.matching { it.name == "check" }.configureEach { dependsOn(checkHardcodedText) }
}
