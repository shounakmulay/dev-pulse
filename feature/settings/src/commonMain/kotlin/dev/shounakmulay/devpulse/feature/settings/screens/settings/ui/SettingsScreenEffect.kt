package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.ui.effect.Effect

sealed interface SettingsScreenEffect: Effect {
    data object NavigateToDesignSystemBoard : SettingsScreenEffect
}