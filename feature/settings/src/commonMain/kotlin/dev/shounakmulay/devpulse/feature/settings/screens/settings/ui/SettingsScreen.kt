package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import dev.shounakmulay.devpulse.core.common.BuildConfig
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
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.AppearanceSettingsSection
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.DeveloperSettingsSection
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.OthersSection
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.SettingsSectionHeading
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.SettingsSubPageLink
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.SettingsToggle
import dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components.ThemeSettingsSection
import devpulse.core.resources.generated.resources.app_version
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
                }
            )
        },
    ) { state ->
        val canToggleBlackMode = state.canToggleBlackMode(isDarkTheme = DPTheme.isDarkTheme)

        LazyColumn {
            item {
                ThemeSettingsSection(
                    selectedThemeMode = state.themeMode,
                    isBlackMode = state.isBlackMode,
                    canToggleBlackMode = canToggleBlackMode,
                    onThemeModeSelected = { value ->
                        viewModel.onEvent(SettingsScreenEvent.OnThemeModeSelected(value))
                    },
                    onBlackModeToggled = { value ->
                        viewModel.onEvent(SettingsScreenEvent.OnBlackModeToggled(value))
                    }
                )
            }
            item {
                AppearanceSettingsSection(
                    selectedVariant = state.feedPostListItemVariant,
                    onVariantSelected = { variant ->
                        viewModel.onEvent(
                            SettingsScreenEvent.OnFeedPostListItemVariantSelected(
                                variant
                            )
                        )
                    },
                    onArticleSettingsClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnArticleSettingsClicked)
                    }
                )
            }
            item {
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
                            SettingsScreenEvent.OnSyncInBackgroundToggled(it)
                        )
                    }
                }
            }
            item {
                OthersSection(
                    onLicensesClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnLicensesClicked)
                    }
                )
            }
            item {
                DeveloperSettingsSection(
                    onDesignSystemBoardClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnDesignSystemBoardClicked)
                    }
                )
            }
            item {
                Spacer(Modifier.height(LocalDPSpacing.current.listItemHeight * 2))
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = LocalDPSpacing.current.listItemHeight),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.sm),
                ) {
                    DPTextView(
                        text = TextResource.fromStringResWithArgs(
                            stringRes.app_version,
                            BuildConfig.APP_VERSION
                        ).asString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        variant = DPTextViewVariant.LabelMedium
                    )
                    Icon(
                        imageVector = DPIcons.devPulseIconLarge(),
                        contentDescription = "Dev Pulse",
                        modifier = Modifier
                            .padding(
                                top = LocalDPSpacing.current.md,
                                bottom = LocalDPSpacing.current.xl
                            )
                            .alpha(0.75f),
                    )
                }
            }
        }
    }
}
