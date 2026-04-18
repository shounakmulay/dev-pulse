@file:OptIn(ExperimentalMaterial3Api::class)

package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPButton
import dev.shounakmulay.core.designsystem.components.DPDatePicker
import dev.shounakmulay.core.designsystem.components.DPDropdownMenu
import dev.shounakmulay.core.designsystem.components.DPDropdownMenuItem

@Composable
fun MenusAndPickersBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            DPBody(text = "Dropdown Menu")
            Spacer(Modifier.height(8.dp))
            var expanded by remember { mutableStateOf(false) }
            Box {
                DPButton(onClick = { expanded = true }) {
                    Text("Show Menu")
                }
                DPDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DPDropdownMenuItem(text = { Text("Edit") }, onClick = { expanded = false })
                    DPDropdownMenuItem(text = { Text("Duplicate") }, onClick = { expanded = false })
                    DPDropdownMenuItem(text = { Text("Delete") }, onClick = { expanded = false })
                }
            }
        }

        item {
            DPBody(text = "Date Picker")
            Spacer(Modifier.height(8.dp))
            val dateState = rememberDatePickerState()
            DPDatePicker(state = dateState)
        }
    }
}
