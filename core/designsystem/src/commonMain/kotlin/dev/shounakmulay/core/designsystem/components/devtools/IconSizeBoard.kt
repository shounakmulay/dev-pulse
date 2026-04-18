package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.theme.DPTheme

private data class IconSizeEntry(val name: String, val size: Dp)

@Composable
fun IconSizeBoard(modifier: Modifier = Modifier) {
    val ic = DPTheme.iconSize
    val entries = listOf(
        IconSizeEntry("xs", ic.xs),
        IconSizeEntry("sm", ic.sm),
        IconSizeEntry("md", ic.md),
        IconSizeEntry("lg", ic.lg),
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(entries) { entry ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(entry.size),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "${entry.name} — ${entry.size}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
