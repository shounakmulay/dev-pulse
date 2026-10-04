package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.feed_search_clear
import org.jetbrains.compose.resources.stringResource

@Composable
fun DPSearchTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigationIcon: @Composable (Modifier) -> Unit = {},
    textValue: String,
    onTextValueChange: (String) -> Unit,
    placeholder: String,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    inputModifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TextFieldColors? = null,
) {
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = { navigationIcon(Modifier.padding(bottom = 6.dp)) },
        title = {
            DPTextField(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).then(inputModifier),
                value = textValue,
                onValueChange = onTextValueChange,
                variant = DPTextFieldVariant.Filled,
                placeholder = placeholder,
                enabled = enabled,
                colors = colors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                ),
                trailingIcon = {
                    if (textValue.isNotEmpty()) {
                        DPIconButton(
                            icon = DPIcons.Close,
                            contentDescription = stringResource(stringRes.feed_search_clear),
                            onClick = {
                                onTextValueChange("")
                            },
                        )
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
            )
        }
    )
}
