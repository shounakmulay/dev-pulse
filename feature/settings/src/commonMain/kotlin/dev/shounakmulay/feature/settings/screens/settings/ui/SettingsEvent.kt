package dev.shounakmulay.feature.settings.screens.settings.ui

import dev.shounakmulay.core.ui.event.ScreenEvent

sealed interface SettingsEvent : ScreenEvent {
    data object NavigateToDesignSystemBoard : SettingsEvent
}