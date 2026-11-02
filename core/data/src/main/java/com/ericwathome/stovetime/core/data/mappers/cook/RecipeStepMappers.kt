package com.ericwathome.stovetime.core.data.mappers.cook

import com.ericwathome.stovetime.core.data.model.cook.CookPlan
import com.ericwathome.stovetime.core.data.model.cook.CookStep
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.recipes.Recipe

private const val SECONDS_PER_MINUTE = 60
private const val SECONDS_PER_HOUR = 3_600

private val stepNumber = Regex("""^(?:step\s*)?\d+\s*[.):-]?\s*""", RegexOption.IGNORE_CASE)
private val sentenceBreak = Regex("""(?<=[.!?])\s+(?=[A-Z0-9])""")
private val fractions = mapOf('½' to 0.5, '¼' to 0.25, '¾' to 0.75)
private val duration =
    Regex(
        """(\d+(?:[.,]\d+)?)?\s*([½¼¾])?(?:\s*(?:-|–|to)\s*\d+(?:[.,]\d+)?\s*[½¼¾]?)?\s*(hours?|hrs?|minutes?|mins?|seconds?|secs?)\b""",
        RegexOption.IGNORE_CASE,
    )

internal fun splitInstructions(instructions: String): List<String> {
    val lines =
        instructions
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .split('\n')
            .map { it.trim().replace(stepNumber, "").trim() }
            .filter { it.isNotEmpty() }
    if (lines.size > 1) return lines
    val text = lines.firstOrNull() ?: return emptyList()
    return text.split(sentenceBreak).map { it.trim() }.filter { it.isNotEmpty() }
}

internal fun timerSecondsIn(step: String): Int? =
    duration.findAll(step).firstNotNullOfOrNull { match ->
        val (whole, fraction, unit) = match.destructured
        if (whole.isEmpty() && fraction.isEmpty()) return@firstNotNullOfOrNull null
        val amount = (whole.replace(',', '.').toDoubleOrNull() ?: 0.0) + (fraction.firstOrNull()?.let { fractions[it] } ?: 0.0)
        val perUnit =
            when (unit.lowercase().first()) {
                'h' -> SECONDS_PER_HOUR
                'm' -> SECONDS_PER_MINUTE
                else -> 1
            }
        (amount * perUnit).toInt().takeIf { it > 0 }
    }

internal fun Recipe.toCookPlan(xp: Int): CookPlan =
    CookPlan(
        kind = CookKind.Recipe,
        dishId = id,
        name = name,
        photo = photo,
        baseXp = xp,
        steps =
            splitInstructions(instructions).map { text ->
                CookStep(text = text, detail = null, timerSeconds = timerSecondsIn(text), doneness = null, tip = null)
            },
    )
