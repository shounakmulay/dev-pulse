@file:OptIn(ExperimentalMaterial3Api::class)

package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.DPIntent
import dev.shounakmulay.core.designsystem.theme.DPSize
import dev.shounakmulay.core.designsystem.theme.DPTheme
import dev.shounakmulay.core.designsystem.theme.colors
import dev.shounakmulay.core.designsystem.theme.iconSize
import dev.shounakmulay.core.designsystem.theme.monoFontFamily

/** Visual container style: [Filled] → Material [TextField], [Outlined] → [OutlinedTextField]. */
enum class DPTextFieldStyle { Outlined, Filled }

/** Font for field text. [Mono] merges [monoFontFamily] into the size-based [TextStyle]. */
enum class DPFontFamily { Sans, Mono }

private class DPTextFieldLayout(
    val textStyle: TextStyle,
    val colors: TextFieldColors,
    val shape: Shape,
    val interactionSource: MutableInteractionSource,
    val label: @Composable (() -> Unit)?,
    val placeholder: @Composable (() -> Unit)?,
    val supportingText: @Composable (() -> Unit)?,
    val leading: @Composable (() -> Unit)?,
    val trailing: @Composable (() -> Unit)?,
    val isError: Boolean,
)

@Composable
private fun dpTextFieldLayout(
    style: DPTextFieldStyle,
    intent: DPIntent,
    size: DPSize,
    fontFamily: DPFontFamily,
    isError: Boolean,
    label: String?,
    placeholder: String?,
    supportingText: String?,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    onTrailingIconClick: (() -> Unit)?,
    textStyle: TextStyle?,
    shape: Shape?,
    colors: TextFieldColors?,
    interactionSource: MutableInteractionSource?,
): DPTextFieldLayout {
    val effectiveIsError = isError || intent == DPIntent.Danger
    val baseTypography = when (size) {
        DPSize.Small -> MaterialTheme.typography.bodySmall
        DPSize.Medium -> MaterialTheme.typography.bodyMedium
        DPSize.Large -> MaterialTheme.typography.bodyLarge
    }
    val withFontFamily = when (fontFamily) {
        DPFontFamily.Sans -> baseTypography
        DPFontFamily.Mono -> baseTypography.merge(TextStyle(fontFamily = monoFontFamily()))
    }
    val resolvedTextStyle = textStyle?.let { withFontFamily.merge(it) } ?: withFontFamily

    val resolvedColors = when {
        colors != null -> colors
        effectiveIsError -> when (style) {
            DPTextFieldStyle.Filled -> TextFieldDefaults.colors()
            DPTextFieldStyle.Outlined -> OutlinedTextFieldDefaults.colors()
        }
        else -> {
            val ic = intent.colors()
            when (style) {
                DPTextFieldStyle.Filled -> TextFieldDefaults.colors(
                    focusedIndicatorColor = ic.accent,
                    focusedLabelColor = ic.accent,
                    cursorColor = ic.accent,
                )
                DPTextFieldStyle.Outlined -> OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ic.accent,
                    focusedLabelColor = ic.accent,
                    cursorColor = ic.accent,
                )
            }
        }
    }
    val resolvedShape = when {
        shape != null -> shape
        style == DPTextFieldStyle.Filled -> TextFieldDefaults.shape
        style == DPTextFieldStyle.Outlined -> OutlinedTextFieldDefaults.shape
        else -> TextFieldDefaults.shape
    }
    val iSource = interactionSource ?: remember { MutableInteractionSource() }
    val labelContent: @Composable (() -> Unit)? =
        if (label != null) {
            { Text(label) }
        } else {
            null
        }
    val placeholderContent: @Composable (() -> Unit)? =
        if (placeholder != null) {
            { Text(placeholder) }
        } else {
            null
        }
    val supportingTextContent: @Composable (() -> Unit)? =
        if (supportingText != null) {
            { Text(supportingText) }
        } else {
            null
        }
    val iconDim = size.iconSize()
    val leadingContent: @Composable (() -> Unit)? =
        if (leadingIcon != null) {
            { Icon(leadingIcon, null, Modifier.size(iconDim)) }
        } else {
            null
        }
    val trailingContent: @Composable (() -> Unit)? =
        if (trailingIcon != null) {
            if (onTrailingIconClick != null) {
                {
                    IconButton(onClick = onTrailingIconClick) {
                        Icon(trailingIcon, null, Modifier.size(iconDim))
                    }
                }
            } else {
                { Icon(trailingIcon, null, Modifier.size(iconDim)) }
            }
        } else {
            null
        }
    return DPTextFieldLayout(
        textStyle = resolvedTextStyle,
        colors = resolvedColors,
        shape = resolvedShape,
        interactionSource = iSource,
        label = labelContent,
        placeholder = placeholderContent,
        supportingText = supportingTextContent,
        leading = leadingContent,
        trailing = trailingContent,
        isError = effectiveIsError,
    )
}

@Composable
fun DPTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: DPTextFieldStyle = DPTextFieldStyle.Outlined,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    fontFamily: DPFontFamily = DPFontFamily.Sans,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    textStyle: TextStyle? = null,
    shape: Shape? = null,
    colors: TextFieldColors? = null,
) {
    val l = dpTextFieldLayout(
        style = style,
        intent = intent,
        size = size,
        fontFamily = fontFamily,
        isError = isError,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        onTrailingIconClick = onTrailingIconClick,
        textStyle = textStyle,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource,
    )
    when (style) {
        DPTextFieldStyle.Filled -> TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = l.textStyle,
            label = l.label,
            placeholder = l.placeholder,
            leadingIcon = l.leading,
            trailingIcon = l.trailing,
            prefix = null,
            suffix = null,
            supportingText = l.supportingText,
            isError = l.isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = l.interactionSource,
            shape = l.shape,
            colors = l.colors,
        )
        DPTextFieldStyle.Outlined -> OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = l.textStyle,
            label = l.label,
            placeholder = l.placeholder,
            leadingIcon = l.leading,
            trailingIcon = l.trailing,
            prefix = null,
            suffix = null,
            supportingText = l.supportingText,
            isError = l.isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = l.interactionSource,
            shape = l.shape,
            colors = l.colors,
        )
    }
}

@Composable
fun DPTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    style: DPTextFieldStyle = DPTextFieldStyle.Outlined,
    intent: DPIntent = DPIntent.Primary,
    size: DPSize = DPSize.Medium,
    fontFamily: DPFontFamily = DPFontFamily.Sans,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    textStyle: TextStyle? = null,
    shape: Shape? = null,
    colors: TextFieldColors? = null,
) {
    val l = dpTextFieldLayout(
        style = style,
        intent = intent,
        size = size,
        fontFamily = fontFamily,
        isError = isError,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        onTrailingIconClick = onTrailingIconClick,
        textStyle = textStyle,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource,
    )
    when (style) {
        DPTextFieldStyle.Filled -> TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = l.textStyle,
            label = l.label,
            placeholder = l.placeholder,
            leadingIcon = l.leading,
            trailingIcon = l.trailing,
            prefix = null,
            suffix = null,
            supportingText = l.supportingText,
            isError = l.isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = l.interactionSource,
            shape = l.shape,
            colors = l.colors,
        )
        DPTextFieldStyle.Outlined -> OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = l.textStyle,
            label = l.label,
            placeholder = l.placeholder,
            leadingIcon = l.leading,
            trailingIcon = l.trailing,
            prefix = null,
            suffix = null,
            supportingText = l.supportingText,
            isError = l.isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = l.interactionSource,
            shape = l.shape,
            colors = l.colors,
        )
    }
}

@DPComponentPreview
@Composable
private fun DPTextFieldVariantsPreview() {
    Preview {
        var v by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.sm),
        ) {
            DPTextField(value = v, onValueChange = { v = it }, label = "Outlined / Primary")
            DPTextField(
                value = v,
                onValueChange = { v = it },
                style = DPTextFieldStyle.Filled,
                intent = DPIntent.Success,
                label = "Filled / Success",
            )
            DPTextField(
                value = v,
                onValueChange = { v = it },
                intent = DPIntent.Danger,
                isError = true,
                label = "Error",
                supportingText = "Invalid input",
            )
            DPTextField(
                value = v,
                onValueChange = { v = it },
                fontFamily = DPFontFamily.Mono,
                label = "Mono",
            )
        }
    }
}
