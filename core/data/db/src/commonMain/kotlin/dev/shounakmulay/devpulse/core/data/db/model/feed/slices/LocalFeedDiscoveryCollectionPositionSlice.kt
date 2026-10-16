package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

data class LocalFeedDiscoveryCollectionPositionSlice(
    val id: LocalUUID,
    val position: Int
)