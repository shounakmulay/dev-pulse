package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = LocalRssFeed::class,
            parentColumns = ["id"],
            childColumns = ["feedId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            "nextEligibleFetchAt",
        )
    ]
)
data class LocalRssFeedSyncMetadata(
    @PrimaryKey
    val feedId: LocalUUID,
    val checkIntervalHours: Int = 1,
    val consecutiveEmptyFetches: Int = 0,
    val consecutiveFailures: Int = 0,
    val lastFetchedAt: Long,
    val lastNewArticleAt: Long?,
    val nextEligibleFetchAt: Long,
    val etag: String? = null,
    val lastModified: String? = null
)