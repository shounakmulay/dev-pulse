package dev.shounakmulay.devpulse.core.navigation.scene.listDetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene

// TODO: @Shounak Make the class configurable
class ExpandableListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?,
    draggable: Boolean,
    stateHolder: SaveableStateHolder,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOfNotNull(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        stateHolder.SaveableStateProvider(listEntry.contentKey) {
            val isDetailPresent = detailEntry != null

            val splitRatio = rememberSaveable {
                mutableFloatStateOf(0.33f)
            }
            var isDetailExpanded by rememberSaveable {
                mutableStateOf(false)
            }

            val expanded = isDetailPresent && isDetailExpanded

            val split by animateFloatAsState(
                targetValue = when {
                    !isDetailPresent -> 1f
                    expanded -> 0f
                    else -> splitRatio.floatValue
                }
            )

            LaunchedEffect(detailEntry) {
                if (detailEntry == null) {
                    isDetailExpanded = false
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                    )
                    .background(MaterialTheme.colorScheme.outlineVariant)
            ) {
                val density = LocalDensity.current
                val totalWidthPx = with(density) { maxWidth.toPx() }

                ExpandableListDetailContent(
                    maxWidth = maxWidth,
                    expanded = expanded,
                    isDetailPresent = isDetailPresent,
                    split = split,
                    draggable = draggable,
                    totalWidthPx = totalWidthPx,
                    splitRatio = splitRatio,
                    onSetDetailExpanded = {
                        isDetailExpanded = it
                    }
                )
            }
        }
    }

    @Composable
    fun ExpandableListDetailContent(
        maxWidth: Dp,
        expanded: Boolean,
        isDetailPresent: Boolean,
        split: Float,
        draggable: Boolean,
        totalWidthPx: Float,
        splitRatio: MutableState<Float>,
        onSetDetailExpanded: (Boolean) -> Unit
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            ListView(
                listEntry = listEntry,
                maxWidth = maxWidth,
                isDetailExpanded = expanded,
                isDetailPresent = isDetailPresent,
                split = split
            )

            if (draggable) {
                DragIndicator(
                    isDetailExpanded = expanded,
                    totalWidthPx = totalWidthPx,
                    splitRatio = splitRatio
                )
            }

            DetailView(
                detailEntry = detailEntry,
                maxWidth = maxWidth,
                split = split,
                isExpanded = expanded,
                onToggleExpanded = {
                    onSetDetailExpanded(!expanded)
                },
            )
        }
    }
}
