package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPCircularProgressIndicator
import dev.shounakmulay.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.core.designsystem.components.DPSnackbar
import dev.shounakmulay.core.designsystem.components.DPSnackbarVariant
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.core.designsystem.theme.DPIntent

@Composable
fun FeedbackBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            DPTextView(text = "Linear Progress — Indeterminate", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPLinearProgressIndicator(Modifier.fillMaxWidth())
        }

        item {
            DPTextView(text = "Linear Progress — 60%", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPLinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth())
        }

        item {
            DPTextView(text = "Linear Progress — intent samples", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            listOf(DPIntent.Primary, DPIntent.Success, DPIntent.Warning, DPIntent.Danger)
                .forEach { intent ->
                    DPLinearProgressIndicator(
                        progress = { 0.5f },
                        intent = intent,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                }
        }

        item {
            DPTextView(text = "Circular Progress — Indeterminate", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPCircularProgressIndicator()
        }

        item {
            DPTextView(text = "Circular Progress — 75%", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPCircularProgressIndicator(progress = { 0.75f })
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DPTextView(text = "Indeterminate + Determinate", variant = DPTextViewVariant.BodyMedium)
                DPCircularProgressIndicator()
                DPCircularProgressIndicator(progress = { 0.4f })
            }
        }

        item { DPTextView(text = "Snackbar – variants", variant = DPTextViewVariant.BodyMedium) }
        DPSnackbarVariant.entries.forEach { variant ->
            item {
                DPSnackbar(
                    message = "Variant: $variant",
                    variant = variant,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            DPTextView(text = "Snackbar – action + dismiss", variant = DPTextViewVariant.BodyMedium)
            Spacer(Modifier.height(8.dp))
            DPSnackbar(
                message = "Network error occurred",
                variant = DPSnackbarVariant.Danger,
                actionLabel = "Retry",
                onAction = {},
                dismissLabel = "Dismiss",
                onDismiss = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
