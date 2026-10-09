package dev.shounakmulay.devpulse.core.navigation.scene.listDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
internal fun RowScope.DragIndicator(
    isDetailExpanded: Boolean,
    totalWidthPx: Float,
    splitRatio: MutableState<Float>
) {
    var isDragging by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "DragIndicatorScale"
    )

    AnimatedVisibility(
        visible = !isDetailExpanded,
        enter = expandHorizontally(expandFrom = Alignment.Start),
        exit = shrinkHorizontally(shrinkTowards = Alignment.Start),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(
                        alpha = 0.2f
                    )
                )
                .combinedClickable(
                    interactionSource = null,
                    indication = null,
                    onDoubleClick = {
                        splitRatio.value = if (1f - splitRatio.value < 0.5f) 0.33f else 0.67f
                    },
                    onClick = {}
                )
                .pointerInput(totalWidthPx) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()

                            val deltaRatio = dragAmount.x / totalWidthPx
                            splitRatio.value =
                                (splitRatio.value + deltaRatio)
                                    .coerceIn(0.33f, 0.67f)
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(40.dp)
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
            )
        }
    }
}