package dev.shounakmulay.devpulse.core.data.db.model.feed

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.TypeConverters

@Entity
@TypeConverters(LocalRssFeedQueueConverters::class)
data class LocalRssFeedQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val url: String,
    val name: String?,
    val feedType: LocalRssFeedType,
    val actionType: LocalRssFeedQueueActionType,
    val requestor: LocalRssFeedQueueActionRequestor,
    val status: LocalRssFeedQueueStatus,
    val fetchAttempt: Int,
    val createAt: Long,
    val updatedAt: Long
)
