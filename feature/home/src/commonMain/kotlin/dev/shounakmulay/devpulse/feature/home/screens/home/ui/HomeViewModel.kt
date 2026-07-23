package dev.shounakmulay.devpulse.feature.home.screens.home.ui

import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel : MviViewModel<HomeScreenState, HomeScreenEffect>(HomeScreenState(isLoading = false)),
    EventHandler<HomeScreenEvent> {
    override fun createStateSerializer() = HomeScreenState.serializer()

    override fun onEvent(event: HomeScreenEvent) {
    }

}