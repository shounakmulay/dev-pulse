package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity(
    primaryKeys = ["collectionId", "feedId"],
    foreignKeys = [
        ForeignKey(
            entity = LocalFeedDiscoveryCollection::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocalFeedDiscoveryFeedMetadata::class,
            parentColumns = ["id"],
            childColumns = ["feedId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["feedId"]),
        Index(value = ["collectionId", "position"])
    ]
)
data class LocalFeedDiscoveryCollectionToFeedMapping(
    val collectionId: LocalUUID,
    val feedId: LocalUUID,
    val position: Int
)