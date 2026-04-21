package dev.shounakmulay.feature.home.screens.home.ui

import dev.shounakmulay.core.ui.viewmodel.StateEffectViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel : StateEffectViewModel<HomeScreenState>() {
    override fun createInitialState() = HomeScreenState(isLoading = false)

    fun increment() {
        setState { copy(count = count + 1) }
    }

}