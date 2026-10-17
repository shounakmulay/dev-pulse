package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = LocalFeedDiscoveryMasterMetadata::class,
            parentColumns = ["catalogId"],
            childColumns = ["catalogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["catalogId", "position"])
    ]
)
data class LocalFeedDiscoveryCollection(
    @PrimaryKey
    val id: LocalUUID,
    val catalogId: LocalUUID,
    val path: String,
    val feedCount: Int,
    val sha256Hash: String,
    val slug: String,
    val title: String,
    val position: Int,
    val description: String
)