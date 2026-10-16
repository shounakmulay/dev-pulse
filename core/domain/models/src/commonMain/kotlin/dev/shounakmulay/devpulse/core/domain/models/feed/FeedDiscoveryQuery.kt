package dev.shounakmulay.devpulse.core.domain.models.feed

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

sealed interface FeedDiscoveryQuery {
    data class Collection(val collectionId: UUID) : FeedDiscoveryQuery
    data class Search(val text: String, val collectionId: UUID? = null) : FeedDiscoveryQuery
}
