package dev.shounakmulay.feature.feed.screens.feed.ui

import dev.shounakmulay.core.ui.viewmodel.StateEffectViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedViewModel : StateEffectViewModel<FeedScreenState>() {
    override fun createInitialState(): FeedScreenState = FeedScreenState(isLoading = false)

    fun increment() {
        setState { copy(count = count + 1) }
    }
}