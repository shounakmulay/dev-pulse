package dev.shounakmulay.devpulse.feature.feed.screens.feedsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.text.asAnnotatedString
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.core.ui.screen.Screen as MviScreen
import dev.shounakmulay.devpulse.feature.feed.components.search.FeedSearchBar

@Composable
fun FeedSearchScreen(viewModel: FeedSearchViewModel, navigator: Navigator) {
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(lazyListState, keyboardController) {
        snapshotFlow { lazyListState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolling) {
                keyboardController?.hide()
            }
        }
    }

    MviScreen(
        viewModel = viewModel,
        topAppBar = {
            FeedSearchBar(
                onNavigateBack = navigator::navigateBack,
                query = searchQuery,
                onQueryChange = { viewModel.onEvent(FeedSearchScreenEvent.OnSearchQueryChanged(it)) },
            )
        },
        onEffect = { viewModel.unhandledEffect(it) },
    ) { state ->
        Column {
            if (state.searchLoading) {
                DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(state = lazyListState) {
                items(state.searchResults) { searchResult ->
                    Row(
                        modifier = Modifier
                            .padding(LocalDPSpacing.current.md)
                            .clickable(enabled = true) {
                                navigator.navigate(
                                    Screen.Tabs.Feed.FeedDetail(searchResult.id),
                                    onRootStack = true,
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DPFeedImage(
                            modifier = Modifier.size(24.dp).clip(CircleShape),
                            url = searchResult.websiteImageUrl,
                            initials = searchResult.initials,
                            feedTitle = searchResult.title.asString(),
                        )
                        Spacer(Modifier.width(LocalDPSpacing.current.md))
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            DPTextView(
                                text = searchResult.title.asAnnotatedString(color = MaterialTheme.colorScheme.primary),
                                variant = DPTextViewVariant.TitleMedium
                            )
                            val description =
                                searchResult.description.asAnnotatedString(color = MaterialTheme.colorScheme.primary)
                            if (description.isNotBlank()) {
                                DPTextView(
                                    text = description,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    variant = DPTextViewVariant.BodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
