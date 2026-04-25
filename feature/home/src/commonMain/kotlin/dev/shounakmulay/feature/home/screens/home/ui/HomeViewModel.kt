package dev.shounakmulay.feature.home.screens.home.ui

import dev.shounakmulay.devpulse.core.ui.effect.HideKeyboardEffect
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel : MviViewModel<HomeScreenState, HomeScreenEffect>(),
    EventHandler<HomeScreenEvent> {
    override fun createInitialState() = HomeScreenState(isLoading = false)
    override fun createStateSerializer() = HomeScreenState.serializer()

    override fun onEvent(event: HomeScreenEvent) {
    }

}