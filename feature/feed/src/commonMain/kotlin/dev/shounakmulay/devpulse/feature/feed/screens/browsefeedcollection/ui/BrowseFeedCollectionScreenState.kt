package dev.shounakmulay.devpulse.feature.feed.screens.browsefeedcollection.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryCollection
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class BrowseFeedCollectionScreenState(
    val collectionId: UUID,
    val content: BrowseFeedCollectionContent = BrowseFeedCollectionContent.Loading,
    val searchQuery: String = "",
    @Transient val pendingImportIds: Set<UUID> = emptySet(),
    val selectedFeedId: UUID? = null,
    @Transient val selectedFeed: UIDiscoveryFeed? = null,
) : ScreenState {
    val selectedFeedDetails: UIDiscoveryFeed? get() = selectedFeed?.takeIf { it.id == selectedFeedId }
}

@Serializable
@Immutable
sealed interface BrowseFeedCollectionContent {
    @Serializable data object Loading : BrowseFeedCollectionContent
    @Serializable data object Missing : BrowseFeedCollectionContent
    @Serializable data class Ready(val collection: UIDiscoveryCollection) : BrowseFeedCollectionContent
}
