package dev.shounakmulay.feature.home.screens.home.ui

import dev.shounakmulay.core.ui.viewmodel.MviViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel : MviViewModel<HomeScreenState>() {
    override fun createInitialState() = HomeScreenState(isLoading = false)

    fun increment() {
        setState { copy(count = count + 1) }
    }

}