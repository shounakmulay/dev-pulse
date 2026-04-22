package dev.shounakmulay.feature.settings.screens.settings.ui

import dev.shounakmulay.core.ui.effect.Effect

sealed interface SettingsEffect: Effect {
    data object NavigateToDesignSystemBoard : SettingsEffect
}