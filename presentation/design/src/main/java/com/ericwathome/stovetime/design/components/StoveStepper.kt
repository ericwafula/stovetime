package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

private val StepTouchSize = 48.dp
private val StepSize = 36.dp
private val ValueWidth = 32.dp

@Composable
fun StoveStepper(
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    decreaseLabel: String,
    increaseLabel: String,
    modifier: Modifier = Modifier,
    canDecrease: Boolean = true,
    canIncrease: Boolean = true,
    decreaseModifier: Modifier = Modifier,
    increaseModifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        StepButton(
            icon = StoveIcons.Minus,
            contentDescription = decreaseLabel,
            enabled = canDecrease,
            onClick = onDecrease,
            modifier = decreaseModifier,
        )
        StoveText(
            text = value,
            style = StovetimeTheme.typography.heading,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = ValueWidth).semantics { liveRegion = LiveRegionMode.Polite },
        )
        StepButton(
            icon = StoveIcons.Plus,
            contentDescription = increaseLabel,
            enabled = canIncrease,
            onClick = onIncrease,
            modifier = increaseModifier,
        )
    }
}

@Composable
private fun StepButton(
    icon: StoveIcons,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    Box(
        modifier =
            modifier
                .size(StepTouchSize)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(StepSize)
                    .background(colors.sunken, StovetimeTheme.shapes.small),
            contentAlignment = Alignment.Center,
        ) {
            StoveIcon(
                icon = icon,
                contentDescription = contentDescription,
                tint = if (enabled) colors.textPrimary else colors.lockedContent,
                size = 18.dp,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StoveStepperPreview() {
    StovetimePreviewTheme(background = { it.surface }) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StoveStepper(value = "4", onDecrease = {}, onIncrease = {}, decreaseLabel = "Fewer", increaseLabel = "More")
            StoveStepper(value = "1", onDecrease = {}, onIncrease = {}, decreaseLabel = "Fewer", increaseLabel = "More", canDecrease = false)
            StoveStepper(value = "7", onDecrease = {}, onIncrease = {}, decreaseLabel = "Fewer", increaseLabel = "More", canIncrease = false)
        }
    }
}
