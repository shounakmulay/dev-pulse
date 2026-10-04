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
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithSearch
import dev.shounakmulay.devpulse.core.ui.datetime.DateTimeStringConverter
import dev.shounakmulay.devpulse.feature.feed.interactor.getWebsiteImageUrl
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.text.parseFtsHighlightedText
import kotlin.jvm.JvmName
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

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

    @JvmName("getUIFeedPagingDataFlow")
    fun getUIFeedFlow(from: Flow<PagingData<RssFeedWithSearch>>): Flow<PagingData<UIFeed>> {
        return from.map { pagingData ->
            pagingData.map {
                toUIFeed(it)
            }
        }.flowOn(dispatcherProvider.defaultDispatcher)
    }



    fun toUIFeed(result: RssFeedWithSearch): UIFeed {
        return toUIFeed(result.feed).copy(
            highlightedTitle = result.highlightedName
                ?.takeIf { it.isNotBlank() }
                .let { highlightedName -> highlightedName ?: result.highlightedTitle }
                ?.let(::parseFtsHighlightedText),
            highlightedDescription = result.highlightedDescription?.let(::parseFtsHighlightedText),
        )
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

    private fun getFeedTitle(name: String?, title: String?): String {
        return name.ifNullOrBlank {
            title.ifNullOrBlank {
                // TODO: Extract from url
                "NO_NAME"
            }
        }
    }
}
