package dev.shounakmulay.devpulse.core.data.db.model.feed

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey


@Entity(
    indices = [
        Index(value = ["sourceUrl"], unique = true),
        Index(value = ["title"])
    ]
)
data class LocalRssFeed(
    @PrimaryKey
    val id: String,
    val sourceUrl: String,
    val title: String?,
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
