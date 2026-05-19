package dev.shounakmulay.devpulse.core.data.db.model.feed

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    primaryKeys = ["postId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = LocalRssContentFeedPost::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = LocalRssPostTag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["tagId"])
    ]
)
data class LocalRssPostToTagMapping(
    val postId: String,
    val tagId: Int
)