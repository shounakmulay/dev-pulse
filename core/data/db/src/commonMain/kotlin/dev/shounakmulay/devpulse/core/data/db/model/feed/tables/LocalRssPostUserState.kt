package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity(
    foreignKeys = [ForeignKey(
        entity = LocalRssContentFeedPost::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index(value = ["bookmarked", "postId"])],
)
data class LocalRssPostUserState(
    @PrimaryKey val postId: LocalUUID,
    @ColumnInfo(defaultValue = "0")
    val isRead: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val bookmarked: Boolean = false,
)
