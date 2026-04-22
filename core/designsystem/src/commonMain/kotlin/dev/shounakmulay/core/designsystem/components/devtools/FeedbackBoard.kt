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
import dev.shounakmulay.core.designsystem.components.DPBody
import dev.shounakmulay.core.designsystem.components.DPCircularProgressIndicator
import dev.shounakmulay.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.core.designsystem.components.DPSnackbar
import dev.shounakmulay.core.designsystem.components.DPSnackbarVariant
import dev.shounakmulay.core.designsystem.theme.DPIntent

@Composable
fun FeedbackBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            DPBody(text = "Linear Progress — Indeterminate")
            Spacer(Modifier.height(8.dp))
            DPLinearProgressIndicator(Modifier.fillMaxWidth())
        }

        item {
            DPBody(text = "Linear Progress — 60%")
            Spacer(Modifier.height(8.dp))
            DPLinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth())
        }

        item {
            DPBody(text = "Linear Progress — intent samples")
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
            DPBody(text = "Circular Progress — Indeterminate")
            Spacer(Modifier.height(8.dp))
            DPCircularProgressIndicator()
        }

        item {
            DPBody(text = "Circular Progress — 75%")
            Spacer(Modifier.height(8.dp))
            DPCircularProgressIndicator(progress = { 0.75f })
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DPBody(text = "Indeterminate + Determinate")
                DPCircularProgressIndicator()
                DPCircularProgressIndicator(progress = { 0.4f })
            }
        }

        item { DPBody(text = "Snackbar – variants") }
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
            DPBody(text = "Snackbar – action + dismiss")
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
