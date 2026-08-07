package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalCompressedText
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssPostContentType


@Entity(
    primaryKeys = ["postId", "type"],
    foreignKeys = [
        ForeignKey(
            entity = LocalRssContentFeedPost::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LocalRssPostContent(
    val postId: LocalUUID,
    val type: LocalRssPostContentType,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val content: LocalCompressedText
)
