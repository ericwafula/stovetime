package com.ericwathome.stovetime.navigation.entry

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.ericwathome.stovetime.R
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.navigation.Destination

fun navigationPlaceholder(
    key: Destination,
    @StringRes title: Int,
): NavEntry<NavKey> = NavEntry(key) { PlaceholderTab(title = stringResource(title)) }

@Composable
private fun PlaceholderTab(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().background(StovetimeTheme.colors.canvas),
        contentAlignment = Alignment.Center,
    ) {
        StoveText(text = title, style = StovetimeTheme.typography.title)
    }
}

@Preview
@Composable
private fun PlaceholderTabPreview() {
    StovetimeTheme {
        PlaceholderTab(title = stringResource(R.string.tab_recipes))
    }
}
