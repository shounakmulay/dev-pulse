package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.feed_list_empty
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyFeedList() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        DPTextView(
            text = stringResource(stringRes.feed_list_empty),
            variant = DPTextViewVariant.BodyMedium,
        )
    }
}
