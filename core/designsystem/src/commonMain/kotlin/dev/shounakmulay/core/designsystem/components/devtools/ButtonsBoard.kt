package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPButton
import dev.shounakmulay.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.core.designsystem.theme.DPIntent

@Composable
fun ButtonsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DPButtonStyle.entries.forEach { style ->
            item { DPBody(text = style.name) }
            item { DPButton(text = "Enabled", onClick = {}, style = style) }
            item { DPButton(text = "Disabled", onClick = {}, style = style, enabled = false) }
        }

        item { DPBody(text = "Intent samples (Filled)") }
        DPIntent.entries.forEach { intent ->
            item { DPButton(text = intent.name, onClick = {}, intent = intent) }
        }
    }
}
