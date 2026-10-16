package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

sealed interface LocalFeedDiscoveryQuery {
    data class Collection(val collectionId: LocalUUID) : LocalFeedDiscoveryQuery
    data class Search(val text: String, val collectionId: LocalUUID? = null) : LocalFeedDiscoveryQuery
}
