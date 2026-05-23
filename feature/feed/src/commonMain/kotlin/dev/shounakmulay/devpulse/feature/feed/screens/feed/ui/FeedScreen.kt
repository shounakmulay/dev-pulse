package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPClickableRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun FeedScreen(
    navigator: Navigator,
    viewModel: FeedViewModel,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(
    )
    val appBarWithSearchColors =
        SearchBarDefaults.appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
        )
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = {
                    val scope = rememberCoroutineScope()
                    AnimatedVisibility(
                        visible = searchBarState.targetValue == SearchBarValue.Expanded,
                        enter = slideInHorizontally() + fadeIn(),
                        exit = slideOutHorizontally() + fadeOut()
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "",
                            modifier = Modifier.clickable {
                                scope.launch {
                                    searchBarState.animateToCollapsed()
                                }
                            }
                        )
                    }
                },
            )
        }
    Screen(
        viewModel = viewModel,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topAppBar = {
            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                colors = appBarWithSearchColors,
                inputField = inputField,
                navigationIcon = {
                    DPTextView(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Feed",
                        variant = DPTextViewVariant.TitleLarge
                    )
                },
                actions = {
                    DPIconButton(
                        DPIcons.RssFeed,
                        contentDescription = "",
                        onClick = {
                            navigator.navigate(Screen.Tabs.Feed.AddFeed, onRootStack = true)
                        })
                },
            )
            ExpandedFullScreenContainedSearchBar(
                state = searchBarState,
                inputField = inputField,
                colors = appBarWithSearchColors.searchBarColors,
            ) {
                LazyColumn {
                    items(10) {
                        DPClickableRow(
                            onClick = { },
                            content = {
                                DPTextView(
                                    text = "Search Item $it",
                                    variant = DPTextViewVariant.BodyMedium
                                )
                            },
                        )
                    }
                }
            }
        },
        onEffect = {
            when (it) {
                else -> viewModel.unhandledEffect(it)
            }
        },
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            LazyColumn {
                items(100) {
                    DPClickableRow(
                        onClick = { navigator.replaceOfSameType(Screen.Tabs.Feed.FeedDetail(it.toString())) },
                        content = {
                            DPTextView(
                                text = "Item $it",
                                variant = DPTextViewVariant.BodyMedium
                            )
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedsSearchBar(modifier: SearchBarState) {
    var query by remember {
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    AppBarWithSearch(
        state = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    query = it
                },
                onSearch = {
                    expanded = !expanded
                },
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                }
            )
        },
    )

}

@Composable
fun FeedDetailScreen(route: Screen.Tabs.Feed.FeedDetail) {
    Column {
        DPTextView(
            text = "Detail ${route.id}",
            variant = DPTextViewVariant.DisplayLargeEmphasized
        )
    }
}
