package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import kotlinx.collections.immutable.ImmutableList

internal fun LazyListScope.clearFiltersButton(
    filters: ImmutableList<RssPostFilter>,
    clearFilters: () -> Unit
) {
    item(key = "clear_filter") {
        val hasFilters = remember(filters) {
            filters.any { it.isEmpty().not() }
        }
        DPIconButton(
            modifier = Modifier.animateItem().size(24.dp),
            variant = DPIconButtonVariant.Tertiary,
            style = DPIconButtonStyle.Tonal,
            size = DPSize.Small,
            icon = DPIcons.Close,
            enabled = hasFilters,
            contentDescription = "",
            onClick = clearFilters
        )
    }
}