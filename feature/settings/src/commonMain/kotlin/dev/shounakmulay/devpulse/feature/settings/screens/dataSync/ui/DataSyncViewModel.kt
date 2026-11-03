package dev.shounakmulay.devpulse.feature.settings.screens.dataSync.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.discovery.RefreshFeedDiscoveryUseCase
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class DataSyncViewModel(
    private val refreshFeedDiscoveryUseCase: RefreshFeedDiscoveryUseCase,
) : MviViewModel<DataSyncScreenState, Nothing>(DataSyncScreenState()),
    EventHandler<DataSyncScreenEvent> {

    override fun createStateSerializer() = DataSyncScreenState.serializer()

    override fun onEvent(event: DataSyncScreenEvent) {
        when (event) {
            DataSyncScreenEvent.RefreshDiscovery -> refreshDiscovery()
        }
    }

    private fun refreshDiscovery() {
        if (state.value.discoveryRefreshStatus == DiscoveryRefreshStatus.Refreshing) return
        blockingSetState { copy(discoveryRefreshStatus = DiscoveryRefreshStatus.Refreshing) }
        viewModelScope.launch {
            try {
                val result = refreshFeedDiscoveryUseCase()
                blockingSetState {
                    copy(
                        discoveryRefreshStatus = if (result.isSuccess) {
                            DiscoveryRefreshStatus.Succeeded
                        } else {
                            DiscoveryRefreshStatus.Failed
                        },
                    )
                }
            } finally {
                if (state.value.discoveryRefreshStatus == DiscoveryRefreshStatus.Refreshing) {
                    blockingSetState { copy(discoveryRefreshStatus = DiscoveryRefreshStatus.Idle) }
                }
            }
        }
    }
}
