package dev.shounakmulay.feature.feed.screens.feed.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.shounakmulay.core.designsystem.components.DPClickableRow
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FeedScreen(
    navigator: Navigator,
    viewModel: FeedViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.collectAsState()
    LazyColumn {
        items(100) {
            DPClickableRow(
                onClick = { navigator.replaceOfSameType(Screen.Tabs.Feed.FeedDetail(it)) },
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

@Composable
fun FeedDetailScreen(route: Screen.Tabs.Feed.FeedDetail) {
    Column {
        DPTextView(
            text = "Detail ${route.id}",
            variant = DPTextViewVariant.DisplayLargeEmphasized
        )
    }
}
