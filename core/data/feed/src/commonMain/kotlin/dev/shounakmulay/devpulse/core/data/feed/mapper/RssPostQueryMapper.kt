package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.toUTCMillis
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostLongRange
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostFilter
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSort
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import org.koin.core.annotation.Factory

@Factory
class RssPostQueryMapper {

    fun fromPostQueryMapper(query: RssPostQuery): LocalFeedPostQuery {
        return LocalFeedPostQuery(
            filters = mapFilters(query.filters),
            sort = mapSort(query.sort)
        )
    }

    private fun mapFilters(filters: List<RssPostFilter>): Set<LocalFeedPostFilter> {
        return filters.map {
            when (it) {
                is RssPostFilter.FeedIds -> LocalFeedPostFilter.FeedIds(it.values)
                is RssPostFilter.Author -> LocalFeedPostFilter.Author(it.values)
                is RssPostFilter.Bookmarked -> LocalFeedPostFilter.Bookmarked(it.value)
                is RssPostFilter.Category -> LocalFeedPostFilter.Category(it.values)
                is RssPostFilter.HasAudio -> LocalFeedPostFilter.HasAudio(it.value)
                is RssPostFilter.HasVideo -> LocalFeedPostFilter.HasVideo(it.value)
                is RssPostFilter.PinnedFeed -> LocalFeedPostFilter.PinnedFeed(it.value)
                is RssPostFilter.PublishedRange -> LocalFeedPostFilter.PublishedRange(
                    FeedPostLongRange(it.min?.toUTCMillis(), it.max?.toUTCMillis())
                )

                is RssPostFilter.SearchText -> LocalFeedPostFilter.SearchText(it.value)
                is RssPostFilter.TagIdsAny -> LocalFeedPostFilter.TagIdsAny(it.values)
            }
        }.toSet()
    }

    private fun mapSort(sort: RssPostSort): LocalFeedPostSort {
        return when (sort) {
            RssPostSort.PublishedOldest -> LocalFeedPostSort.PublishedOldest
            RssPostSort.PublishedNewest -> LocalFeedPostSort.PublishedNewest
            RssPostSort.TitleZtoA -> LocalFeedPostSort.TitleZtoA
            RssPostSort.TitleAtoZ -> LocalFeedPostSort.TitleAtoZ
        }
    }
}