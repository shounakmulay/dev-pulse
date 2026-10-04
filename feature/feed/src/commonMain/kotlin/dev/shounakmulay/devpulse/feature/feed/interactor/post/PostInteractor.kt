package dev.shounakmulay.devpulse.feature.feed.interactor.post

import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.extensions.ifNullOrBlank
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetRecentFeedItemsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetPaginatedFeedPostsUseCase
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentityAndSearch
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.datetime.DateTimeStringConverter
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPostSearchHighlights
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import dev.shounakmulay.devpulse.feature.feed.text.parseFtsHighlightedText
import devpulse.core.resources.generated.resources.newest
import devpulse.core.resources.generated.resources.oldest
import devpulse.core.resources.generated.resources.title_a_z
import devpulse.core.resources.generated.resources.title_z_a
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import kotlin.time.Instant

@Factory
class PostInteractor(
    private val feedInteractor: FeedInteractor,
    private val getRecentFeedItemsUseCase: GetRecentFeedItemsUseCase,
    private val getPaginatedFeedPostsUseCase: GetPaginatedFeedPostsUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val dateTimeStringConverter: DateTimeStringConverter
) {
    companion object {
        fun getDefaultFiltersFor(screen: Screen): ImmutableList<RssPostFilter> {
            return when (screen) {
                is Screen.Tabs.Feed.FeedDetail -> listOf(
                    RssPostFilter.Bookmarked(null),
                    RssPostFilter.PublishedRange(),
                    RssPostFilter.Category(emptySet()),
                    RssPostFilter.TagIdsAny(emptySet()),
                )

                else -> listOf(
                    RssPostFilter.FeedIds(emptySet()),
                    RssPostFilter.Bookmarked(null),
                    RssPostFilter.PublishedRange(),
                    RssPostFilter.Category(emptySet()),
                    RssPostFilter.TagIdsAny(emptySet()),
                )
            }.sortedWith(compareBy<RssPostFilter> {
                it.isEmpty()
            }.thenBy {
                it.order()
            }).toImmutableList()
        }

        val DEFAULT_SORT_OPTIONS: ImmutableList<UIPostSort> by lazy {
            RssPostSort.entries.map {
                val nameRes = when (it) {
                    RssPostSort.PublishedNewest -> stringRes.newest
                    RssPostSort.PublishedOldest -> stringRes.oldest
                    RssPostSort.TitleAtoZ -> stringRes.title_a_z
                    RssPostSort.TitleZtoA -> stringRes.title_z_a
                }
                UIPostSort(
                    name = TextResource.fromStringRes(nameRes),
                    selected = it == RssPostSort.PublishedNewest,
                    sort = it
                )
            }.toImmutableList()
        }
    }

    fun getRecentArticlesFlow(): Flow<ImmutableList<UIFeedPost>> {
        return getRecentFeedItemsUseCase().map { feedItems ->
            feedItems.map {
                toUIFeedPost(it)
            }.toImmutableList()
        }.flowOn(dispatcherProvider.defaultDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPostsWithFilterAndSort(
        filerAndSortFlow: Flow<Pair<RssPostSort?, List<RssPostFilter>>>
    ): Flow<PagingData<UIFeedPost>> {
        return filerAndSortFlow.flatMapLatest { (sort, filters) ->
            getPaginatedFeedPostsUseCase(
                filters = filters,
                sort = sort ?: RssPostSort.PublishedNewest
            ).map { pagingData ->
                pagingData.map {
                    toUIFeedPost(it)
                }
            }.flowOn(dispatcherProvider.defaultDispatcher)
        }
    }

    fun toUIFeedPost(postWithFeedIdentity: RssPostWithFeedIdentityAndSearch): UIFeedPost {
        val (post, feedIdentity) = postWithFeedIdentity
        val uiFeedIdentity = feedInteractor.toUIFeed(feedIdentity)
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
            feed = feedInteractor.toUIFeed(feedIdentity),
            search = postWithFeedIdentity.search?.let { search ->
                UIFeedPostSearchHighlights(
                    highlightedTitle = search.highlightedTitle?.let(::parseFtsHighlightedText),
                    highlightedDescription = search.highlightedDescription?.let(::parseFtsHighlightedText),
                    highlightedContent = search.highlightedContent?.let(::parseFtsHighlightedText)
                )
            },
        )
    }
}
