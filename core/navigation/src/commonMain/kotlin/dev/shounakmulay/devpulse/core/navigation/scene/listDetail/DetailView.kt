package dev.shounakmulay.devpulse.core.navigation.scene.listDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry

@Composable
internal fun <T : Any> RowScope.DetailView(
    detailEntry: NavEntry<T>?,
    split: Float,
    maxWidth: Dp,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onDispose: () -> Unit
) {
    CompositionLocalProvider(
        LocalExpandableDetailPaneContext provides ExpandableDetailPaneContext(
            isExpanded = isExpanded,
            onToggleExpanded = onToggleExpanded
        )
    ) {
        AnimatedVisibility(
            visible = detailEntry != null,
            enter = expandHorizontally(expandFrom = Alignment.Start) +
                    slideInHorizontally { -it },
            exit = shrinkHorizontally(shrinkTowards = Alignment.Start) +
                    slideOutHorizontally { -it },
        ) {
            DisposableEffect(Unit) {
                onDispose {
                    onDispose()
                }
            }
            val shapes = MaterialTheme.shapes
            val clipShape = remember(isExpanded, shapes) {
                if (isExpanded) {
                    RectangleShape
                } else {
                    shapes.extraLarge.copy(
                        topEnd = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(maxWidth * (1f - split))
                    .fillMaxHeight()
                    .clip(clipShape)
            ) {
                detailEntry?.Content()
            }
        }
    }
}