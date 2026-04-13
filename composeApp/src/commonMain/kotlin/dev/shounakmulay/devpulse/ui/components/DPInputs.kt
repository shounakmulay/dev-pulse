package dev.shounakmulay.devpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.ui.theme.DPTheme

// ─────────────────────────────────────────────────────────────────────────────
// DPTEXTFIELD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val colours = DPTheme.colours
    val typography = DPTheme.typography
    val shape = DPTheme.shape
    val spacing = DPTheme.spacing

    var isFocused by remember { mutableStateOf(false) }

    val borderColour = when {
        isError   -> colours.signalErrorText.copy(alpha = 0.5f)
        isFocused -> colours.borderStrong
        else      -> colours.borderDefault
    }

    val shape2d = RoundedCornerShape(shape.sm)

    Column(modifier = modifier) {
        if (label != null) {
            DPLabel(
                text = label,
                size = DPTextSize.Medium,
                color = colours.textSecondary,
                modifier = Modifier.padding(bottom = spacing.xs),
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            textStyle = typography.bodyMedium.copy(color = colours.textPrimary),
            cursorBrush = SolidColor(colours.accentPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape2d)
                        .background(colours.backgroundElevated, shape2d)
                        .border(1.dp, borderColour, shape2d)
                        .padding(horizontal = spacing.md)
                        .height(44.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = colours.textTertiary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(spacing.sm))
                    }
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            DPBody(
                                text = placeholder,
                                size = DPTextSize.Medium,
                                color = colours.textTertiary,
                            )
                        }
                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        Spacer(Modifier.width(spacing.sm))
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = colours.textTertiary,
                            modifier = Modifier
                                .size(16.dp)
                                .then(
                                    if (onTrailingIconClick != null)
                                        Modifier.clickable(onClick = onTrailingIconClick)
                                    else Modifier
                                ),
                        )
                    }
                }
            },
        )

        if (isError && errorMessage != null) {
            DPLabel(
                text = errorMessage,
                size = DPTextSize.Small,
                color = colours.signalErrorText,
                modifier = Modifier.padding(top = spacing.xs),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DPDIVIDER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = DPTheme.colours.borderSubtle,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// DPLISTROW — standard feed / article list item
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val colours = DPTheme.colours
    val spacing = DPTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.listItemHeight)
            .background(colours.backgroundElevated)
            .then(
                if (onClick != null)
                    Modifier.clickable(indication = ripple(), interactionSource = null, onClick = onClick)
                else Modifier
            )
            .padding(horizontal = spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(Modifier.width(spacing.md))
        }

        Column(modifier = Modifier.weight(1f)) {
            DPTitle(
                text = title,
                size = DPTextSize.Small,
                maxLines = 1,
            )
            if (subtitle != null) {
                DPMono(
                    text = subtitle,
                    size = DPTextSize.Small,
                )
            }
        }

        if (trailingContent != null) {
            Spacer(Modifier.width(spacing.sm))
            trailingContent()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DPSETTINGSROW — settings screen list item
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPSettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: String? = null,
    onClick: (() -> Unit)? = null,
    showChevron: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    val colours = DPTheme.colours
    val spacing = DPTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.listItemHeightCompact)
            .background(colours.backgroundElevated)
            .then(
                if (onClick != null)
                    Modifier.clickable(indication = ripple(), interactionSource = null, onClick = onClick)
                else Modifier
            )
            .padding(horizontal = spacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            DPTitle(text = title, size = DPTextSize.Small)
            if (subtitle != null) {
                DPMono(text = subtitle, size = DPTextSize.Small)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingContent != null) {
                trailingContent()
            } else if (value != null) {
                DPMono(
                    text = value,
                    size = DPTextSize.Small,
                    color = colours.textTertiary,
                )
            }
            if (showChevron && onClick != null) {
                Spacer(Modifier.width(spacing.xs))
                DPMono(
                    text = "›",
                    size = DPTextSize.Medium,
                    color = colours.textTertiary,
                )
            }
        }
    }
}
