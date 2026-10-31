package dev.shounakmulay.devpulse.feature.settings.screens.telemetry.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.TelemetrySettingsSection
import devpulse.core.resources.generated.resources.telemetry_settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TelemetrySettingsScreen(viewModel: TelemetrySettingsViewModel, navigator: Navigator) {
    Screen(
        viewModel = viewModel,
        onEffect = viewModel::unhandledEffect,
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.telemetry_settings_title),
                navigationIcon = { DPBackNavigationIconButton(onNavigateBack = navigator::navigateBack) },
            )
        },
    ) { state ->
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(bottom = LocalDPSpacing.current.sectionGap),
        ) {
            TelemetrySettingsSection(
                crashReporting = state.consent.crashReporting,
                performanceMetrics = state.consent.usageAnalytics,
                loaded = state.loaded,
                saving = state.saving,
                error = state.error,
                onCrashReportingChanged = { viewModel.onEvent(TelemetrySettingsEvent.SetCrashReporting(it)) },
                onPerformanceMetricsChanged = { viewModel.onEvent(TelemetrySettingsEvent.SetAnalytics(it)) },
                onRetry = { viewModel.onEvent(TelemetrySettingsEvent.Retry) },
            )
        }
    }
}
