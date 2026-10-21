package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

@Entity(
    foreignKeys = [ForeignKey(
        entity = LocalRssFeed::class,
        parentColumns = ["id"],
        childColumns = ["feedId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index(value = ["pinned", "feedId"])],
)
data class LocalRssFeedUserState(
    @PrimaryKey val feedId: LocalUUID,
    @ColumnInfo(defaultValue = "0")
    val pinned: Boolean = false,
    @ColumnInfo(defaultValue = "NULL")
    val lastOpenedAt: Long? = null,
)
