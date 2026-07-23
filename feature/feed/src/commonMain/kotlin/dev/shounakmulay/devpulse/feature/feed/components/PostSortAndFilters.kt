package dev.shounakmulay.devpulse.feature.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.common.extensions.ifNullOrBlank
import dev.shounakmulay.devpulse.core.designsystem.components.DPClickableRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.ui.bottomsheet.dpModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import devpulse.core.resources.generated.resources.bookmarked
import devpulse.core.resources.generated.resources.has_audio
import devpulse.core.resources.generated.resources.has_video
import devpulse.core.resources.generated.resources.pinned
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PostSortAndFilters(
    sortValues: ImmutableList<UIPostSort>,
    onSortUpdated: (UIPostSort) -> Unit,
    filters: ImmutableList<RssPostFilter>,
    onFilterUpdated: (RssPostFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.sm),
    ) {
        sortItem(sortValues, onSortUpdated)
        filterItems(filters, onFilterUpdated)
    }
}

private fun LazyListScope.filterItems(
    filters: ImmutableList<RssPostFilter>,
    onFilterUpdated: (RssPostFilter) -> Unit
) {
    items(
        filters,
        key = { it::class.simpleName.ifNullOrBlank { it::class.toString() } }) {
        when (it) {
            is RssPostFilter.Bookmarked -> ToggleChip(
                name = stringResource(stringRes.bookmarked),
                selected = it.value,
                onToggled = { onFilterUpdated(it.copy(value = if (it.value == true) null else true)) }
            )

            is RssPostFilter.HasAudio -> ToggleChip(
                name = stringResource(stringRes.has_audio),
                selected = it.value,
                onToggled = {}
            )

            is RssPostFilter.HasVideo -> ToggleChip(
                name = stringResource(stringRes.has_video),
                selected = it.value,
                onToggled = {}
            )

            is RssPostFilter.PinnedFeed -> ToggleChip(
                name = stringResource(stringRes.pinned),
                selected = it.value,
                onToggled = {}
            )

            is RssPostFilter.Author,
            is RssPostFilter.Category,
            is RssPostFilter.FeedIds,
            is RssPostFilter.PublishedRange,
            is RssPostFilter.TagIdsAny -> {
                FilterChip(
                    modifier = Modifier.animateItem(),
                    colors = FilterChipDefaults.elevatedFilterChipColors(),
                    selected = false,
                    shape = MaterialTheme.shapes.large,
                    trailingIcon = {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = ""
                        )
                    },
                    onClick = {},
                    label = {
                        DPTextView(
                            it::class.simpleName.orEmpty(),
                            variant = DPTextViewVariant.LabelMedium
                        )
                    },
                )
            }

            is RssPostFilter.SearchText -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.sortItem(
    sortValues: ImmutableList<UIPostSort>,
    onSortUpdated: (UIPostSort) -> Unit,
) {
    item(key = "sort") {
        val selected by remember(sortValues) {
            derivedStateOf {
                sortValues.firstOrNull { it.selected }
            }
        }
        val optionsBottomSheetController = dpModalBottomSheetController()
        FilterChip(
            modifier = Modifier.animateItem(),
            colors = FilterChipDefaults.elevatedFilterChipColors(),
            selected = selected != null,
            shape = MaterialTheme.shapes.large,
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
            },
            onClick = { optionsBottomSheetController.show() },
            label = {
                DPTextView(
                    text = selected?.name?.asString().orEmpty(),
                    variant = DPTextViewVariant.LabelMedium
                )
            },
        )

        DPModalBottomSheet(controller = optionsBottomSheetController) {
            LazyColumn {
                items(sortValues) {
                    DPClickableRow(
                        trailingIcon = if (it.selected) DPIcons.Check else null,
                        title = it.name.asString(),
                        onClick = {
                            onSortUpdated(it)
                            optionsBottomSheetController.hide()
                        },
                    )
                }
            }
        }

    }
}

@Composable
private fun LazyItemScope.ToggleChip(name: String, selected: Boolean?, onToggled: () -> Unit) {
    FilterChip(
        modifier = Modifier.animateItem(),
        colors = FilterChipDefaults.elevatedFilterChipColors(),
        selected = selected ?: false,
        shape = MaterialTheme.shapes.large,
        trailingIcon = {
            if (selected == true) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = DPIcons.Check,
                    contentDescription = ""
                )
            }
        },
        onClick = onToggled,
        label = {
            DPTextView(
                text = name,
                variant = DPTextViewVariant.LabelMedium
            )
        },
    )
}