package dev.shounakmulay.devpulse.core.data.feed.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.bridge.markdownconverter.HtmlToMarkdownConverter
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.dao.PostContentDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.paging.FeedPostPagingSourceProvider
import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionAccessor
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostContentMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostQueryMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.html.ArticleFtsContentParser
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.domain.models.post.RssParsedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSearchHighlights
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentityAndSearch
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.DevPulseUrlHelper
import dev.shounakmulay.devpulse.core.network.bodyAsText
import dev.shounakmulay.devpulse.readability.ArticleExtractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory

@Factory
class PostRepositoryImpl(
    private val articleExtractor: ArticleExtractor,
    private val articleFtsContentParser: ArticleFtsContentParser,
    private val feedPostDao: FeedPostDao,
    private val postContentDao: PostContentDao,
    private val feedPostPagingSourceProvider: FeedPostPagingSourceProvider,
    private val rssPostQueryMapper: RssPostQueryMapper,
    private val rssPostMapper: RssPostMapper,
    private val rssFeedMapper: RssFeedMapper,
    private val rssPostContentMapper: RssPostContentMapper,
    private val uuidMapper: UuidMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val devPulseNetworkClient: DevPulseNetworkClient,
    private val devPulseUrlHelper: DevPulseUrlHelper,
    private val markdownConverter: HtmlToMarkdownConverter,
    private val transactionAccessor: DevPulseDatabaseTransactionAccessor
) : PostRepository {
    override fun getPost(id: UUID): Flow<RssPostWithFeedIdentityAndSearch> {
        return feedPostDao.observePost(uuidMapper.fromUuid(id)).map { post ->
            post.toRssPostWithFeedIdentity()
        }
    }

    override suspend fun upsertPosts(posts: List<RssFeedPost>) {
        feedPostDao.upsertPosts(posts.map(rssPostMapper::toLocalRssContentFeedPost))
    }

    override fun observePosts(
        query: RssPostQuery,
        pagingConfig: PagingConfig
    ): Flow<PagingData<RssPostWithFeedIdentityAndSearch>> {
        val query = rssPostQueryMapper.fromPostQueryMapper(query)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                feedPostPagingSourceProvider.getFeedPostPagingSource(query)
            }
        )
            .flow
            .map { pagingData ->
                pagingData.map { post ->
                    post.toRssPostWithFeedIdentity()
                }
            }
    }

    override fun observeRecentPosts(maxCount: Int): Flow<List<RssPostWithFeedIdentityAndSearch>> {
        return feedPostDao.observeRecentPosts(maxCount).map {
            it.map { post ->
                post.toRssPostWithFeedIdentity()
            }
        }
    }

    override suspend fun setPostBookmarked(id: UUID, bookmarked: Boolean) {
        feedPostDao.updateBookmarkStatus(id = uuidMapper.fromUuid(id), isBookmarked = bookmarked)
    }

    override suspend fun fetchPostContentUseCase(
        postId: UUID,
        type: RssFeedPostContentType
    ): RssParsedPostContent = withContext(dispatcherProvider.defaultDispatcher) {
        val post = feedPostDao.getPost(uuidMapper.fromUuid(postId))
        val link = requireNotNull(post?.link)
        val html = devPulseNetworkClient.get(
            url = link,
            headers = mapOf(
                DevPulseNetworkClient.Companion.HttpHeaders.USER_AGENT to DevPulseNetworkClient.Companion.HttpUserAgent.BROWSER
            )
        ).bodyAsText()
        val baseUrl = devPulseUrlHelper.getBaseUrl(link)
        val articleHtml = articleExtractor.extract(
            html = html,
            baseUrl = baseUrl
        )?.content
        val markdown = articleHtml?.let { convertToMarkdownString(it) }

        RssParsedPostContent(
            html = articleHtml,
            markdown = markdown
        )
    }

    override suspend fun getPostContent(
        postId: UUID,
        type: RssFeedPostContentType
    ): RssFeedPostContent? {
        val content = postContentDao.getPostContents(
            postId = uuidMapper.fromUuid(postId),
            type = rssPostContentMapper.fromPostContentType(type)
        )

        return content?.let {
            rssPostContentMapper.toRssPostContent(it)
        }
    }

    override suspend fun savePostContent(
        content: RssFeedPostContent,
    ) {
        // HTML is the source; we convert it to Markdown directly from the HTML,
        // so we store only the FTS content when we store HTML.
        val ftsContent = when (content.type) {
            RssFeedPostContentType.HTML -> articleFtsContentParser.parse(content.content)
                .getOrNull()

            else -> null
        }
        transactionAccessor.writeTransaction {
            postContentDao.upsertPostContent(
                rssPostContentMapper.fromRssPostContent(content)
            )
            if (content.type == RssFeedPostContentType.HTML) {
                postContentDao.deletePostContentFts(uuidMapper.fromUuid(content.postId))
            }
            if (!ftsContent.isNullOrBlank()) {
                postContentDao.insertPostContentFts(
                    rssPostContentMapper.fromRssPostContentToFts(
                        postId = content.postId,
                        ftsContent = ftsContent,
                    )
                )
            }
        }
    }

    override suspend fun getPostRssEncodedContent(
        postId: UUID,
        type: RssFeedPostContentType
    ): RssFeedPostContent? {
        require(
            type in setOf(
                RssFeedPostContentType.RSS_HTML,
                RssFeedPostContentType.RSS_MARKDOWN
            )
        ) {
            "Invalid post content type: $type"
        }


        val localId = uuidMapper.fromUuid(postId)
        val post = feedPostDao.getPost(localId) ?: return null
        return when (type) {
            RssFeedPostContentType.RSS_HTML -> {
                val encodedHtmlContent = post.content ?: return null
                RssFeedPostContent(
                    postId = postId,
                    type = type,
                    content = encodedHtmlContent
                )
            }

            RssFeedPostContentType.RSS_MARKDOWN -> {
                val localContent = postContentDao.getPostContents(
                    postId = localId,
                    type = rssPostContentMapper.fromPostContentType(type)
                ) ?: return null
                rssPostContentMapper.toRssPostContent(localContent)
            }

            else -> null
        }


    }

    override suspend fun getPostDescription(postId: UUID): String? {
        val post = feedPostDao.getPost(uuidMapper.fromUuid(postId)) ?: return null
        return post.description
    }

    override suspend fun convertToMarkdown(html: String): RssParsedPostContent? {
        val markdown = convertToMarkdownString(html)
        return RssParsedPostContent(
            html = null,
            markdown = markdown
        )
    }

    override suspend fun getLatestPostPublishedTimeForFeed(feedId: UUID): Long? {
        return feedPostDao.getLatestPostPublishedTimeForFeed(uuidMapper.fromUuid(feedId))
    }

    private suspend fun convertToMarkdownString(html: String): String? {
        val markdown = markdownConverter.convert(html)
        return markdown
    }

    private fun LocalRssPostWithFeedAndSearch.toRssPostWithFeedIdentity(): RssPostWithFeedIdentityAndSearch {
        return rssPostMapper.toRssPostWithFeedIdentity(
            post = rssPostMapper.toRssFeedPost(post),
            identity = rssFeedMapper.toRssIdentity(feed)
        ).copy(
            search = search?.let {
                RssPostSearchHighlights(
                    highlightedTitle = it.highlightedTitle,
                    highlightedDescription = it.highlightedDescription,
                    highlightedContent = it.highlightedContent
                )
            }
        )
    }

    private val IMAGE_REGEX = Regex("""!\[[^\]]*]\([^)]*\)""")
    private val CODE_FENCE_REGEX = Regex("""^\s*(```|~~~)""")

    /**
     * Ensures every Markdown image element (`![alt](src)`) sits on its own line
     * with a blank line above and below it.
     *
     * Rules:
     * - Lines inside fenced code blocks (``` or ~~~) are left untouched, even if
     *   they happen to contain image-like syntax.
     * - If an "image line" is preceded/followed by an existing blank line, no
     *   extra blank line is added (avoids double blank lines).
     * - If an image line is adjacent to another image line, they stay separated
     *   by exactly one blank line, not stacked.
     * - Leading/trailing blank lines are not force-added at doc boundaries.
     *
     * Note: if a line mixes an image with other prose (e.g. `Text ![a](b) more`),
     * the whole line is treated as an "image line" and isolated as-is — this
     * function does not split inline text away from the image. If you need that,
     * pre-process to hoist images onto their own line first.
     */
    fun addBlankLinesAroundImages(markdown: String): String {
        val lines = markdown.split("\n")

        // Precompute fenced-code-block state per line so we don't touch content inside ``` blocks.
        val inCodeBlock = BooleanArray(lines.size)
        var toggled = false
        for (i in lines.indices) {
            if (CODE_FENCE_REGEX.containsMatchIn(lines[i])) {
                inCodeBlock[i] = toggled // the fence line itself isn't "inside"
                toggled = !toggled
            } else {
                inCodeBlock[i] = toggled
            }
        }

        fun isImageLine(index: Int): Boolean =
            !inCodeBlock[index] &&
                    lines[index].isNotBlank() &&
                    IMAGE_REGEX.containsMatchIn(lines[index])

        val result = StringBuilder()
        for (i in lines.indices) {
            val line = lines[i]
            val currentIsImage = isImageLine(i)
            val prevIsImage = i > 0 && isImageLine(i - 1)

            // Insert blank line above, if this is an image line following non-blank, non-image content
            if (currentIsImage && i > 0 && lines[i - 1].isNotBlank() && result.isNotEmpty()) {
                result.append('\n')
            }

            result.append(line)
            if (i != lines.lastIndex) result.append('\n')

            // Insert blank line below, if this is (or was) an image line and the next line is non-blank
            val nextExists = i + 1 <= lines.lastIndex
            if (currentIsImage && nextExists && lines[i + 1].isNotBlank()) {
                result.append('\n')
            }
        }

        return result.toString()
    }
}
