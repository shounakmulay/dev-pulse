package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPHorizontalMultiBrowseCarousel(
    state: CarouselState,
    preferredItemWidth: Dp,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 0.dp,
    flingBehavior: TargetedFlingBehavior =
        CarouselDefaults.singleAdvanceFlingBehavior(state = state),
    userScrollEnabled: Boolean = true,
    minSmallItemWidth: Dp = CarouselDefaults.MinSmallItemSize,
    maxSmallItemWidth: Dp = CarouselDefaults.MaxSmallItemSize,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit,
) {
    HorizontalMultiBrowseCarousel(
        state = state,
        preferredItemWidth = preferredItemWidth,
        modifier = modifier,
        itemSpacing = itemSpacing,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        minSmallItemWidth = minSmallItemWidth,
        maxSmallItemWidth = maxSmallItemWidth,
        contentPadding = contentPadding,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPHorizontalUncontainedCarousel(
    state: CarouselState,
    itemWidth: Dp,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 0.dp,
    flingBehavior: TargetedFlingBehavior = CarouselDefaults.noSnapFlingBehavior(),
    userScrollEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit,
) {
    HorizontalUncontainedCarousel(
        state = state,
        itemWidth = itemWidth,
        modifier = modifier,
        itemSpacing = itemSpacing,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        contentPadding = contentPadding,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@DPComponentPreview
@Composable
private fun DPCarouselPreview() {
    Preview {
        val state = rememberCarouselState { 5 }
        DPHorizontalUncontainedCarousel(
            state = state,
            itemWidth = 120.dp,
            modifier = Modifier.fillMaxWidth().height(160.dp).padding(8.dp),
        ) {
            Surface(
                modifier = Modifier.maskClip(shape = MaterialTheme.shapes.large),
                color = Color.Magenta.copy(alpha = 0.3f),
            ) {
                Text(
                    text = "Item $it",
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}
