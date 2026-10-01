package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssPostContentType
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContent
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContentFts

@Dao
interface PostContentDao {

    @Upsert
    suspend fun upsertPostContent(feedContent: LocalRssPostContent)

    @Upsert
    suspend fun upsertPostContentFts(feedContentFts: LocalRssPostContentFts)

    @Query("DELETE FROM LocalRssPostContentFts WHERE postId = :postId")
    suspend fun deletePostContentFts(postId: LocalUUID)

    @Upsert
    suspend fun upsertPostContents(feedContents: List<LocalRssPostContent>)

    @Upsert
    suspend fun upsertPostContentsFts(feedContentsFts: List<LocalRssPostContentFts>)

    @Query("SELECT * FROM LocalRssPostContent WHERE postId = :postId AND type = :type")
    suspend fun getPostContents(
        postId: LocalUUID,
        type: LocalRssPostContentType
    ): LocalRssPostContent?

    @Query(
        """
    SELECT
        post.*,
        feed.id AS feed_id,
        feed.title AS feed_title,
        feed.name AS feed_name,
        feed.pinned AS feed_pinned,
        feed.sourceUrl AS feed_sourceUrl,
        feed.link AS feed_link,
        feed.createdAt AS feed_createdAt,
        feed.updatedAt AS feed_updatedAt,
        feed.lastOpenedAt AS feed_lastOpenedAt,
        NULL AS search_highlightedTitle,
        NULL AS search_highlightedDescription,

        snippet(
            LocalRssPostContentFts,
            1,
            :hlStart,
            :hlEnd,
            "...",
            :snippetLength
        ) AS search_highlightedContent

    FROM LocalRssPostContentFts AS contentFts
    JOIN LocalRssContentFeedPost AS post
        ON post.id = contentFts.postId
    JOIN LocalRssFeed AS feed
        ON feed.id = post.feedId

    WHERE LocalRssPostContentFts MATCH :query

    ORDER BY rank
    LIMIT :limit
    """
    )
    suspend fun searchPostContent(
        query: String,
        snippetLength: Int = 30,
        hlStart: String = HL_START,
        hlEnd: String = HL_END,
        limit: Int = 25
    ): List<LocalRssPostWithFeedAndSearch>
}
