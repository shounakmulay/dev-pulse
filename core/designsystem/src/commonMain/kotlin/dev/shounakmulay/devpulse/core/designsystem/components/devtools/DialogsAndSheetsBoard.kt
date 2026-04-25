package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPAlertDialog
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPDialogVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogsAndSheetsBoard(modifier: Modifier = Modifier) {
    var showStandardDialog by remember { mutableStateOf(false) }
    var showDestructiveDialog by remember { mutableStateOf(false) }
    var showInformationalDialog by remember { mutableStateOf(false) }
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
            DPTextView(text = "Alert Dialog – Standard", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPButton(text = "Show Standard Dialog", onClick = { showStandardDialog = true })
        }

        item {
            DPTextView(text = "Alert Dialog – Destructive", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPButton(text = "Show Destructive Dialog", onClick = { showDestructiveDialog = true })
        }

        item {
            DPTextView(text = "Alert Dialog – Informational", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPButton(text = "Show Informational Dialog", onClick = { showInformationalDialog = true })
        }

        item {
            DPTextView(text = "Modal Bottom Sheet", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPButton(text = "Show Modal Bottom Sheet", onClick = { showModalSheet = true })
        }

        item {
            DPTextView(text = "Theme Switcher Sheet", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPButton(text = "Show Theme Switcher Sheet", onClick = { showThemeSheet = true })
        }
    }

    if (showStandardDialog) {
        DPAlertDialog(
            title = "Confirm action",
            message = "Are you sure you want to proceed? This action cannot be undone.",
            confirmText = "Confirm",
            onConfirm = { showStandardDialog = false },
            onDismissRequest = { showStandardDialog = false },
            variant = DPDialogVariant.Standard,
            dismissText = "Cancel",
            onDismiss = { showStandardDialog = false },
            icon = Icons.Default.Info,
        )
    }

    if (showDestructiveDialog) {
        DPAlertDialog(
            title = "Delete everything?",
            message = "You are about to delete all data. This is irreversible.",
            confirmText = "Delete",
            onConfirm = { showDestructiveDialog = false },
            onDismissRequest = { showDestructiveDialog = false },
            variant = DPDialogVariant.Destructive,
            dismissText = "Keep",
            onDismiss = { showDestructiveDialog = false },
            icon = Icons.Default.Delete,
        )
    }

    if (showInformationalDialog) {
        DPAlertDialog(
            title = "Heads up",
            message = "This is an informational dialog. No destructive action here.",
            confirmText = "Got it",
            onConfirm = { showInformationalDialog = false },
            onDismissRequest = { showInformationalDialog = false },
            variant = DPDialogVariant.Informational,
            icon = Icons.Default.Warning,
        )
    }

    if (showModalSheet) {
        DPModalBottomSheet(
            onDismissRequest = { showModalSheet = false },
            sheetState = sheetState,
        ) {
            DPTextView(
                text = "This is a DPModalBottomSheet. Swipe down or tap outside to dismiss.",
                variant = DPTextViewVariant.BodyMedium,
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
