package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.add_feed_action_import
import org.jetbrains.compose.resources.stringResource

@Composable
fun BoxScope.ImportButton(
    onClick: () -> Unit
) {
    DPButton(
        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(0.5f)
            .padding(
                vertical = LocalDPSpacing.current.md,
                horizontal = LocalDPSpacing.current.md
            ),
        text = stringResource(stringRes.add_feed_action_import),
        size = DPSize.Large,
        variant = DPButtonVariant.Primary,
        style = DPButtonStyle.Elevated,
        elevation = ButtonDefaults.elevatedButtonElevation(),
        onClick = onClick
    )
}
