package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSectionDivider
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing

fun LazyListScope.articlesContentSection() {
    stickyHeader {
        DPSectionDivider(
            title = "Articles"
        ) {
            DPButton(
                text = "View All",
                onClick = {},
                variant = DPButtonVariant.Secondary,
                style = DPButtonStyle.Text,
                size = DPSize.Small
            )
        }
    }
    item(key = "ArticlesContentSection") {
        LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Adaptive(250.dp),
            modifier = Modifier.height(520.dp),
            verticalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.sm,
                Alignment.CenterVertically
            ),
            horizontalItemSpacing = LocalDPSpacing.current.sm,
            contentPadding = PaddingValues(horizontal = LocalDPSpacing.current.md)
        ) {
            for (i in 1..100) {
                item(key = i) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Spacer(
                            Modifier.size(
                                height = if (i % 3 == 0) 125.dp else 200.dp,
                                width = if (i % 2 == 0) 200.dp else 250.dp
                            )
                        )
                    }
                }
            }
        }
    }
}