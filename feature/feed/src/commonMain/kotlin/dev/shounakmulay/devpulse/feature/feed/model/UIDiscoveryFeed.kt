package dev.shounakmulay.devpulse.feature.feed.model

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class UIDiscoveryFeed(
    val id: UUID,
    val title: String,
    val description: TextResource?,
    val feedUrl: String,
    val websiteUrl: String?,
    val imageUrl: String?,
    val initials: String,
    val importState: UIDiscoveryFeedImportState,
    val searchHighlights: UIDiscoveryFeedSearchHighlights? = null,
)

@Serializable
enum class UIDiscoveryFeedImportState { Added, Adding, Queued, Failed, NotAdded }

@Serializable
@Immutable
data class UIDiscoveryFeedSearchHighlights(
    val title: TextResource?,
    val description: TextResource?,
    val feedUrl: TextResource?,
    val websiteUrl: TextResource?,
)

@Serializable
@Immutable
data class UIDiscoveryCollection(
    val id: UUID,
    val title: String,
    val description: String?,
    val feedCount: Int,
)

@Serializable
@Immutable
data class UIDiscoveryCollectionShelf(
    val collection: UIDiscoveryCollection,
    val feeds: List<UIDiscoveryFeed>,
)
