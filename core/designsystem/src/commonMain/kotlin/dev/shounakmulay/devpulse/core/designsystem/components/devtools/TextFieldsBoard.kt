package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPFontFamily
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextField
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextFieldStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.DPIntent

@Composable
fun TextFieldsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPTextView(text = "DPTextField – Outlined", variant = DPTextViewVariant.BodyMedium) }
        item {
            var text by remember { mutableStateOf("") }
            DPTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Label",
                placeholder = "Type here…",
            )
        }

        item { DPTextView(text = "DPTextField – Filled", variant = DPTextViewVariant.BodyMedium) }
        item {
            var text by remember { mutableStateOf("") }
            DPTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                style = DPTextFieldStyle.Filled,
                label = "Label",
                placeholder = "Placeholder",
            )
        }

        item { DPTextView(text = "DPTextField – Disabled", variant = DPTextViewVariant.BodyMedium) }
        item {
            DPTextField(
                value = "Disabled",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                label = "Disabled",
            )
        }

        item { DPTextView(text = "DPTextField – Mono", variant = DPTextViewVariant.BodyMedium) }
        item {
            var code by remember { mutableStateOf("") }
            DPTextField(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier.fillMaxWidth(),
                fontFamily = DPFontFamily.Mono,
                label = "Code",
                placeholder = "Enter code…",
                singleLine = true,
            )
        }

        item { DPTextView(text = "DPTextField – Intent samples", variant = DPTextViewVariant.BodyMedium) }
        listOf(DPIntent.Primary, DPIntent.Success, DPIntent.Danger).forEach { intent ->
            item {
                var text by remember { mutableStateOf("") }
                DPTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    intent = intent,
                    label = intent.name,
                    isError = intent == DPIntent.Danger,
                )
            }
        }
    }
}
