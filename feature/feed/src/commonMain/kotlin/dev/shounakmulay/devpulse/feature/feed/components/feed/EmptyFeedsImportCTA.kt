package dev.shounakmulay.devpulse.feature.feed.components.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.add_feed_action_import
import devpulse.core.resources.generated.resources.feed_empty_imported
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EmptyFeedsImportCTA(modifier: Modifier = Modifier, onNavigateToAddFeed: () -> Unit) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            LocalDPSpacing.current.sm,
            Alignment.CenterVertically
        )
    ) {
        DPTextView(
            text = stringResource(stringRes.feed_empty_imported),
            variant = DPTextViewVariant.TitleMedium
        )
        DPButton(
            text = stringResource(stringRes.add_feed_action_import),
            onClick = onNavigateToAddFeed
        )
    }
}