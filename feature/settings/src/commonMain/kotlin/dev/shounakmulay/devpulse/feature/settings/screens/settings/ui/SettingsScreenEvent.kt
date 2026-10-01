package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface SettingsScreenEvent : ScreenEvent {
    data class OnFeedPostListItemVariantSelected(val variant: FeedsPostListItemVariant) : SettingsScreenEvent
    data class OnThemeModeSelected(val themeMode: ThemeMode) : SettingsScreenEvent
    data class OnBlackModeToggled(val value: Boolean) : SettingsScreenEvent
    data object OnDesignSystemBoardClicked : SettingsScreenEvent
    data object OnLicensesClicked : SettingsScreenEvent
    data object OnArticleSettingsClicked : SettingsScreenEvent
}
