package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.extensions.ifNullOrBlank
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPinnedAndRecentFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetRecentFeedItemsUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentity
import dev.shounakmulay.devpulse.core.ui.datetime.DateTimeStringConverter
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import kotlin.jvm.JvmName
import kotlin.time.Instant

@Factory
class FeedInteractor(
    private val extractInitialsUseCase: ExtractInitialsUseCase,
    private val getPinnedAndRecentFeedsUseCase: GetPinnedAndRecentFeedsUseCase,
    private val getRecentFeedItemsUseCase: GetRecentFeedItemsUseCase,
    private val dateTimeStringConverter: DateTimeStringConverter,
    private val dispatcherProvider: DispatcherProvider
) {
    fun getPinnedAndRecentsUIFeedFlow(): Flow<ImmutableList<UIFeed>> {
        return getPinnedAndRecentFeedsUseCase().map { feedList ->
            feedList.map {
                toUIFeed(it)
            }.toImmutableList()
        }.flowOn(dispatcherProvider.defaultDispatcher)
    }

    fun getRecentArticlesFlow(): Flow<ImmutableList<UIFeedPost>> {
        return getRecentFeedItemsUseCase().map { feedItems ->
            feedItems.map {
                toUIFeedArticle(it)
            }.toImmutableList()
        }.flowOn(dispatcherProvider.defaultDispatcher)
    }

    @JvmName("getUIFeedPostPagingDataFlow")
    @Deprecated("Use from PostInteractor")
    fun getUIFeedPostFlow(from: Flow<PagingData<RssPostWithFeedIdentity>>): Flow<PagingData<UIFeedPost>> {
        return from.map { pagingData ->
            pagingData.map {
                toUIFeedArticle(it)
            }
        }.flowOn(dispatcherProvider.defaultDispatcher)
    }

    @JvmName("getUIFeedPagingDataFlow")
    fun getUIFeedFlow(from: Flow<PagingData<RssFeed>>): Flow<PagingData<UIFeed>> {
        return from.map { pagingData ->
            pagingData.map {
                toUIFeed(it)
            }
        }.flowOn(dispatcherProvider.defaultDispatcher)
    }

    fun toUIFeed(feed: RssFeed): UIFeed {
        return createUIFeed(
            id = feed.id,
            name = feed.name,
            title = feed.title,
            link = feed.link,
            sourceUrl = feed.sourceUrl,
            pinned = feed.pinned,
            imageUrl = feed.image?.url
        )
    }


    fun toUIFeed(feedIdentity: RssFeedIdentity): UIFeed {
        val websiteImageUrl = getWebsiteImageUrl(feedIdentity.link, feedIdentity.sourceUrl)
        return createUIFeed(
            id = feedIdentity.id,
            name = feedIdentity.name,
            title = feedIdentity.title,
            link = feedIdentity.link,
            sourceUrl = feedIdentity.sourceUrl,
            pinned = feedIdentity.pinned,
            imageUrl = websiteImageUrl
        )
    }

    private fun createUIFeed(
        id: UUID,
        name: String?,
        title: String?,
        link: String?,
        sourceUrl: String,
        pinned: Boolean,
        imageUrl: String?
    ): UIFeed {
        val feedTitle = getFeedTitle(name, title)
        val websiteImageUrl = getWebsiteImageUrl(link, sourceUrl)

        return UIFeed(
            id = id,
            imageUrl = imageUrl,
            title = feedTitle,
            initials = extractInitialsUseCase(feedTitle),
            pinned = pinned,
            sourceUrl = sourceUrl,
            websiteImageUrl = websiteImageUrl
        )
    }

    private fun getWebsiteImageUrl(link: String?, sourceUrl: String): String =
        "https://www.google.com/s2/favicons?domain=${link ?: sourceUrl}&sz=128"

    private fun getFeedTitle(name: String?, title: String?): String {
        return name.ifNullOrBlank {
            title.ifNullOrBlank {
                // TODO: Extract from url
                "NO_NAME"
            }
        }
    }

    @Deprecated("Use from PostInteractor")
    fun toUIFeedArticle(postWithFeedIdentity: RssPostWithFeedIdentity): UIFeedPost {
        val (post, feedIdentity) = postWithFeedIdentity
        val uiFeedIdentity = toUIFeed(feedIdentity)
        val title = post.title.ifNullOrBlank {
            uiFeedIdentity.title
        }
        val imageUrl = listOf(
            post.image,
            post.rawMediaContent?.url,
            post.rawEnclosure?.url
        ).firstOrNull { !it.isNullOrBlank() }

        val summary = listOf(
            post.description,
            post.content
        ).firstOrNull { !it.isNullOrBlank() }

        val publishedText = post.publishedAtMillis?.let {
            dateTimeStringConverter.getTimeElapsedOrDateString(
                Instant.fromEpochMilliseconds(
                    it
                )
            )
        }
        val createAt = dateTimeStringConverter.getTimeElapsedOrDateString(
            Instant.fromEpochMilliseconds(
                post.createdAtMillis
            )
        )
        return UIFeedPost(
            id = post.id,
            title = title,
            sourceName = uiFeedIdentity.title,
            sourceUrl = post.sourceUrl,
            articleUrl = post.link,
            publishedText = publishedText,
            imageUrl = imageUrl,
            summary = summary,
            bookmarked = post.bookmarked,
            createdAt = createAt,
            feed = toUIFeed(feedIdentity),
        )
    }
}