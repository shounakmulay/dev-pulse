package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssPostContentType
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContent

@Dao
interface PostContentDao {

    @Upsert
    suspend fun upsertPostContent(feedContent: LocalRssPostContent)

    @Upsert
    suspend fun upsertPostContents(feedContents: List<LocalRssPostContent>)

    @Query("SELECT * FROM LocalRssPostContent WHERE postId = :postId AND type = :type")
    suspend fun getPostContents(postId: LocalUUID, type: LocalRssPostContentType): LocalRssPostContent?
}