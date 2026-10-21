package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Fts5
import androidx.room3.FtsOptions.TOKENIZER_TRIGRAM
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedItemYoutubeData

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
        Index(value = ["fingerprint"], unique = true),
        Index(value = ["publishedAtEpochMillis", "updatedAt", "id"]),
        Index(value = ["feedId", "publishedAtEpochMillis", "updatedAt", "id"]),
        Index(value = ["bookmarked", "publishedAtEpochMillis", "updatedAt", "id"]),
        Index(value = ["feedId", "bookmarked", "publishedAtEpochMillis", "updatedAt", "id"]),
        Index(value = ["updatedAt", "id"]),
        Index(value = ["createdAt", "id"]),
        Index(value = ["title", "id"])
    ]
)
data class LocalRssContentFeedPost(
    @PrimaryKey
    val id: LocalUUID,
    val feedId: LocalUUID,
    val fingerprint: String,
    val guid: String?,
    @ColumnInfo(defaultValue = "''")
    val title: String = "",
    @ColumnInfo(defaultValue = "''")
    val author: String = "",
    val link: String?,
    val pubDate: String?,
    @ColumnInfo(defaultValue = "-9223372036854775808")
    val publishedAtEpochMillis: Long = Long.MIN_VALUE,
    val description: String?,
    val content: String?,
    val image: String?,
    val audio: String?,
    val video: String?,
    @ColumnInfo(defaultValue = "''")
    val sourceName: String = "",
    @ColumnInfo(defaultValue = "''")
    val sourceUrl: String = "",
    val categories: String,
    val commentsUrl: String?,
    @ColumnInfo(defaultValue = "0")
    val bookmarked: Boolean = false,
    @Embedded(prefix = "youtubeData_")
    val youtubeData: LocalRssFeedItemYoutubeData?,
    @Embedded(prefix = "rawEnclosure_")
    val rawEnclosure: LocalRssFeedItemRawEnclosure?,
    @Embedded(prefix = "rawMedia_")
    val rawMedia: LocalRssFeedItemMediaContent? = null,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity
@Fts5(tokenizer = TOKENIZER_TRIGRAM, contentEntity = LocalRssContentFeedPost::class)
data class LocalRssContentFeedPostFts(
    val title: String,
    val description: String?,
    val content: String?
)