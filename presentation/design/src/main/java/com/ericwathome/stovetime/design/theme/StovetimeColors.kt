package com.ericwathome.stovetime.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class StovetimeColors(
    val canvas: Color,
    val surface: Color,
    val sunken: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val onAccent: Color,
    val primary: Color,
    val primaryShade: Color,
    val primarySoft: Color,
    val success: Color,
    val successShade: Color,
    val successSoft: Color,
    val xp: Color,
    val xpShade: Color,
    val xpSoft: Color,
    val lockedFill: Color,
    val lockedContent: Color,
)

internal val LightColors =
    StovetimeColors(
        canvas = Palette.Flour,
        surface = Palette.White,
        sunken = Palette.Oat,
        border = Palette.Crust,
        textPrimary = Palette.Espresso,
        textSecondary = Palette.Cocoa,
        onAccent = Palette.White,
        primary = Palette.Tomato500,
        primaryShade = Palette.Tomato700,
        primarySoft = Palette.Tomato100,
        success = Palette.Basil500,
        successShade = Palette.Basil700,
        successSoft = Palette.Basil100,
        xp = Palette.Yolk500,
        xpShade = Palette.Yolk700,
        xpSoft = Palette.Yolk100,
        lockedFill = Palette.Ash,
        lockedContent = Palette.Pebble,
    )
