package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity(
    primaryKeys = ["postId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = LocalRssContentFeedPost::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocalRssPostTag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tagId", "postId"])
    ]
)
data class LocalRssPostToTagMapping(
    val postId: LocalUUID,
    val tagId: Int
)