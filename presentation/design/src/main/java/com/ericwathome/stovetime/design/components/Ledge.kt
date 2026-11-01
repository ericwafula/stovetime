package com.ericwathome.stovetime.design.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun LedgeSurface(
    face: Color,
    ledge: Color,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    ledgeHeight: Dp,
    border: Color? = null,
    onClick: (() -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val drop by animateDpAsState(if (pressed && onClick != null) ledgeHeight else 0.dp, label = "ledge")
    val clickModifier =
        if (onClick != null) {
            Modifier.clickable(interactionSource = interactionSource, indication = null, role = Role.Button, onClick = onClick)
        } else {
            Modifier
        }
    Box(modifier = modifier.then(clickModifier), propagateMinConstraints = true) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = ledgeHeight)
                .background(ledge, shape),
        )
        Box(
            modifier =
                Modifier
                    .padding(top = drop, bottom = ledgeHeight - drop)
                    .background(face, shape)
                    .then(if (border != null) Modifier.border(2.dp, border, shape) else Modifier),
            contentAlignment = contentAlignment,
            content = content,
        )
    }
}
