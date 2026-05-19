package dev.shounakmulay.devpulse.core.data.db.model.feed

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class LocalRssFeedQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val url: String,
    val name: String?,
    val type: LocalRssFeedType,
    val status: LocalRssFeedQueueStatus,
    val fetchesAttempted: Int
)
