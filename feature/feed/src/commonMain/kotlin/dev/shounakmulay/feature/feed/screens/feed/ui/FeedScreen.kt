package dev.shounakmulay.feature.feed.screens.feed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPElevatedButton
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.core.navigation.Navigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FeedScreen(
    navigator: Navigator,
    viewModel: FeedViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.collectAsState()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            DPTextView(
                text = state.count.toString(),
                variant = DPTextViewVariant.DisplayMedium
            )
            Spacer(Modifier.height(16.dp))
            DPElevatedButton(onClick = viewModel::increment) {
                DPTextView(text = "Increment", variant = DPTextViewVariant.LabelMedium)
            }
        }
    }
}
