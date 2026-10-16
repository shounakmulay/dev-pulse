package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

data class LocalFeedDiscoveryCollectionHashSlice(
    val id: LocalUUID,
    val path: String,
    val sha256Hash: String
)
