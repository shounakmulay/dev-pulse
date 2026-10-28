package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.ui.bottomsheet.rememberDPModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.telemetry.TelemetryConsentOptions
import devpulse.core.resources.generated.resources.telemetry_callout_summary
import devpulse.core.resources.generated.resources.telemetry_change_hint
import devpulse.core.resources.generated.resources.telemetry_decline
import devpulse.core.resources.generated.resources.telemetry_help_title
import devpulse.core.resources.generated.resources.telemetry_intro
import devpulse.core.resources.generated.resources.telemetry_save
import devpulse.core.resources.generated.resources.telemetry_saving
import devpulse.core.resources.generated.resources.telemetry_settings_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelemetryConsentBanner(
    saving: Boolean,
    onSave: (crashReporting: Boolean, performanceMetrics: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val controller = rememberDPModalBottomSheetController(skipPartiallyExpanded = true)
    val spacing = LocalDPSpacing.current
    Column(modifier = modifier.fillMaxWidth()) {
        DPListItem(
            headlineText = stringResource(stringRes.telemetry_help_title),
            supportingText = stringResource(stringRes.telemetry_callout_summary),
            enabled = !saving,
            onClick = controller::show,
            trailingContent = { Icon(imageVector = DPIcons.ChevronRight, contentDescription = null) },
        )
    }
    DPModalBottomSheet(controller = controller) {
        var crashReporting by rememberSaveable { mutableStateOf(false) }
        var performanceMetrics by rememberSaveable { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = spacing.lg, end = spacing.lg, bottom = spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            DPTextView(
                text = stringResource(stringRes.telemetry_settings_title),
                variant = DPTextViewVariant.TitleLarge,
                modifier = Modifier.semantics { heading() },
            )
            DPTextView(
                text = stringResource(stringRes.telemetry_intro),
                variant = DPTextViewVariant.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TelemetryConsentOptions(
                crashReporting = crashReporting,
                performanceMetrics = performanceMetrics,
                enabled = !saving,
                onCrashReportingChanged = { crashReporting = it },
                onPerformanceMetricsChanged = { performanceMetrics = it },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                DPButton(
                    text = stringResource(stringRes.telemetry_decline),
                    modifier = Modifier.weight(1f),
                    style = DPButtonStyle.Outlined,
                    enabled = !saving,
                    onClick = { onSave(false, false) },
                )
                DPButton(
                    text = stringResource(if (saving) stringRes.telemetry_saving else stringRes.telemetry_save),
                    modifier = Modifier.weight(1f),
                    style = DPButtonStyle.Outlined,
                    enabled = !saving,
                    onClick = { onSave(crashReporting, performanceMetrics) },
                )
            }
            DPTextView(
                text = stringResource(stringRes.telemetry_change_hint),
                variant = DPTextViewVariant.BodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
