package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPAlertDialog
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPButton
import dev.shounakmulay.core.designsystem.components.DPModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogsAndSheetsBoard(modifier: Modifier = Modifier) {
    var showBasicDialog by remember { mutableStateOf(false) }
    var showIconDialog by remember { mutableStateOf(false) }
    var showModalSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf<ThemeMode>(ThemeMode.Light) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            DPBody(text = "Alert Dialog")
            Spacer(Modifier.height(8.dp))
            DPButton(onClick = { showBasicDialog = true }) {
                Text("Show Alert Dialog")
            }
        }

        item {
            DPBody(text = "Alert Dialog with Icon")
            Spacer(Modifier.height(8.dp))
            DPButton(onClick = { showIconDialog = true }) {
                Text("Show Icon Dialog")
            }
        }

        item {
            DPBody(text = "Modal Bottom Sheet")
            Spacer(Modifier.height(8.dp))
            DPButton(onClick = { showModalSheet = true }) {
                Text("Show Modal Bottom Sheet")
            }
        }

        item {
            DPBody(text = "Theme Switcher Sheet")
            Spacer(Modifier.height(8.dp))
            DPButton(onClick = { showThemeSheet = true }) {
                Text("Show Theme Switcher Sheet")
            }
        }
    }

    if (showBasicDialog) {
        DPAlertDialog(
            onDismissRequest = { showBasicDialog = false },
            title = { Text("Confirm Action") },
            text = { Text("Are you sure you want to proceed? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showBasicDialog = false }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showBasicDialog = false }) { Text("Cancel") }
            },
        )
    }

    if (showIconDialog) {
        DPAlertDialog(
            onDismissRequest = { showIconDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Warning") },
            text = { Text("You are about to delete all data. This is irreversible.") },
            confirmButton = {
                TextButton(onClick = { showIconDialog = false }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showIconDialog = false }) { Text("Keep") }
            },
        )
    }

    if (showModalSheet) {
        DPModalBottomSheet(
            onDismissRequest = { showModalSheet = false },
            sheetState = sheetState,
        ) {
            DPBody(
                text = "This is a DPModalBottomSheet. Swipe down or tap outside to dismiss.",
                modifier = Modifier.fillMaxSize(),
            )
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showThemeSheet) {
        DPModalBottomSheet(
            onDismissRequest = { showThemeSheet = false },
            sheetState = sheetState,
        ) {
            ThemeSwitcherSheetContent(
                selected = selectedTheme,
                onSelect = { selectedTheme = it },
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
