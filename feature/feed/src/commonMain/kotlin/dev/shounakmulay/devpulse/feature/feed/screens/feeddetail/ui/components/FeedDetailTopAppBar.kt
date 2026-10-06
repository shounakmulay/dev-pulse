package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import dev.shounakmulay.devpulse.core.designsystem.components.DPAlertDialog
import dev.shounakmulay.devpulse.core.designsystem.components.DPDialogVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPDropdownMenu
import dev.shounakmulay.devpulse.core.designsystem.components.DPDropdownMenuItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.FeedDetailScreenState
import devpulse.core.resources.generated.resources.cancel
import devpulse.core.resources.generated.resources.confirm_delete_feed_message
import devpulse.core.resources.generated.resources.confirm_delete_feed_title
import devpulse.core.resources.generated.resources.delete
import devpulse.core.resources.generated.resources.feed_lowercase
import devpulse.core.resources.generated.resources.more_options
import devpulse.core.resources.generated.resources.pin_feed
import devpulse.core.resources.generated.resources.share
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCalfUiApi::class)
@Composable
internal fun FeedDetailScreenState.FeedDetailTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    lazyGridState: LazyGridState,
    navigator: Navigator,
    onPinToggled: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        actions = {
            DPIconButton(
                modifier = Modifier.rotate(45f),
                icon = if (feed?.pinned == true) DPIcons.Pin else DPIcons.PinOutlined,
                contentDescription = stringResource(stringRes.pin_feed),
                variant = DPIconButtonVariant.Tertiary,
                onClick = onPinToggled
            )
            Box {
                var menuExpanded by rememberSaveable {
                    mutableStateOf(false)
                }
                var deleteDialogVisible by rememberSaveable {
                    mutableStateOf(false)
                }
                DPIconButton(
                    icon = DPIcons.MoreOptionsVert,
                    contentDescription = stringResource(stringRes.more_options),
                    variant = DPIconButtonVariant.Tertiary,
                    onClick = { menuExpanded = true }
                )
                DPDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DPDropdownMenuItem(
                        text = stringResource(stringRes.delete),
                        trailingIcon = DPIcons.Delete,
                        onClick = {
                            menuExpanded = false
                            deleteDialogVisible = true
                        },
                    )
                    DPDropdownMenuItem(
                        text = stringResource(stringRes.share),
                        trailingIcon = DPIcons.Share,
                        onClick = {
                            menuExpanded = false
                            onShare()
                        },
                    )
                }
                if (deleteDialogVisible) {
                    DPAlertDialog(
                        icon = DPIcons.DeleteForever,
                        title = stringResource(stringRes.confirm_delete_feed_title),
                        message = stringResource(
                            stringRes.confirm_delete_feed_message,
                            feed?.title ?: stringResource(stringRes.feed_lowercase)
                        ),
                        variant = DPDialogVariant.Destructive,
                        confirmText = stringResource(stringRes.delete),
                        dismissText = stringResource(stringRes.cancel),
                        onConfirm = {
                            deleteDialogVisible = false
                            onDelete()
                        },
                        onDismissRequest = {
                            deleteDialogVisible = false
                        },
                    )
                }
            }
        }
    )
}