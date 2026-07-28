package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons

@Composable
internal fun LazyItemScope.ToggleChip(name: String, selected: Boolean?, onToggled: () -> Unit) {
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