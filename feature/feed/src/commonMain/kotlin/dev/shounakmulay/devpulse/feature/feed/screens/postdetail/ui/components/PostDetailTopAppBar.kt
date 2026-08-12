package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components

import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.PostDetailScreenState

@Composable
internal fun PostDetailScreenState.PostDetailTopAppBar(
    navigator: Navigator,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
) {
    DPTopAppBar(
        title = post?.title.orEmpty(),
        navigationIcon = {
            DPBackNavigationIconButton {
                navigator.navigateBack()
            }
        },
        scrollBehavior = topAppBarScrollBehavior,
        actions = {
            DPIconButton(
                icon = DPIcons.MoreOptionsVert,
                variant = DPIconButtonVariant.Tertiary,
                contentDescription = "",
                size = DPSize.Small
            ) {

            }
        }
    )
}