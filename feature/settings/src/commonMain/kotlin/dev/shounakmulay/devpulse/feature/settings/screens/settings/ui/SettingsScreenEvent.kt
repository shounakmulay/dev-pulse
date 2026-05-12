package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.SettingsListItem

sealed interface SettingsScreenEvent : ScreenEvent {
    data class OnSubPageLinkClick(val linkToScreen: Screen) : SettingsScreenEvent
    data class OnListItemToggled(val item: SettingsListItem.Toggle, val value: Boolean) :
        SettingsScreenEvent

    data class OnSingleChoiceUpdated(
        val item: SettingsListItem.SingleChoice,
        val value: SettingsListItem.SingleChoice.SingleChoiceOptions
    ) : SettingsScreenEvent
}
