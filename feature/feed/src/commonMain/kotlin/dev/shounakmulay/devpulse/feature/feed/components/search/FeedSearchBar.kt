package dev.shounakmulay.devpulse.feature.feed.components.search

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import dev.shounakmulay.devpulse.core.designsystem.components.DPSearchTopAppBar
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.transition.sharedBounds
import devpulse.core.resources.generated.resources.feed_search
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FeedSearchBar(
    onNavigateBack: () -> Unit,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    val inputModifier = Modifier.sharedBounds(
        key = "feed-search-bar",
        clipShape = CircleShape,
    ).focusRequester(focusRequester)

    DPSearchTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = { modifier ->
            DPBackNavigationIconButton(modifier = modifier, onNavigateBack = onNavigateBack)
        },
        textValue = query,
        onTextValueChange = onQueryChange,
        placeholder = stringResource(stringRes.feed_search),
        inputModifier = inputModifier,
    )
}
