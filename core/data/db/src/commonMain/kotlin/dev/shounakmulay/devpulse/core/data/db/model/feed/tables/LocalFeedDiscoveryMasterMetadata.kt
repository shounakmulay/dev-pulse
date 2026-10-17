package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity
data class LocalFeedDiscoveryMasterMetadata(
    @PrimaryKey
    val catalogId: LocalUUID,
    val feedMembershipCount: Int,
    val publication: Int,
    val schemaVersion: Int,
    val uniqueFeedCount: Int,
    val etag: String? = null
)
