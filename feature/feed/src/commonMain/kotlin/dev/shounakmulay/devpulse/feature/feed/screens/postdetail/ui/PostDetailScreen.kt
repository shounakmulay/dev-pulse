package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPLoadingIndicator
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown.DPMarkdown

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel,
    navigator: Navigator
) {
    Screen<PostDetailScreenState, PostDetailScreenEffect, PostDetailViewModel>(
        viewModel = viewModel,
        topAppBar = {
            DPTopAppBar(
                title = post?.post?.title.orEmpty(),
                navigationIcon = {
                    DPBackNavigationIconButton {
                        navigator.navigateBack()
                    }
                },
                actions = {

                }
            )
        },
        onEffect = {
            viewModel.unhandledEffect(it)
        }
    ) { state ->
        val content = state.content
        if (content == null) {
            DPLoadingIndicator(Modifier.align(Alignment.Center))
        } else {
            DPMarkdown(content.content)
        }
    }
}









