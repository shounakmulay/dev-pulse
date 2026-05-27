package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing

@Composable
fun FeedListPlaceholderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalDPSpacing.current.listItemHeight + LocalDPSpacing.current.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
    ) {
        Surface(
            modifier = Modifier.size(LocalDPSpacing.current.listItemHeight),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            content = {},
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.xs),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.7f).height(18.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                content = {},
            )
            Surface(
                modifier = Modifier.fillMaxWidth(0.9f).height(14.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceContainer,
                content = {},
            )
        }
        Spacer(Modifier.height(LocalDPSpacing.current.listItemHeight))
    }
}