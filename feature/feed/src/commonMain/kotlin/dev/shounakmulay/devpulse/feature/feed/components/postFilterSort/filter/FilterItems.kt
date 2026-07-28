package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import dev.shounakmulay.devpulse.core.common.extensions.ifNullOrBlank
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.feedList.FeedIdFilter
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange.PublishedRangeFilter
import devpulse.core.resources.generated.resources.bookmarked
import devpulse.core.resources.generated.resources.has_audio
import devpulse.core.resources.generated.resources.has_video
import devpulse.core.resources.generated.resources.pinned
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.jetbrains.compose.resources.stringResource

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    FormatStringsInDatetimeFormats::class
)
internal fun LazyListScope.filterItems(
    filters: ImmutableList<RssPostFilter>,
    onFilterUpdated: (RssPostFilter) -> Unit,
    clearFilters: () -> Unit
) {
    filterIcon()
    clearFiltersButton(filters, clearFilters)
    filters(filters, onFilterUpdated)
}

private fun LazyListScope.filters(
    filters: ImmutableList<RssPostFilter>,
    onFilterUpdated: (RssPostFilter) -> Unit
) {
    items(
        filters,
        key = { it::class.simpleName.ifNullOrBlank { it::class.toString() } }) { filter ->
        when (filter) {
            is RssPostFilter.Bookmarked -> ToggleChip(
                name = stringResource(stringRes.bookmarked),
                selected = filter.value,
                onToggled = {
                    onFilterUpdated(
                        filter.copy(
                            value = if (filter.value == true) null else true
                        )
                    )
                }
            )

            is RssPostFilter.HasAudio -> ToggleChip(
                name = stringResource(stringRes.has_audio),
                selected = filter.value,
                onToggled = {}
            )

            is RssPostFilter.HasVideo -> ToggleChip(
                name = stringResource(stringRes.has_video),
                selected = filter.value,
                onToggled = {}
            )

            is RssPostFilter.PinnedFeed -> ToggleChip(
                name = stringResource(stringRes.pinned),
                selected = filter.value,
                onToggled = {}
            )

            is RssPostFilter.Author,
            is RssPostFilter.Category,
            is RssPostFilter.TagIdsAny -> {
                OptionChip(
                    selected = false,
                    onClick = {},
                    label = filter::class.simpleName.orEmpty()
                )
            }

            is RssPostFilter.FeedIds -> {
                FeedIdFilter(filter, onFilterUpdated)
            }


            is RssPostFilter.PublishedRange -> {
                PublishedRangeFilter(
                    filter = filter,
                    onFilterUpdated = onFilterUpdated
                )
            }

            is RssPostFilter.SearchText -> {}
        }
    }
}
