package com.ericwathome.stovetime.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ericwathome.stovetime.design.R

@Immutable
data class StovetimeTypography(
    val display: TextStyle,
    val title: TextStyle,
    val heading: TextStyle,
    val number: TextStyle,
    val body: TextStyle,
    val bodyStrong: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
    val button: TextStyle,
)

@OptIn(ExperimentalTextApi::class)
private fun variableFont(
    resource: Int,
    weight: FontWeight,
) = Font(resource, weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))

private val Gabarito =
    FontFamily(
        variableFont(R.font.gabarito, FontWeight.Bold),
        variableFont(R.font.gabarito, FontWeight.ExtraBold),
    )

private val Rubik =
    FontFamily(
        variableFont(R.font.rubik, FontWeight.Normal),
        variableFont(R.font.rubik, FontWeight.Medium),
        variableFont(R.font.rubik, FontWeight.SemiBold),
    )

private fun gabarito(
    weight: FontWeight,
    size: Int,
    lineHeight: Int,
    tracking: Double = 0.0,
) = TextStyle(fontFamily = Gabarito, fontWeight = weight, fontSize = size.sp, lineHeight = lineHeight.sp, letterSpacing = tracking.em)

private fun rubik(
    weight: FontWeight,
    size: Int,
    lineHeight: Int,
    tracking: Double = 0.0,
) = TextStyle(fontFamily = Rubik, fontWeight = weight, fontSize = size.sp, lineHeight = lineHeight.sp, letterSpacing = tracking.em)

internal val DefaultTypography =
    StovetimeTypography(
        display = gabarito(FontWeight.ExtraBold, size = 34, lineHeight = 38, tracking = -0.015),
        title = gabarito(FontWeight.ExtraBold, size = 24, lineHeight = 28, tracking = -0.0125),
        heading = gabarito(FontWeight.Bold, size = 18, lineHeight = 22),
        number = gabarito(FontWeight.ExtraBold, size = 44, lineHeight = 44, tracking = -0.02),
        body = rubik(FontWeight.Normal, size = 16, lineHeight = 24),
        bodyStrong = rubik(FontWeight.Medium, size = 16, lineHeight = 24),
        label = rubik(FontWeight.SemiBold, size = 14, lineHeight = 18, tracking = 0.015),
        caption = rubik(FontWeight.Normal, size = 13, lineHeight = 18),
        button = gabarito(FontWeight.Bold, size = 17, lineHeight = 20, tracking = 0.025),
    )
