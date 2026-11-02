package com.ericwathome.stovetime.ui.helpers

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

val isKeyboardOpen: Boolean
    @Composable
    get() = WindowInsets.ime.getBottom(LocalDensity.current) > 0
