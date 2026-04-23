package dev.shounakmulay.core.navigation.scene.listDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene

// TODO: @Shounak Make the class configurable
class ExpandableListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?,
    draggable: Boolean,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOfNotNull(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        var isDetailExpanded = remember { mutableStateOf(false) }
        var splitTarget = remember { mutableFloatStateOf(1f) }
        val split by animateFloatAsState(splitTarget.value)

        val isDetailPresent by remember(detailEntry) {
            derivedStateOf {
                detailEntry != null
            }
        }

        LaunchedEffect(isDetailPresent) {
            splitTarget.value = if (isDetailPresent) 0.33f else 1f
        }

        LaunchedEffect(detailEntry) {
            if (detailEntry == null) {
                isDetailExpanded.value = false
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            val totalWidthPx = with(density) { maxWidth.toPx() }

            Row(modifier = Modifier.fillMaxSize()) {
                ListView(
                    maxWidth = this@BoxWithConstraints.maxWidth,
                    isDetailExpanded = isDetailExpanded.value,
                    split = split
                )

                if (draggable) {
                    DragIndicator(
                        isDetailExpanded = isDetailExpanded.value,
                        totalWidthPx = totalWidthPx,
                        splitRatio = splitTarget
                    )
                }

                DetailView(split = split)
            }
        }
    }

    @Composable
    private fun RowScope.DetailView(
        split: Float,
    ) {
        AnimatedVisibility(
            visible = detailEntry != null,
            enter = expandHorizontally(expandFrom = Alignment.Start) +
                    slideInHorizontally { -it },
            exit = shrinkHorizontally(shrinkTowards = Alignment.Start) +
                    slideOutHorizontally { -it },
        ) {
            Box(
                modifier = Modifier.weight(split)
                    .fillMaxHeight()
            ) {
                detailEntry?.Content()
            }
        }
    }

    @Composable
    private fun RowScope.DragIndicator(
        isDetailExpanded: Boolean,
        totalWidthPx: Float,
        splitRatio: MutableState<Float>
    ) {
        AnimatedVisibility(
            visible = !isDetailExpanded,
            enter = expandHorizontally(expandFrom = Alignment.Start),
            exit = shrinkHorizontally(shrinkTowards = Alignment.Start),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    )
                    .pointerInput(totalWidthPx) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaRatio = dragAmount.x / totalWidthPx
                            splitRatio.value = (splitRatio.value + deltaRatio).coerceIn(0.2f, 0.8f)
                        }
                    },

            ) {
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(40.dp)
                        .align(Alignment.Center)
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = MaterialTheme.shapes.extraLarge,
                        )
                )
            }
        }
    }

    @Composable
    private fun ListView(
        maxWidth: Dp,
        isDetailExpanded: Boolean,
        split: Float
    ) {
        AnimatedVisibility(
            visible = !isDetailExpanded,
            enter = expandHorizontally(expandFrom = Alignment.Start) +
                    slideInHorizontally { -it },
            exit = shrinkHorizontally(shrinkTowards = Alignment.Start) +
                    slideOutHorizontally { -it },
        ) {
            Column(
                modifier = Modifier.width(maxWidth * split)
                    .fillMaxHeight()
            ) {
                listEntry.Content()
            }
        }
    }
}