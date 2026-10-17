package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

sealed interface FeedDiscoveryCursor {
    val id: LocalUUID

    data class Collection(override val id: LocalUUID, val position: Int) : FeedDiscoveryCursor
    data class Search(override val id: LocalUUID, val rank: Double) : FeedDiscoveryCursor
}
