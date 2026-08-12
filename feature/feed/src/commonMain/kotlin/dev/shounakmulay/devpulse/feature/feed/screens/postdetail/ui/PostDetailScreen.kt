package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components.PostDetailContent
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components.PostDetailFloatingToolbar
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.components.PostDetailTopAppBar

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

        PostDetailFloatingToolbar(
            visible = visible,
            selectedSection = state.selectedSection,
            onSectionSelected = {
               viewModel.onEvent(PostDetailScreenEvent.OnSectionSelected(it))
            }
        )

        PostDetailContent(
            state = state,
            onBookmarkChanged = {
                viewModel.onEvent(PostDetailScreenEvent.SetPostBookmarked(it))
            },
            onOpenInWebView = {
                navigator.navigate(Screen.WebView(it))
            }
        )
    }
}









