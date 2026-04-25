package dev.shounakmulay.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface SettingsEvent : ScreenEvent {
    data object NavigateToDesignSystemBoard : SettingsEvent
}