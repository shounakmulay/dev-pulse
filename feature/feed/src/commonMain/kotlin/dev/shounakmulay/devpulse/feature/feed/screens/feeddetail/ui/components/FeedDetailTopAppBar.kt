package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.FeedDetailScreenState

@Composable
internal fun FeedDetailScreenState.FeedDetailTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    lazyGridState: LazyGridState,
    navigator: Navigator,
    onPinToggled: () -> Unit,
) {
    val showTitle by remember {
        derivedStateOf {
            val collapsed = scrollBehavior.state.collapsedFraction < 0.5f
            val pastFirstItem = lazyGridState.firstVisibleItemIndex > 0
            val pastScrollOffsetOnFirstItem = lazyGridState.firstVisibleItemIndex == 0
                    && lazyGridState.firstVisibleItemScrollOffset > 400
            collapsed && (pastFirstItem || pastScrollOffsetOnFirstItem)
        }
    }
    DPTopAppBar(
        title = feed?.title?.takeIf { showTitle }
            .orEmpty(),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            DPBackNavigationIconButton {
                navigator.navigateBack()
            }
        },
        actions = {
            DPIconButton(
                modifier = Modifier.rotate(45f),
                icon = if (feed?.pinned == true) DPIcons.Pin else DPIcons.PinOutlined,
                contentDescription = "",
                variant = DPIconButtonVariant.Tertiary,
                onClick = onPinToggled
            )
        }
    )
}