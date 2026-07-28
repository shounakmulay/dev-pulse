package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons

internal fun LazyListScope.filterIcon() {
    item(key = "filter_icon") {
        Icon(
            DPIcons.Filter,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = ""
        )
    }
}