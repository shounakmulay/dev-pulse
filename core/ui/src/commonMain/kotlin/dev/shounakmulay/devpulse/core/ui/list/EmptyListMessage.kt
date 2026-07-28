package dev.shounakmulay.devpulse.core.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.no_results_found
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyListMessage(
    modifier: Modifier = Modifier,
    text: String = stringResource(stringRes.no_results_found)
) {
    EmptyListMessage(modifier = modifier) {
        DPTextView(
            text = text,
            variant = DPTextViewVariant.HeadingSmall
        )
    }
}

@Composable
fun EmptyListMessage(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
        val widthFraction = remember(windowSizeClass) {
            if (windowSizeClass.minWidthDp > windowSizeClass.minHeightDp) {
                .25f
            } else {
                .5f
            }
        }
        Icon(
            modifier = Modifier.fillMaxWidth(widthFraction),
            imageVector = DPIcons.EmptyList,
            contentDescription = "",
            tint = Color.Unspecified
        )
        Spacer(Modifier.size(LocalDPSpacing.current.xxl))
        content()
    }
}