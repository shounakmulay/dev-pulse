package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPButton
import dev.shounakmulay.core.designsystem.components.DPElevatedButton
import dev.shounakmulay.core.designsystem.components.DPFilledTonalButton
import dev.shounakmulay.core.designsystem.components.DPOutlinedButton
import dev.shounakmulay.core.designsystem.components.DPTextButton

@Composable
fun ButtonsBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { DPBody(text = "DPButton") }
        item { DPButton(onClick = {}) { Text("Enabled") } }
        item { DPButton(onClick = {}, enabled = false) { Text("Disabled") } }

        item { DPBody(text = "DPElevatedButton") }
        item { DPElevatedButton(onClick = {}) { Text("Enabled") } }
        item { DPElevatedButton(onClick = {}, enabled = false) { Text("Disabled") } }

        item { DPBody(text = "DPFilledTonalButton") }
        item { DPFilledTonalButton(onClick = {}) { Text("Enabled") } }
        item { DPFilledTonalButton(onClick = {}, enabled = false) { Text("Disabled") } }

        item { DPBody(text = "DPOutlinedButton") }
        item { DPOutlinedButton(onClick = {}) { Text("Enabled") } }
        item { DPOutlinedButton(onClick = {}, enabled = false) { Text("Disabled") } }

        item { DPBody(text = "DPTextButton") }
        item { DPTextButton(onClick = {}) { Text("Enabled") } }
        item { DPTextButton(onClick = {}, enabled = false) { Text("Disabled") } }
    }
}
