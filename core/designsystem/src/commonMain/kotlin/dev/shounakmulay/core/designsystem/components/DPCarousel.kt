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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.*

enum class DPCarouselVariant {
    MultiBrowse,
    Uncontained,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPCarousel(
    state: CarouselState,
    modifier: Modifier = Modifier,
    variant: DPCarouselVariant = DPCarouselVariant.MultiBrowse,
    preferredItemWidth: Dp = 200.dp,
    itemSpacing: Dp? = null,
    contentPadding: PaddingValues? = null,
    userScrollEnabled: Boolean = true,
    flingBehavior: TargetedFlingBehavior? = null,
    content: @Composable CarouselItemScope.(itemIndex: Int) -> Unit,
) {
    val resolvedSpacing = itemSpacing ?: DPTheme.spacing.sm
    val resolvedContentPadding = contentPadding
        ?: PaddingValues(horizontal = DPTheme.spacing.lg, vertical = 0.dp)
    when (variant) {
        DPCarouselVariant.MultiBrowse -> {
            val resolvedFling = flingBehavior
                ?: CarouselDefaults.singleAdvanceFlingBehavior(state = state)
            HorizontalMultiBrowseCarousel(
                state = state,
                preferredItemWidth = preferredItemWidth,
                modifier = modifier,
                itemSpacing = resolvedSpacing,
                flingBehavior = resolvedFling,
                userScrollEnabled = userScrollEnabled,
                minSmallItemWidth = CarouselDefaults.MinSmallItemSize,
                maxSmallItemWidth = CarouselDefaults.MaxSmallItemSize,
                contentPadding = resolvedContentPadding,
                content = content,
            )
        }
        DPCarouselVariant.Uncontained -> {
            val resolvedFling = flingBehavior
                ?: CarouselDefaults.noSnapFlingBehavior()
            HorizontalUncontainedCarousel(
                state = state,
                itemWidth = preferredItemWidth,
                modifier = modifier,
                itemSpacing = resolvedSpacing,
                flingBehavior = resolvedFling,
                userScrollEnabled = userScrollEnabled,
                contentPadding = resolvedContentPadding,
                content = content,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@DPComponentPreview
@Composable
private fun DPCarouselPreview() {
    Preview {
        val state = rememberCarouselState { 8 }
        DPCarousel(
            state = state,
            modifier = Modifier.fillMaxWidth().height(160.dp),
        ) { index ->
            Surface(
                modifier = Modifier.maskClip(MaterialTheme.shapes.large),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Text("Item $index", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
