package dev.shounakmulay.devpulse.feature.feed.interactor.post

import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPaginatedFeedPostsUseCase
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import devpulse.core.resources.generated.resources.newest
import devpulse.core.resources.generated.resources.oldest
import devpulse.core.resources.generated.resources.title_a_z
import devpulse.core.resources.generated.resources.title_z_a
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.annotation.Factory

@Factory
class PostInteractor(
    private val feedInteractor: FeedInteractor,
    private val getPaginatedFeedPostsUseCase: GetPaginatedFeedPostsUseCase
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPostsWithFilterAndSort(
        filerAndSortFlow: Flow<Pair<RssPostSort?, List<RssPostFilter>>>
    ): Flow<PagingData<UIFeedPost>> {
        return filerAndSortFlow.flatMapLatest { (sort, filters) ->
            feedInteractor.getUIFeedPostFlow(
                getPaginatedFeedPostsUseCase(
                    filters = buildList {
                        addAll(filters)
                    },
                    sort = sort ?: RssPostSort.PublishedNewest
                )
            )
        }
    }
}