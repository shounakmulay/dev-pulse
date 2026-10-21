package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSearchTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenu
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.FeedDetailScreenState
import devpulse.core.resources.generated.resources.more_options
import devpulse.core.resources.generated.resources.pin_feed
import devpulse.core.resources.generated.resources.post_search
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCalfUiApi::class)
@Composable
internal fun FeedDetailScreenState.FeedDetailTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navigator: Navigator,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onPinToggled: () -> Unit,
    onShowOptions: () -> Unit,
    onDismissOptions: () -> Unit,
    onMenuItemSelected: (FeedOptionsMenuItem) -> Unit
) {
    DPSearchTopAppBar(
        navigationIcon = {
            DPBackNavigationIconButton {
                navigator.navigateBack()
            }
        },
        scrollBehavior = scrollBehavior,
        appBarColors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        actions = {
            DPIconButton(
                modifier = Modifier.rotate(45f),
                icon = if (uiFeed?.pinned == true) DPIcons.Pin else DPIcons.PinOutlined,
                contentDescription = stringResource(stringRes.pin_feed),
                variant = DPIconButtonVariant.Tertiary,
                onClick = onPinToggled
            )
            Box {
                DPIconButton(
                    icon = DPIcons.MoreOptionsVert,
                    contentDescription = stringResource(stringRes.more_options),
                    variant = DPIconButtonVariant.Tertiary,
                    onClick = onShowOptions
                )
                (feedOptions as? FeedOptionsState.Open)?.target?.let {
                    FeedOptionsMenu(
                        expanded = true,
                        onDismissRequest = onDismissOptions,
                        menuItems = it.items,
                        onMenuItemSelected = onMenuItemSelected
                    )
                }
            }
        },
        textValue = searchText,
        onTextValueChange = onSearchTextChange,
        placeholder = stringResource(stringRes.post_search)
    )
}
