package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

@Composable
internal fun LazyItemScope.OptionChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
) {
    FilterChip(
        modifier = modifier.animateItem(),
        colors = FilterChipDefaults.elevatedFilterChipColors(),
        selected = selected,
        shape = MaterialTheme.shapes.large,
        trailingIcon = {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = ""
            )
        },
        onClick = onClick,
        label = {
            DPTextView(
                text = label,
                variant = DPTextViewVariant.LabelMedium
            )
        },
    )
}