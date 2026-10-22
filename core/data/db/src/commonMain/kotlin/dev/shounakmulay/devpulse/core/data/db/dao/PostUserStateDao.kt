package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostUserState

@Dao
interface PostUserStateDao {
    @Query("SELECT * FROM LocalRssPostUserState WHERE postId = :postId")
    suspend fun getState(postId: LocalUUID): LocalRssPostUserState?

    @Query("INSERT OR IGNORE INTO LocalRssPostUserState (postId, isRead, bookmarked) SELECT id, 0, 0 FROM LocalRssContentFeedPost WHERE id = :postId")
    suspend fun ensureState(postId: LocalUUID)

    @Query("UPDATE LocalRssPostUserState SET bookmarked = :bookmarked WHERE postId = :postId AND bookmarked != :bookmarked")
    suspend fun updateBookmarked(postId: LocalUUID, bookmarked: Boolean): Int

    @Query("UPDATE LocalRssPostUserState SET isRead = :isRead WHERE postId = :postId AND isRead != :isRead")
    suspend fun updateRead(postId: LocalUUID, isRead: Boolean): Int

    @Transaction
    suspend fun setBookmarked(postId: LocalUUID, bookmarked: Boolean) {
        ensureState(postId)
        updateBookmarked(postId = postId, bookmarked = bookmarked)
    }

    @Transaction
    suspend fun setRead(postId: LocalUUID, isRead: Boolean) {
        ensureState(postId)
        updateRead(postId = postId, isRead = isRead)
    }
}
