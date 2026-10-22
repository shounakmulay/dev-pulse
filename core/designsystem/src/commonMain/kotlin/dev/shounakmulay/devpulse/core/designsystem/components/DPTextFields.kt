package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview

enum class DPTextFieldVariant { Outlined, Filled }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: DPTextFieldVariant = DPTextFieldVariant.Outlined,
    textVariant: DPTextViewVariant = DPTextViewVariant.BodyLarge,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = MaterialTheme.shapes.large,
    colors: TextFieldColors? = null,
    textStyle: TextStyle? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val baseTextStyle = textVariant.textStyle()
    val resolvedTextStyle = textStyle?.let { baseTextStyle.merge(it) } ?: baseTextStyle
    val labelContent: (@Composable () -> Unit)? =
        label?.let { labelText ->
            { DPTextView(text = labelText, variant = DPTextViewVariant.LabelMedium, maxLines = 1) }
        }
    val placeholderContent: (@Composable () -> Unit)? =
        placeholder?.let { placeholderText ->
            {
                DPTextView(
                    text = placeholderText,
                    variant = textVariant,
                    maxLines = maxLines,
                    minLines = minLines,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    val supportingTextContent: (@Composable () -> Unit)? =
        supportingText?.let { supportingText ->
            { DPTextView(text = supportingText, variant = DPTextViewVariant.BodySmall) }
        }

    when (variant) {
        DPTextFieldVariant.Outlined -> OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = resolvedTextStyle,
            label = labelContent,
            placeholder = placeholderContent,
            supportingText = supportingTextContent,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = shape,
            colors = colors ?: OutlinedTextFieldDefaults.colors(),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )

        DPTextFieldVariant.Filled -> TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = resolvedTextStyle,
            label = labelContent,
            placeholder = placeholderContent,
            supportingText = supportingTextContent,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = shape,
            colors = colors ?: TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
            ),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
    }
}

@DPComponentPreview
@Composable
private fun DPTextFieldVariantsPreview() {
    Preview {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DPTextFieldVariant.entries.forEach { variant ->
                DPTextField(
                    value = "DevPulse",
                    onValueChange = {},
                    variant = variant,
                    label = "Feed name",
                )
                DPTextField(
                    value = "invalid-url",
                    onValueChange = {},
                    variant = variant,
                    label = "Feed URL",
                    supportingText = "Enter a valid feed URL",
                    isError = true,
                )
                DPTextField(
                    value = "",
                    onValueChange = {},
                    variant = variant,
                    placeholder = "Feed URL",
                    enabled = false,
                )
            }
        }
    }
}

@DPComponentPreview
@Composable
private fun DPTextFieldLargeTextPreview() {
    Preview {
        CompositionLocalProvider(
            LocalDensity provides Density(density = LocalDensity.current.density, fontScale = 2f),
        ) {
            Column(
                modifier = Modifier.width(320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DPTextFieldVariant.entries.forEach { variant ->
                    DPTextField(
                        value = "invalid-url",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        variant = variant,
                        label = "The complete address of the feed you want to follow",
                        supportingText = "Enter a valid feed address that starts with https:// and try again.",
                        isError = true,
                    )
                    DPTextField(
                        value = "A longer feed name",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        variant = variant,
                        label = "The name shown in your feed list",
                        supportingText = "You can change this name later.",
                        enabled = false,
                    )
                }
            }
        }
    }
}
