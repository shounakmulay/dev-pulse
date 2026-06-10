package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs.Feed.FeedDetail
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.screen.Screen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedDetailScreen(
    route: FeedDetail,
    navigator: Navigator,
    viewModel: FeedDetailViewModel,
) {
    Screen(
        viewModel = viewModel,
        topAppBar = {
            DPTopAppBar(
                title = "",
                navigationIcon = {
                    DPBackNavigationIconButton { navigator.navigateBack() }
                }
            )
        },
        onEffect = {},
    ) { state ->

        if (state.isLoading || state.feed == null || state.uiFeed == null) return@Screen LoadingIndicator(
            modifier = Modifier.align(
                Alignment.Center
            )
        )

        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(300.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(Modifier) {
                    FlowRow(
                        modifier = Modifier
                            .padding(LocalDPSpacing.current.md),
                        horizontalArrangement = Arrangement.spacedBy(
                            LocalDPSpacing.current.xl,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            LocalDPSpacing.current.xl,
                            Alignment.CenterVertically
                        )
                    ) {
                        Box(Modifier.fillMaxRowHeight(), contentAlignment = Alignment.Center) {
                            DPFeedImage(
                                modifier = Modifier.size(64.dp).clip(CircleShape),
                                url = state.uiFeed.websiteImageUrl,
                                initials = state.uiFeed.initials,
                                feedTitle = state.uiFeed.title
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxRowHeight().weight(1f).widthIn(min = 350.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DPTextView(
                                text = state.uiFeed.title,
                                variant = DPTextViewVariant.HeadingMediumEmphasized,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    state.feed.description?.let { description ->
                        DPTextView(
                            text = description,
                            variant = DPTextViewVariant.BodyMedium,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
