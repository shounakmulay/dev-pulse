package dev.shounakmulay.devpulse.core.ui.telemetry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPSwitch
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.telemetry_crash_description
import devpulse.core.resources.generated.resources.telemetry_crash_title
import devpulse.core.resources.generated.resources.telemetry_performance_description
import devpulse.core.resources.generated.resources.telemetry_performance_title
import devpulse.core.resources.generated.resources.telemetry_privacy_policy
import devpulse.core.resources.generated.resources.telemetry_restart_notice
import org.jetbrains.compose.resources.stringResource

@Composable
fun TelemetryConsentOptions(
    crashReporting: Boolean,
    performanceMetrics: Boolean,
    enabled: Boolean,
    onCrashReportingChanged: (Boolean) -> Unit,
    onPerformanceMetricsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalDPSpacing.current
    val uriHandler = LocalUriHandler.current
    Column(modifier = modifier.fillMaxWidth()) {
        TelemetryConsentToggle(
            title = stringResource(stringRes.telemetry_crash_title),
            description = stringResource(stringRes.telemetry_crash_description),
            checked = crashReporting,
            enabled = enabled,
            onCheckedChange = onCrashReportingChanged,
        )
        TelemetryConsentToggle(
            title = stringResource(stringRes.telemetry_performance_title),
            description = stringResource(stringRes.telemetry_performance_description),
            checked = performanceMetrics,
            enabled = enabled,
            onCheckedChange = onPerformanceMetricsChanged,
        )
        DPTextView(
            text = stringResource(stringRes.telemetry_restart_notice),
            variant = DPTextViewVariant.BodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
        )
        DPListItem(
            headlineText = stringResource(stringRes.telemetry_privacy_policy),
            contentPadding = PaddingValues(horizontal = spacing.screenHorizontal, vertical = spacing.md),
            onClick = { uriHandler.openUri("https://devpulse.shounakmulay.dev/privacy/") },
            trailingContent = {
                Icon(
                    imageVector = DPIcons.ExternalLink,
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
fun TelemetryConsentToggle(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val spacing = LocalDPSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = spacing.touchTarget)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            DPTextView(text = title, variant = DPTextViewVariant.BodyLarge)
            DPTextView(
                text = description,
                variant = DPTextViewVariant.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DPSwitch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}
