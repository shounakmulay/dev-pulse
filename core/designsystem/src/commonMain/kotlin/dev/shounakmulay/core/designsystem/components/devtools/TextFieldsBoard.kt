package dev.shounakmulay.core.designsystem.components.devtools

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
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPFontFamily
import dev.shounakmulay.core.designsystem.components.DPTextField
import dev.shounakmulay.core.designsystem.components.DPTextFieldStyle
import dev.shounakmulay.core.designsystem.theme.DPIntent

@Composable
fun TextFieldsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPBody(text = "DPTextField – Outlined") }
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

        item { DPBody(text = "DPTextField – Filled") }
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

        item { DPBody(text = "DPTextField – Disabled") }
        item {
            DPTextField(
                value = "Disabled",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                label = "Disabled",
            )
        }

        item { DPBody(text = "DPTextField – Mono") }
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

        item { DPBody(text = "DPTextField – Intent samples") }
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
