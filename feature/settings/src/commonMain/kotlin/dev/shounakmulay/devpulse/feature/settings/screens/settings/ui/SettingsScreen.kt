package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import dev.shounakmulay.devpulse.core.common.BuildKonfigCommon
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.AppearanceSettingsSection
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.DeveloperSettingsSection
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.OthersSection
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.SettingsSectionHeading
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.SettingsSubPageLink
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.SettingsToggle
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.TelemetrySettingsSection
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.ThemeSettingsSection
import devpulse.core.resources.generated.resources.data_and_sync
import devpulse.core.resources.generated.resources.import_feeds
import devpulse.core.resources.generated.resources.settings
import devpulse.core.resources.generated.resources.sync_in_background
import devpulse.core.resources.generated.resources.update_feeds_in_background
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navigator: Navigator) {
    val uriHandler = LocalUriHandler.current
    Screen(
        viewModel = viewModel,
        onEffect = {
            when (it) {
                SettingsScreenEffect.NavigateToDesignSystemBoard -> navigator.navigate(Screen.DeveloperTools.DesignSystemBoard)
                SettingsScreenEffect.NavigateToLicenses -> navigator.navigate(Screen.AboutLibs)
                SettingsScreenEffect.NavigateToArticleSettings -> navigator.navigate(Screen.Settings.ArticleSettings)
                else -> viewModel.unhandledEffect(it)
            }
        },
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.settings),
                navigationIcon = {
                    DPBackNavigationIconButton(onNavigateBack = navigator::navigateBack)
                },
            )
        },
    ) { state ->
        val canToggleBlackMode = state.canToggleBlackMode(isDarkTheme = DPTheme.isDarkTheme)

        val spacing = LocalDPSpacing.current
        LazyColumn(
            contentPadding = PaddingValues(top = spacing.sm, bottom = spacing.sectionGap),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            item(key = "theme") {
                ThemeSettingsSection(
                    selectedThemeMode = state.themeMode,
                    isBlackMode = state.isBlackMode,
                    canToggleBlackMode = canToggleBlackMode,
                    onThemeModeSelected = { value ->
                        viewModel.onEvent(SettingsScreenEvent.OnThemeModeSelected(value))
                    },
                    onBlackModeToggled = { value ->
                        viewModel.onEvent(SettingsScreenEvent.OnBlackModeToggled(value))
                    },
                )
            }
            item(key = "appearance") {
                AppearanceSettingsSection(
                    selectedVariant = state.feedPostListItemVariant,
                    onVariantSelected = { variant ->
                        viewModel.onEvent(
                            SettingsScreenEvent.OnFeedPostListItemVariantSelected(
                                variant,
                            ),
                        )
                    },
                    onArticleSettingsClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnArticleSettingsClicked)
                    },
                )
            }
            item(key = "data-and-sync") {
                Column {
                    SettingsSectionHeading(title = stringResource(stringRes.data_and_sync))
                    SettingsSubPageLink(stringResource(stringRes.import_feeds)) {
                        navigator.navigate(Screen.Tabs.Feed.AddFeed)
                    }
                    SettingsToggle(
                        checked = state.syncInBackground,
                        headlineText = stringResource(stringRes.sync_in_background),
                        supportingText = stringResource(stringRes.update_feeds_in_background),
                    ) {
                        viewModel.onEvent(
                            SettingsScreenEvent.OnSyncInBackgroundToggled(it),
                        )
                    }
                }
            }
            item(key = "privacy-and-diagnostics") {
                TelemetrySettingsSection(
                    crashReporting = state.crashReporting,
                    performanceMetrics = state.performanceMetrics,
                    loaded = state.isTelemetryConsentLoaded,
                    saving = state.isSavingTelemetryConsent,
                    error = state.telemetryConsentError,
                    onCrashReportingChanged = {
                        viewModel.onEvent(SettingsScreenEvent.OnTelemetryConsentChanged(crashReporting = it, performanceMetrics = state.performanceMetrics))
                    },
                    onPerformanceMetricsChanged = {
                        viewModel.onEvent(SettingsScreenEvent.OnTelemetryConsentChanged(crashReporting = state.crashReporting, performanceMetrics = it))
                    },
                    onRetry = { viewModel.onEvent(SettingsScreenEvent.OnRetryTelemetryConsent) },
                )
            }
            item(key = "others") {
                OthersSection(
                    onLicensesClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnLicensesClicked)
                    },
                )
            }
            item(key = "developer-settings") {
                DeveloperSettingsSection(
                    onDesignSystemBoardClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnDesignSystemBoardClicked)
                    },
                )
            }
            item(key = "version") {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = spacing.sectionGap),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Icon(
                        imageVector = DPIcons.devPulseIconLarge(),
                        contentDescription = "Dev Pulse",
                        modifier = Modifier.alpha(0.75f),
                    )
                    DPTextView(
                        text = "v${BuildKonfigCommon.APP_VERSION}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        variant = DPTextViewVariant.LabelMedium,
                    )
                }
            }
        }
    }
}
