package com.ericwathome.stovetime.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.ericwathome.stovetime.core.common.helpers.Elapsed
import com.ericwathome.stovetime.ui.R

@Composable
fun Elapsed.agoText(): String =
    when (this) {
        Elapsed.JustNow -> stringResource(R.string.elapsed_just_now)
        is Elapsed.Minutes -> pluralStringResource(R.plurals.elapsed_minutes_ago, count, count)
        is Elapsed.Hours -> pluralStringResource(R.plurals.elapsed_hours_ago, count, count)
        is Elapsed.Days -> pluralStringResource(R.plurals.elapsed_days_ago, count, count)
    }
