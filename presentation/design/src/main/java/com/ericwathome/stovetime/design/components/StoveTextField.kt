package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

private val FieldShape = RoundedCornerShape(14.dp)

@Composable
fun StoveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: StoveIcons? = null,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    imeAction: ImeAction = ImeAction.Done,
) {
    val colors = StovetimeTheme.colors
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = StovetimeTheme.typography.body.copy(color = colors.textPrimary),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = KeyboardOptions(capitalization = capitalization, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { focusManager.clearFocus() }),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Row(
                modifier =
                    Modifier
                        .background(colors.surface, FieldShape)
                        .border(2.dp, colors.border, FieldShape)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                leadingIcon?.let { StoveIcon(it, contentDescription = null, tint = colors.textSecondary, size = 20.dp) }
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        StoveText(text = placeholder, color = colors.textSecondary, maxLines = 1)
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Composable
fun StoveSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    StoveTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        leadingIcon = StoveIcons.Search,
        capitalization = KeyboardCapitalization.None,
        imeAction = ImeAction.Search,
    )
}

@PreviewLightDark
@Composable
private fun StoveTextFieldPreview() {
    StovetimePreviewTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StoveSearchField(value = "", onValueChange = {}, placeholder = "Search recipes")
            StoveSearchField(value = "chicken", onValueChange = {}, placeholder = "Search recipes")
            StoveTextField(value = "", onValueChange = {}, placeholder = "Name it, e.g. Rice (optional)")
            StoveTextField(value = "Rice", onValueChange = {}, placeholder = "Name it, e.g. Rice (optional)")
        }
    }
}
