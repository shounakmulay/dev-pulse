package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity
data class LocalFeedDiscoveryFeedMetadata(
    @PrimaryKey
    val id: LocalUUID,
    val title: String,
    val description: String?,
    val feedFormat: LocalFeedDiscoveryFeedFormat,
    val url: String,
    val iconUrl: String?,
    val languageTag: String?,
    val websiteUrl: String?
)