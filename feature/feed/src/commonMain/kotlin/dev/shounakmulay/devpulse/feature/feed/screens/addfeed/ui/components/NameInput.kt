package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextField
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.custom_name
import devpulse.core.resources.generated.resources.name_optional
import org.jetbrains.compose.resources.stringResource

@Composable
fun NameInput(
    title: String,
    onTitleChanged: (String) -> Unit
) {
    FormFieldTitle(text = stringResource(stringRes.name_optional))

    DPTextField(
        modifier = Modifier.fillMaxWidth(),
        placeholder = stringResource(stringRes.custom_name),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                defaultKeyboardAction(ImeAction.Done)
            }
        ),
        value = title,
        onValueChange = onTitleChanged,
    )
}
