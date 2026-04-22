@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ComponentOverrideApi::class)

package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.*

enum class DPDialogVariant { Standard, Destructive, Informational }

@Composable
fun DPAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DPDialogVariant = DPDialogVariant.Standard,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: ImageVector? = null,
    properties: DialogProperties = DialogProperties(),
    shape: Shape? = null,
    tonalElevation: DPElevationLevel = DPElevationLevel.Level3,
) {
    val colorScheme = MaterialTheme.colorScheme
    val resolvedIconTint = when (variant) {
        DPDialogVariant.Standard -> colorScheme.primary
        DPDialogVariant.Destructive -> colorScheme.error
        DPDialogVariant.Informational -> colorScheme.tertiary
    }
    val resolvedConfirmColor = when (variant) {
        DPDialogVariant.Standard -> colorScheme.primary
        DPDialogVariant.Destructive -> colorScheme.error
        DPDialogVariant.Informational -> colorScheme.tertiary
    }
    val iconSlot: @Composable (() -> Unit)? =
        if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = resolvedIconTint,
                )
            }
        } else {
            null
        }
    val dismissOnClick: () -> Unit = onDismiss ?: {}
    val dismissButtonSlot: @Composable (() -> Unit)? =
        if (dismissText != null) {
            val dText = dismissText
            {
                TextButton(onClick = dismissOnClick) { DPLabel(dText) }
            }
        } else {
            null
        }
    DPAlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                DPLabel(confirmText, color = resolvedConfirmColor)
            }
        },
        modifier = modifier,
        dismissButton = dismissButtonSlot,
        icon = iconSlot,
        title = { DPTitle(text = title, size = DPTextSize.Large) },
        text = { DPBody(text = message, size = DPTextSize.Medium) },
        shape = shape ?: AlertDialogDefaults.shape,
        containerColor = AlertDialogDefaults.containerColor,
        iconContentColor = AlertDialogDefaults.iconContentColor,
        titleContentColor = AlertDialogDefaults.titleContentColor,
        textContentColor = AlertDialogDefaults.textContentColor,
        tonalElevation = tonalElevation.value(),
        properties = properties,
    )
}

@Composable
private fun DPAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(),
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier,
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = text,
        shape = shape,
        containerColor = containerColor,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        tonalElevation = tonalElevation,
        properties = properties,
    )
}

@DPComponentPreview
@Composable
private fun DPAlertDialogTextFirstPreview() {
    Preview {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            data class Sample(val variant: DPDialogVariant, val label: String, val icon: ImageVector)

            listOf(
                Sample(DPDialogVariant.Standard, "Standard", Icons.Filled.Add),
                Sample(DPDialogVariant.Destructive, "Destructive", Icons.Filled.Delete),
                Sample(DPDialogVariant.Informational, "Informational", Icons.Filled.Info),
            ).forEach { (variant, label, vIcon) ->
                val colorScheme = MaterialTheme.colorScheme
                val resolvedIconTint = when (variant) {
                    DPDialogVariant.Standard -> colorScheme.primary
                    DPDialogVariant.Destructive -> colorScheme.error
                    DPDialogVariant.Informational -> colorScheme.tertiary
                }
                val resolvedConfirmColor = when (variant) {
                    DPDialogVariant.Standard -> colorScheme.primary
                    DPDialogVariant.Destructive -> colorScheme.error
                    DPDialogVariant.Informational -> colorScheme.tertiary
                }
                Surface(
                    shape = AlertDialogDefaults.shape,
                    tonalElevation = DPElevationLevel.Level3.value(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = vIcon,
                            contentDescription = null,
                            tint = resolvedIconTint,
                        )
                        DPTitle(text = label, size = DPTextSize.Large)
                        DPBody(text = "Sample message for this variant.", size = DPTextSize.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = {}) { DPLabel("Cancel") }
                            TextButton(onClick = {}) { DPLabel("OK", color = resolvedConfirmColor) }
                        }
                    }
                }
            }
        }
    }
}

