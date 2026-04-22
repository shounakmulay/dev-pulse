package dev.shounakmulay.feature.feed.screens.feed.ui

import dev.shounakmulay.core.ui.viewmodel.MviViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedViewModel : MviViewModel<FeedScreenState>() {
    override fun createInitialState(): FeedScreenState = FeedScreenState(isLoading = false)

    fun increment() {
        setState { copy(count = count + 1) }
    }
}