package dev.shounakmulay.devpulse.feature.feed.interactor

import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.extensions.ifNullOrBlank
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPinnedAndRecentFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetRecentFeedItemsUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssPostWithFeedIdentity
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import kotlin.jvm.JvmName

@Factory
class FeedInteractor(
    private val extractInitialsUseCase: ExtractInitialsUseCase,
    private val getPinnedAndRecentFeedsUseCase: GetPinnedAndRecentFeedsUseCase,
    private val getRecentFeedItemsUseCase: GetRecentFeedItemsUseCase,
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

    @JvmName("getUIFeedPagingDataFlow")
    fun getUIFeedFlow(from: Flow<PagingData<RssFeed>>): Flow<PagingData<UIFeed>> {
        return from.map { pagingData ->
            pagingData.map {
                toUIFeed(it)
            }
        }.flowOn(dispatcherProvider.defaultDispatcher)
    }

    private fun toUIFeed(feed: RssFeed): UIFeed {
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

    private fun toUIFeed(feedIdentity: RssFeedIdentity): UIFeed {
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
        id: String,
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

    private fun toUIFeedArticle(postWithFeedIdentity: RssPostWithFeedIdentity): UIFeedPost {
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

        return UIFeedPost(
            id = post.id,
            title = title,
            sourceName = uiFeedIdentity.title,
            sourceUrl = post.sourceUrl,
            articleUrl = post.link,
            publishedText = post.pubDate,
            imageUrl = imageUrl,
            summary = summary,
            feed = toUIFeed(feedIdentity),
        )
    }
}
