package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.shounakmulay.devpulse.core.designsystem.components.DPCircularProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.feed_loading_more_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppendLoadingRow() {
    val loadingMoreContentDescription = stringResource(stringRes.feed_loading_more_content_description)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalDPSpacing.current.listItemHeight)
            .semantics {
                contentDescription = loadingMoreContentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        DPCircularProgressIndicator()
    }
}
