package dev.shounakmulay.devpulse.core.designsystem.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation

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
    shape: Shape = MaterialTheme.shapes.extraExtraLarge,
    colors: TextFieldColors? = null,
    textStyle: TextStyle? = null,
) {
    val baseTextStyle = textVariant.textStyle()
    val resolvedTextStyle = textStyle?.let { baseTextStyle.merge(it) } ?: baseTextStyle
    val labelContent: (@Composable () -> Unit)? =
        label?.let { labelText ->
            { DPTextView(text = labelText, variant = DPTextViewVariant.LabelMedium) }
        }
    val placeholderContent: (@Composable () -> Unit)? =
        placeholder?.let { placeholderText ->
            { DPTextView(text = placeholderText, variant = textVariant) }
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
                errorIndicatorColor = Color.Transparent,
            ),
        )
    }
}
