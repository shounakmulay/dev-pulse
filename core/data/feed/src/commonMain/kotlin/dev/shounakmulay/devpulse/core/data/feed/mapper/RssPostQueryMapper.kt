package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostFilter
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSort
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSortOrder
import org.koin.core.annotation.Factory

@Factory
class RssPostQueryMapper {

    fun fromPostQueryMapper(query: RssPostQuery): LocalFeedPostQuery {
        return LocalFeedPostQuery(
            filters = mapFilters(query.filters),
            sort = mapSort(query.sort, query.order)
        )
    }

    private fun mapFilters(filters: List<RssPostFilter>): Set<LocalFeedPostFilter> {
        return filters.map {
            when (it) {
                is RssPostFilter.FeedIds -> LocalFeedPostFilter.FeedIds(it.values)
            }
        }.toSet()
    }

    private fun mapSort(sort: RssPostSort, direction: RssPostSortOrder): LocalFeedPostSort {
        return when (sort) {
            RssPostSort.Published if direction == RssPostSortOrder.Ascending -> LocalFeedPostSort.PublishedOldest
            RssPostSort.Published -> LocalFeedPostSort.PublishedNewest
            RssPostSort.Title if direction == RssPostSortOrder.Descending -> LocalFeedPostSort.TitleZtoA
            RssPostSort.Title -> LocalFeedPostSort.TitleAtoZ
        }
    }
}