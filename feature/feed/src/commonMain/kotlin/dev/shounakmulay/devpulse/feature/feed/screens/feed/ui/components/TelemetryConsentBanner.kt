package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPCard
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.telemetry.TelemetryConsentToggle
import devpulse.core.resources.generated.resources.telemetry_callout_summary
import devpulse.core.resources.generated.resources.telemetry_help_title
import devpulse.core.resources.generated.resources.telemetry_saving
import org.jetbrains.compose.resources.stringResource

@Composable
fun TelemetryConsentBanner(
    saving: Boolean,
    onAnalyticsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    DPCard(
        modifier = modifier.fillMaxWidth().padding(horizontal = LocalDPSpacing.current.screenHorizontal),
    ) {
        TelemetryConsentToggle(
            title = stringResource(stringRes.telemetry_help_title),
            description = stringResource(if (saving) stringRes.telemetry_saving else stringRes.telemetry_callout_summary),
            checked = false,
            enabled = !saving,
            onCheckedChange = onAnalyticsChanged,
        )
    }
}
