package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedUserState

@Dao
interface FeedUserStateDao {
    @Query("SELECT * FROM LocalRssFeedUserState WHERE feedId = :feedId")
    suspend fun getState(feedId: LocalUUID): LocalRssFeedUserState?

    @Query("INSERT OR IGNORE INTO LocalRssFeedUserState (feedId, pinned, lastOpenedAt) SELECT id, 0, NULL FROM LocalRssFeed WHERE id = :feedId")
    suspend fun ensureState(feedId: LocalUUID)

    @Query("UPDATE LocalRssFeedUserState SET pinned = :pinned WHERE feedId = :feedId AND pinned != :pinned")
    suspend fun updatePinned(feedId: LocalUUID, pinned: Boolean): Int

    @Query("UPDATE LocalRssFeedUserState SET lastOpenedAt = :lastOpenedAt WHERE feedId = :feedId AND lastOpenedAt IS NOT :lastOpenedAt")
    suspend fun updateLastOpenedAt(feedId: LocalUUID, lastOpenedAt: Long?): Int

    @Transaction
    suspend fun setPinned(feedId: LocalUUID, pinned: Boolean) {
        ensureState(feedId)
        updatePinned(feedId = feedId, pinned = pinned)
    }

    @Transaction
    suspend fun setLastOpenedAt(feedId: LocalUUID, lastOpenedAt: Long?) {
        ensureState(feedId)
        updateLastOpenedAt(feedId = feedId, lastOpenedAt = lastOpenedAt)
    }
}
