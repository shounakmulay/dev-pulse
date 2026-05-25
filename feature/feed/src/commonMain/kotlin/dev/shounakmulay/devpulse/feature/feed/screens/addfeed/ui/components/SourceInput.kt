package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextField
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIAddFeedData
import devpulse.core.resources.generated.resources.enter_valid_url
import devpulse.core.resources.generated.resources.source
import devpulse.core.resources.generated.resources.url
import org.jetbrains.compose.resources.stringResource

@Composable
fun SourceInput(
    url: String,
    onUrlChanged: (String) -> Unit,
    onFocusLost: () -> Unit,
    error: UIAddFeedData.ValidationError?
) {

    var wasFocused by remember { mutableStateOf(false) }
    val errorText = remember(error) {
        when (error) {
            UIAddFeedData.ValidationError.INVALID_SOURCE_URL -> stringRes.enter_valid_url
            UIAddFeedData.ValidationError.INVALID_RSS_URL -> TODO()
            null -> null
        }
    }

    FormFieldTitle(text = stringResource(stringRes.source))

    DPTextField(
        modifier = Modifier.fillMaxWidth()
            .onFocusChanged { focusState ->
            if (focusState.isFocused) {
                wasFocused = true
            } else if (wasFocused) {
                wasFocused = false
                onFocusLost()
            }
        },
        supportingText = if (errorText != null) stringResource(errorText) else null,
        isError = error != null,
        placeholder = stringResource(stringRes.url),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            capitalization = KeyboardCapitalization.None
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                defaultKeyboardAction(ImeAction.Next)
            }
        ),
        value = url,
        onValueChange = onUrlChanged,
    )
}

