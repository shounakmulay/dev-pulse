package dev.shounakmulay.devpulse.core.data.db.model.feed

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity
data class LocalRssFeed(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
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
