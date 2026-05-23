package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import dev.shounakmulay.devpulse.core.domain.feed.ImportFeedUseCase
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AddFeedViewModel(
    private val importFeedUseCase: ImportFeedUseCase
) : MviViewModel<AddFeedScreenState, Effect>(), EventHandler<AddFeedScreenEvent> {
    override fun createInitialState() = AddFeedScreenState()
    override fun createStateSerializer() = AddFeedScreenState.serializer()

    override fun onEvent(event: AddFeedScreenEvent) {
        when (event) {
            is AddFeedScreenEvent.ImportFeeds -> importFeeds()
            is AddFeedScreenEvent.UpdateUrl -> updateUrl(event.index, event.url)
        }
    }

    private fun updateUrl(index: Int, url: String) {
        setState {
            copy(
                addFeedDataList = addFeedDataList.map {
                    it.copy(
                        url = url
                    )
                }
            )
        }
    }

    fun importFeeds() {
        intent {
            importFeedUseCase.invoke(state.addFeedDataList)
        }
    }
}
