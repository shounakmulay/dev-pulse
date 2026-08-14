package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.bottomsheet.rememberDPModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.content.ContentTextSettingsBottomSheet
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components.PostDetailContent
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components.PostDetailFloatingToolbar
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components.PostDetailTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel,
    navigator: Navigator
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Screen<PostDetailScreenState, PostDetailScreenEffect, PostDetailViewModel>(
        modifier = Modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            PostDetailTopAppBar(navigator, topAppBarScrollBehavior)
        },
        onEffect = {
            viewModel.unhandledEffect(it)
        }
    ) { state ->
        val visible by remember {
            derivedStateOf {
                topAppBarScrollBehavior.state.collapsedFraction <= 0.5f
            }
        }
        val scope = rememberCoroutineScope()
        val contentTextSettingsSheetController = rememberDPModalBottomSheetController(
            skipPartiallyExpanded = true
        )
        val listState = rememberLazyListState()

        PostDetailFloatingToolbar(
            visible = visible,
            selectedSection = state.selectedSection,
            onSectionSelected = {
                if (state.selectedSection == it) {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                } else {
                    viewModel.onEvent(PostDetailScreenEvent.OnSectionSelected(it))
                }
            }
        )

        PostDetailContent(
            state = state,
            listState = listState,
            onBookmarkChanged = {
                viewModel.onEvent(PostDetailScreenEvent.SetPostBookmarked(it))
            },
            onOpenInWebView = {
                navigator.navigate(Screen.WebView(it))
            },
            onOpenContentTextSettings = {
                contentTextSettingsSheetController.show()
            },
            onImageClick = {
                navigator.navigate(Screen.Tabs.Feed.ImageScreen(it))
            }
        )

        ContentTextSettingsBottomSheet(
            controller = contentTextSettingsSheetController,
            onTextScaleChanged = {
                viewModel.onEvent(PostDetailScreenEvent.SetContentTextScale(it))
            },
            onLineHeightScaleChanged = {
                viewModel.onEvent(PostDetailScreenEvent.SetContentLineHeightScale(it))
            }
        )
    }
}









