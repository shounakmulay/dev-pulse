package dev.shounakmulay.devpulse.feature.feed.interactor

import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.extensions.ifNullOrBlank
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPinnedAndRecentFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import kotlin.jvm.JvmName

@Factory
class FeedInteractor(
    private val extractInitialsUseCase: ExtractInitialsUseCase,
    private val getPinnedAndRecentFeedsUseCase: GetPinnedAndRecentFeedsUseCase,
    private val dispatcherProvider: DispatcherProvider
) {
    fun getPinnedAndRecentsUIFeedFlow(): Flow<List<UIFeed>> {
        return getPinnedAndRecentFeedsUseCase().map { feedList ->
            feedList.map {
                toUIFeed(it)
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

    private fun toUIFeed(feed: RssFeed): UIFeed {
        val title = feed.name.ifNullOrBlank {
            feed.title.ifNullOrBlank {
                // TODO: Extract from url
                "NO_NAME"
            }
        }

        val websiteImageUrl =
            "https://www.google.com/s2/favicons?domain=${feed.link ?: feed.sourceUrl}&sz=128"

        return UIFeed(
            id = feed.id,
            imageUrl = feed.image?.url,
            title = title,
            initials = extractInitialsUseCase(title),
            pinned = feed.pinned,
            sourceUrl = feed.sourceUrl,
            websiteImageUrl = websiteImageUrl
        )
    }
}