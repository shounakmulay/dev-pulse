package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPFilledTextField
import dev.shounakmulay.core.designsystem.components.DPMonoTextField
import dev.shounakmulay.core.designsystem.components.DPOutlinedTextField

@Composable
fun TextFieldsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPBody(text = "DPFilledTextField") }
        item {
            var text by remember { mutableStateOf("") }
            DPFilledTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Label") },
                placeholder = { Text("Placeholder") },
            )
        }
        item {
            DPFilledTextField(
                value = "Disabled",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                label = { Text("Disabled") },
            )
        }

        item { DPBody(text = "DPOutlinedTextField") }
        item {
            var text by remember { mutableStateOf("") }
            DPOutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Label") },
                placeholder = { Text("Type here…") },
            )
        }
        item {
            DPOutlinedTextField(
                value = "Read-only",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Read Only") },
            )
        }

        item { DPBody(text = "DPMonoTextField") }
        item {
            var code by remember { mutableStateOf("") }
            DPMonoTextField(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Code") },
                placeholder = { Text("Enter code…") },
                singleLine = true,
            )
        }
    }
}
