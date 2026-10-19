package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.domain.settings.privacy.TelemetryPrivacyState
import dev.shounakmulay.devpulse.core.domain.settings.privacy.TelemetryPrivacyStatus
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.privacy_analytics
import devpulse.core.resources.generated.resources.privacy_analytics_description
import devpulse.core.resources.generated.resources.privacy_apply_failed
import devpulse.core.resources.generated.resources.privacy_description
import devpulse.core.resources.generated.resources.privacy_diagnostics
import devpulse.core.resources.generated.resources.privacy_diagnostics_description
import devpulse.core.resources.generated.resources.privacy_heading
import devpulse.core.resources.generated.resources.privacy_load_failed
import devpulse.core.resources.generated.resources.privacy_loading
import devpulse.core.resources.generated.resources.privacy_retry
import devpulse.core.resources.generated.resources.privacy_save_failed
import devpulse.core.resources.generated.resources.privacy_saving
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PrivacySettingsSection(
    state: TelemetryPrivacyState,
    onAnalyticsChanged: (Boolean) -> Unit,
    onDiagnosticsChanged: (Boolean) -> Unit,
    onRetry: () -> Unit,
) {
    Column {
        SettingsSectionHeading(title = stringResource(stringRes.privacy_heading))
        DPTextView(
            text = stringResource(stringRes.privacy_description),
            variant = DPTextViewVariant.BodyMedium,
            modifier = Modifier.padding(horizontal = DPTheme.spacing.md),
        )
        SettingsToggle(
            checked = state.analytics,
            headlineText = stringResource(stringRes.privacy_analytics),
            supportingText = stringResource(stringRes.privacy_analytics_description),
            enabled = state.canChangeConsent || (state.analytics && state.canWithdrawConsent),
            onClick = onAnalyticsChanged,
        )
        SettingsToggle(
            checked = state.diagnostics,
            headlineText = stringResource(stringRes.privacy_diagnostics),
            supportingText = stringResource(stringRes.privacy_diagnostics_description),
            enabled = state.canChangeConsent || (state.diagnostics && state.canWithdrawConsent),
            onClick = onDiagnosticsChanged,
        )
        val message = when (state.status) {
            TelemetryPrivacyStatus.Loading -> stringRes.privacy_loading
            TelemetryPrivacyStatus.Saving -> stringRes.privacy_saving
            TelemetryPrivacyStatus.LoadFailed -> stringRes.privacy_load_failed
            TelemetryPrivacyStatus.SaveFailed -> stringRes.privacy_save_failed
            TelemetryPrivacyStatus.ApplyFailed -> stringRes.privacy_apply_failed
            TelemetryPrivacyStatus.Ready -> null
        }
        if (message != null) {
            DPTextView(
                text = stringResource(message),
                variant = DPTextViewVariant.BodyMedium,
                modifier = Modifier.padding(horizontal = DPTheme.spacing.md),
            )
        }
        when (state.status) {
            TelemetryPrivacyStatus.LoadFailed,
            TelemetryPrivacyStatus.SaveFailed,
            TelemetryPrivacyStatus.ApplyFailed -> DPButton(
                text = stringResource(stringRes.privacy_retry),
                style = DPButtonStyle.Text,
                modifier = Modifier.padding(horizontal = DPTheme.spacing.md),
                onClick = onRetry,
            )
            else -> Unit
        }
    }
}
