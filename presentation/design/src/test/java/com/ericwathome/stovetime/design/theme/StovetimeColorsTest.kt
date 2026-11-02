package com.ericwathome.stovetime.design.theme

import androidx.compose.ui.graphics.Color
import com.ericwathome.stovetime.design.testing.contrastRatio
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

class StovetimeColorsTest {
    private val themes = mapOf("light" to LightColors, "dark" to DarkColors)

    private val readablePairs: List<Pair<(StovetimeColors) -> Color, (StovetimeColors) -> Color>> =
        listOf(
            { c: StovetimeColors -> c.textPrimary } to { c: StovetimeColors -> c.canvas },
            { c: StovetimeColors -> c.textPrimary } to { c: StovetimeColors -> c.surface },
            { c: StovetimeColors -> c.textPrimary } to { c: StovetimeColors -> c.sunken },
            { c: StovetimeColors -> c.textSecondary } to { c: StovetimeColors -> c.canvas },
            { c: StovetimeColors -> c.textSecondary } to { c: StovetimeColors -> c.surface },
            { c: StovetimeColors -> c.textSecondary } to { c: StovetimeColors -> c.sunken },
            { c: StovetimeColors -> c.onAccent } to { c: StovetimeColors -> c.primary },
            { c: StovetimeColors -> c.onAccent } to { c: StovetimeColors -> c.success },
            { c: StovetimeColors -> c.onXp } to { c: StovetimeColors -> c.xp },
            { c: StovetimeColors -> c.surface } to { c: StovetimeColors -> c.textPrimary },
        )

    private val accentTextPairs: List<Pair<(StovetimeColors) -> Color, (StovetimeColors) -> Color>> =
        listOf(
            { c: StovetimeColors -> c.primaryContent } to { c: StovetimeColors -> c.canvas },
            { c: StovetimeColors -> c.primaryContent } to { c: StovetimeColors -> c.surface },
            { c: StovetimeColors -> c.primaryContent } to { c: StovetimeColors -> c.primarySoft },
            { c: StovetimeColors -> c.successContent } to { c: StovetimeColors -> c.successSoft },
            { c: StovetimeColors -> c.xpContent } to { c: StovetimeColors -> c.xpSoft },
            { c: StovetimeColors -> c.xpContent } to { c: StovetimeColors -> c.canvas },
            { c: StovetimeColors -> c.successContent } to { c: StovetimeColors -> c.canvas },
            { c: StovetimeColors -> c.textPrimary } to { c: StovetimeColors -> c.xpSoft },
        )

    @Test
    fun every_light_token_has_a_dark_value() {
        // given
        val tokens = tokenFields()

        // when
        val missing = tokens.filter { field -> Color(field.getLong(DarkColors).toULong()) == Color.Unspecified }

        // then
        assertThat(tokens).isNotEmpty()
        assertThat(missing).isEmpty()
    }

    @Test
    fun dark_surfaces_and_text_differ_from_light() {
        // given
        val themed = listOf<(StovetimeColors) -> Color>({ it.canvas }, { it.surface }, { it.sunken }, { it.border }, { it.textPrimary }, { it.textSecondary })

        // when
        val unchanged = themed.filter { token -> token(LightColors) == token(DarkColors) }

        // then
        assertThat(unchanged).isEmpty()
    }

    @Test
    fun text_tokens_keep_at_least_4_5_to_1_on_their_backgrounds_in_both_themes() {
        // given
        val ratios =
            themes.flatMap { (name, colors) ->
                readablePairs.map { (text, background) -> "$name ${text(colors)} on ${background(colors)}" to contrastRatio(text(colors), background(colors)) }
            }

        // when
        val failing = ratios.filter { (_, ratio) -> ratio < MIN_TEXT_CONTRAST }

        // then
        assertThat(failing).isEmpty()
    }

    @Test
    fun accent_text_stays_readable_in_both_themes() {
        // given
        val ratios =
            themes.flatMap { (_, colors) -> accentTextPairs.map { (text, background) -> contrastRatio(text(colors), background(colors)) } }

        // when
        val failing = ratios.filter { it < MIN_TEXT_CONTRAST }

        // then
        assertThat(failing).isEmpty()
    }

    @Test
    fun the_alarm_keeps_white_text_on_tomato() {
        // when
        val ratio = contrastRatio(LightColors.onAccent, LightColors.primary)

        // then
        assertThat(LightColors.onAccent).isEqualTo(Palette.White)
        assertThat(ratio).isAtLeast(MIN_TEXT_CONTRAST)
    }

    @Test
    fun the_window_background_matches_the_canvas_in_both_themes() {
        // when
        val light = windowCanvas("values")
        val dark = windowCanvas("values-night")

        // then
        assertThat(light).isEqualTo(LightColors.canvas)
        assertThat(dark).isEqualTo(DarkColors.canvas)
    }

    private fun tokenFields() =
        StovetimeColors::class.java.declaredFields
            .filter { it.type == Long::class.javaPrimitiveType }
            .onEach { it.isAccessible = true }

    private fun windowCanvas(folder: String): Color {
        val xml = File("src/main/res/$folder/colors.xml").readText()
        val hex = Regex("""name="stovetime_canvas">#([0-9A-Fa-f]{8})<""").find(xml)!!.groupValues[1]
        return Color(hex.toLong(16))
    }

    private companion object {
        const val MIN_TEXT_CONTRAST = 4.5
    }
}
