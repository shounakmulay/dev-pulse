package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.sort

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPClickableRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.ui.bottomsheet.rememberDPModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
internal fun LazyListScope.sortItem(
    sortValues: ImmutableList<UIPostSort>,
    onSortUpdated: (UIPostSort) -> Unit,
    enabled: Boolean = true,
) {
    item(key = "sort_icon") {
        Icon(
            DPIcons.Sort,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = ""
        )
    }
    item(key = "sort_content") {
        val selected by remember(sortValues) {
            derivedStateOf {
                sortValues.firstOrNull { it.selected }
            }
        }
        val optionsBottomSheetController = rememberDPModalBottomSheetController()
        FilterChip(
            modifier = Modifier.animateItem(),
            colors = FilterChipDefaults.elevatedFilterChipColors(),
            selected = selected != null,
            enabled = enabled,
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
                    val icon = remember(it.sort) {
                        when (it.sort) {
                            RssPostSort.PublishedNewest -> DPIcons.ArrowUp
                            RssPostSort.PublishedOldest -> DPIcons.ArrowDownward
                            RssPostSort.TitleAtoZ -> DPIcons.SortAlphabetical
                            RssPostSort.TitleZtoA -> DPIcons.SortAlphabetical
                        }
                    }
                    val coroutineScope = rememberCoroutineScope()
                    DPClickableRow(
                        modifier = Modifier.padding(LocalDPSpacing.current.lg),
                        leadingIcon = icon,
                        trailingIcon = if (it.selected) DPIcons.Check else null,
                        trailingIconTint = MaterialTheme.colorScheme.primary,
                        title = it.name.asString(),
                        enabled = enabled,
                        onClick = {
                            onSortUpdated(it)
                            coroutineScope.launch { optionsBottomSheetController.hide() }
                        },
                    )
                }
            }
        }
    }
}