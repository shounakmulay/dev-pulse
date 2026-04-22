package dev.shounakmulay.feature.settings.screens.settings.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.core.ui.event.EventHandler
import dev.shounakmulay.core.ui.viewmodel.MviViewModel
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel : MviViewModel<SettingsScreenState>(), EventHandler<SettingsEvent> {
    override fun createInitialState() = SettingsScreenState()
    override fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.NavigateToDesignSystemBoard -> navigateToDesignSystemBoard()
        }
    }

    private fun navigateToDesignSystemBoard() = viewModelScope.launch {
        setEffect(SettingsEffect.NavigateToDesignSystemBoard)
    }
}
