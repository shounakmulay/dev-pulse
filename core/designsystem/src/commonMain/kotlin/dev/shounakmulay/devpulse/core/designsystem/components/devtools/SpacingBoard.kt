package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme

private data class SpacingEntry(val name: String, val value: Dp)

@Composable
fun SpacingBoard(modifier: Modifier = Modifier) {
    val s = DPTheme.spacing
    val entries = listOf(
        SpacingEntry("xxs", s.xxs),
        SpacingEntry("xs", s.xs),
        SpacingEntry("sm", s.sm),
        SpacingEntry("md", s.md),
        SpacingEntry("lg", s.lg),
        SpacingEntry("xl", s.xl),
        SpacingEntry("xxl", s.xxl),
        SpacingEntry("xxxl", s.xxxl),
        SpacingEntry("hero", s.hero),
        SpacingEntry("paragraphGap", s.paragraphGap),
        SpacingEntry("screenHorizontal", s.screenHorizontal),
        SpacingEntry("sectionGap", s.sectionGap),
        SpacingEntry("touchTarget", s.touchTarget),
        SpacingEntry("listItemHeight", s.listItemHeight),
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(entries) { entry ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(entry.value)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary),
                )
                Text(
                    text = "${entry.name} — ${entry.value}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
