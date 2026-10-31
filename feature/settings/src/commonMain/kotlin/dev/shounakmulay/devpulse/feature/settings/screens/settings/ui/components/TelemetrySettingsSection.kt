package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.telemetry.TelemetryConsentOptions
import devpulse.core.resources.generated.resources.telemetry_error
import devpulse.core.resources.generated.resources.telemetry_loading
import devpulse.core.resources.generated.resources.telemetry_retry
import devpulse.core.resources.generated.resources.telemetry_saving
import devpulse.core.resources.generated.resources.telemetry_settings_hint
import devpulse.core.resources.generated.resources.telemetry_settings_intro
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TelemetrySettingsSection(
    crashReporting: Boolean,
    performanceMetrics: Boolean,
    loaded: Boolean,
    saving: Boolean,
    error: Boolean,
    onCrashReportingChanged: (Boolean) -> Unit,
    onPerformanceMetricsChanged: (Boolean) -> Unit,
    onRetry: () -> Unit,
) {
    val spacing = LocalDPSpacing.current
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        DPTextView(
            modifier = Modifier.padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            text = stringResource(stringRes.telemetry_settings_intro),
            variant = DPTextViewVariant.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TelemetryConsentOptions(
            crashReporting = crashReporting,
            performanceMetrics = performanceMetrics,
            enabled = loaded && !saving,
            onCrashReportingChanged = onCrashReportingChanged,
            onPerformanceMetricsChanged = onPerformanceMetricsChanged,
        )
        when {
            error -> {
                DPTextView(
                    modifier = Modifier.padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
                    text = stringResource(stringRes.telemetry_error),
                    variant = DPTextViewVariant.BodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                if (!loaded) {
                    DPButton(
                        modifier = Modifier.padding(horizontal = spacing.screenHorizontal),
                        text = stringResource(stringRes.telemetry_retry),
                        style = DPButtonStyle.Text,
                        onClick = onRetry,
                    )
                }
            }

            !loaded || saving -> DPTextView(
                modifier = Modifier.padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
                text = stringResource(if (saving) stringRes.telemetry_saving else stringRes.telemetry_loading),
                variant = DPTextViewVariant.BodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DPTextView(
            modifier = Modifier.padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
            text = stringResource(stringRes.telemetry_settings_hint),
            variant = DPTextViewVariant.BodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
