package dev.shounakmulay.devpulse.core.data.db.model.feed.tables

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedQueueActionType
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedQueueStatus
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedType

@Entity(
    indices = [
        Index("feedId", "status")
    ]
)
data class LocalRssFeedQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(defaultValue = "NULL")
    val feedId: LocalUUID? = null,
    val url: String,
    val name: String?,
    val feedType: LocalRssFeedType,
    val actionType: LocalRssFeedQueueActionType,
    val requestor: LocalRssFeedQueueActionRequestor,
    val status: LocalRssFeedQueueStatus,
    val fetchAttempt: Int,
    val tagIds: String?,
    val folderIds: String?,
    val createAt: Long,
    val updatedAt: Long
)
