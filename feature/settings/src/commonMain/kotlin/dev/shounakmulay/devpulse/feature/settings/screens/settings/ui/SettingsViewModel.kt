package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel : MviViewModel<SettingsScreenState, SettingsScreenEffect>(),
    EventHandler<SettingsScreenEvent> {
    override fun createInitialState() = SettingsScreenState()
    override fun createStateSerializer() = SettingsScreenState.serializer()

    override fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            SettingsScreenEvent.NavigateToDesignSystemBoard -> navigateToDesignSystemBoard()
        }
    }

    private fun navigateToDesignSystemBoard() =
        postEffect(SettingsScreenEffect.NavigateToDesignSystemBoard)
}
