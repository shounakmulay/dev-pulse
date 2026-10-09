package dev.shounakmulay.devpulse.core.navigation.scene.listDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry

@Composable
internal fun <T: Any> RowScope.ListView(
    listEntry: NavEntry<T>,
    maxWidth: Dp,
    isDetailPresent: Boolean,
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
        val shapes = MaterialTheme.shapes
        val clipShape = remember(isDetailPresent, shapes) {
            if (isDetailPresent) {
                shapes.extraLarge.copy(
                    topStart = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                )
            } else {
                RectangleShape
            }
        }
        Column(
            modifier = Modifier
                .width(maxWidth * split)
                .fillMaxHeight()
                .clip(clipShape)
        ) {
            listEntry.Content()
        }
    }
}