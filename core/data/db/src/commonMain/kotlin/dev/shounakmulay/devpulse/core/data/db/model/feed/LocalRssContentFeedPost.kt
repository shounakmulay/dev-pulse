package dev.shounakmulay.devpulse.core.data.db.model.feed

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

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
        Index(value = ["feedId"]),
        Index(value = ["fingerprint"], unique = true)
    ]
)
data class LocalRssContentFeedPost(
    @PrimaryKey
    val id: String,
    val feedId: String,
    val fingerprint: String,
    val guid: String?,
    val title: String?,
    val author: String?,
    val link: String?,
    val pubDate: String?,
    val publishedAtEpochMillis: Long?,
    val description: String?,
    val content: String?,
    val image: String?,
    val audio: String?,
    val video: String?,
    val sourceName: String?,
    val sourceUrl: String?,
    val categories: String,
    val commentsUrl: String?,
    @Embedded(prefix = "youtubeData_")
    val youtubeData: LocalRssFeedItemYoutubeData?,
    @Embedded(prefix = "rawEnclosure_")
    val rawEnclosure: LocalRssFeedItemRawEnclosure?,
    @Embedded(prefix = "rawMedia_")
    val rawMedia: LocalRssFeedItemMediaContent? = null,
    val createdAt: Long,
    val updatedAt: Long
)
