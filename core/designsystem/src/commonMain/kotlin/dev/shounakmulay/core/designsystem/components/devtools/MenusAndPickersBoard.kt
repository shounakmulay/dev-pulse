@file:OptIn(ExperimentalMaterial3Api::class)

package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DatePicker
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
import dev.shounakmulay.core.designsystem.components.DPButton
import dev.shounakmulay.core.designsystem.components.DPDropdownMenu
import dev.shounakmulay.core.designsystem.components.DPDropdownMenuItem
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant

@Composable
fun MenusAndPickersBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            DPTextView(text = "Dropdown Menu", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            var expanded by remember { mutableStateOf(false) }
            Box {
                DPButton(text = "Show Menu", onClick = { expanded = true })
                DPDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DPDropdownMenuItem(text = "Edit", onClick = { expanded = false })
                    DPDropdownMenuItem(text = "Duplicate", onClick = { expanded = false })
                    DPDropdownMenuItem(text = "Delete", onClick = { expanded = false })
                }
            }
        }

        item {
            DPTextView(text = "Date Picker", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            val dateState = rememberDatePickerState()
            DatePicker(state = dateState)
        }
    }
}
