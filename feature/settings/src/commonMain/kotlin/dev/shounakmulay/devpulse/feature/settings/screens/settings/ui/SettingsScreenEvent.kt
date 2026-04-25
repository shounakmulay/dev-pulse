package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface SettingsScreenEvent : ScreenEvent {
    data object NavigateToDesignSystemBoard : SettingsScreenEvent
}