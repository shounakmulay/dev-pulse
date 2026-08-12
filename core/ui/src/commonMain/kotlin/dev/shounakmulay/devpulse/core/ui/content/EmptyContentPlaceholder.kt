package dev.shounakmulay.devpulse.core.ui.content

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
import androidx.compose.ui.graphics.vector.ImageVector
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.no_results_found
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyContentPlaceholder(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: String = stringResource(stringRes.no_results_found)
) {
    EmptyContentPlaceholder(
        modifier = modifier,
        icon = icon,
    ) {
        DPTextView(
            text = text,
            variant = DPTextViewVariant.TitleMedium
        )
    }
}

@Composable
fun EmptyContentPlaceholder(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon != null) {
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
                imageVector = icon,
                contentDescription = "",
                tint = Color.Unspecified
            )
        }
        Spacer(Modifier.size(LocalDPSpacing.current.xxl))
        content()
    }
}