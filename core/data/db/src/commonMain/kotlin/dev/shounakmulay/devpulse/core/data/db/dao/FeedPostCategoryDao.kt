package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostCategory

@Dao
interface FeedPostCategoryDao {

    @Upsert
    suspend fun upsertPostCategories(categories: List<LocalRssPostCategory>)

    @Query("DELETE FROM LocalRssPostCategory WHERE postId = :postId")
    suspend fun deletePostCategories(postId: LocalUUID)

    @Query("DELETE FROM LocalRssPostCategory WHERE postId IN (:postIds)")
    suspend fun deletePostCategories(postIds: Set<LocalUUID>)
}