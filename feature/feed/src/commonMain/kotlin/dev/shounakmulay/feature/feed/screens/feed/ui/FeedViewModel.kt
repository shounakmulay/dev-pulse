package dev.shounakmulay.feature.feed.screens.feed.ui

import dev.shounakmulay.core.ui.event.EventHandler
import dev.shounakmulay.core.ui.viewmodel.MviViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedViewModel : MviViewModel<FeedScreenState, FeedScreenEffect>(),
    EventHandler<FeedScreenEvent> {
    override fun createInitialState(): FeedScreenState = FeedScreenState(isLoading = false)
    override fun createStateSerializer() = FeedScreenState.serializer()

    override fun onEvent(event: FeedScreenEvent) {
        TODO("Not yet implemented")
    }
}