package dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class BrowseFeedsScreenState(
    val collectionShelves: List<UIDiscoveryCollectionShelf> = emptyList(),
    val searchQuery: String = "",
    @Transient val pendingImportIds: Set<UUID> = emptySet(),
    val selectedFeedId: UUID? = null,
    @Transient val selectedFeed: UIDiscoveryFeed? = null,
) : ScreenState {
    val selectedFeedDetails: UIDiscoveryFeed? get() = selectedFeed?.takeIf { it.id == selectedFeedId }
    val isLoading: Boolean = false
}
