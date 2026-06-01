package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedPostListItem
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.FeedsPostListItemVariant
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PostListScreen(
    navigator: Navigator,
    viewModel: PostListViewModel = koinViewModel(),
) {
    val posts = viewModel.recentPosts.collectAsStateWithLifecycle().value

    Screen(
        viewModel = viewModel,
        onEffect = { },
    ) {
        Column {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(posts) { post ->
                    FeedPostListItem(
                        post = post,
                        variant = FeedsPostListItemVariant.L,
                        onBookmarkChanged = { selectedPost, bookmarked ->
                            viewModel.onEvent(
                                PostListScreenEvent.OnPostBookmarkChanged(
                                    postId = selectedPost.id,
                                    bookmarked = bookmarked
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}
