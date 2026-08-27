package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity(
    primaryKeys = ["postId", "category"],
    foreignKeys = [
        ForeignKey(
            entity = LocalRssContentFeedPost::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category", "postId"])
    ]
)
data class LocalRssPostCategory(
    val postId: LocalUUID,
    val category: String
)
