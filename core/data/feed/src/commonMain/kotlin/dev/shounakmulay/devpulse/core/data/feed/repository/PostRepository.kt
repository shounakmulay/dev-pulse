package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.domain.models.post.RssParsedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentity
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPost(id: UUID): Flow<RssPostWithFeedIdentity>
    fun observePosts(
        query: RssPostQuery,
        pagingConfig: PagingConfig
    ): Flow<PagingData<RssPostWithFeedIdentity>>

    fun observeRecentPosts(maxCount: Int): Flow<List<RssPostWithFeedIdentity>>
    suspend fun setPostBookmarked(id: UUID, bookmarked: Boolean)
    suspend fun fetchPostContentUseCase(postId: UUID, type: RssFeedPostContentType): RssParsedPostContent
    suspend fun getPostContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent?
    suspend fun savePostContent(
        content: RssFeedPostContent,
    )

    suspend fun getPostRssEncodedContent(
        postId: UUID,
        type: RssFeedPostContentType
    ): RssFeedPostContent?

    suspend fun getPostDescription(postId: UUID): String?

    suspend fun convertToMarkdown(html: String): RssParsedPostContent?
}