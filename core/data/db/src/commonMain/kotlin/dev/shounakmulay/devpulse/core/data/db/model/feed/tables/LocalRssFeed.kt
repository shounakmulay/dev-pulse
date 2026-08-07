package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedImage
import dev.shounakmulay.devpulse.core.data.db.model.feed.embedded.LocalRssFeedYoutubeChannel


@Entity(
    indices = [
        Index(value = ["sourceUrl"], unique = true),
        Index(value = ["pinned", "id"]),
        Index(value = ["name", "id"]),
    ]
)
data class LocalRssFeed(
    @PrimaryKey
    val id: LocalUUID,
    val pinned: Boolean,
    val sourceUrl: String,
    @ColumnInfo(defaultValue = "''")
    val title: String = "",
    @ColumnInfo(defaultValue = "''")
    val name: String = "",
    val link: String?,
    val description: String?,
    @Embedded(prefix = "image_")
    val image: LocalRssFeedImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    @Embedded(prefix = "youtubeChannel_")
    val youtubeChannel: LocalRssFeedYoutubeChannel?,
    val createdAt: Long,
    val updatedAt: Long
)
