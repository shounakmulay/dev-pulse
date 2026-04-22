package dev.shounakmulay.feature.settings.screens.settings.ui

import dev.shounakmulay.core.ui.viewmodel.StateEffectViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel : StateEffectViewModel<SettingsScreenState>() {
    override fun createInitialState() = SettingsScreenState()
}
