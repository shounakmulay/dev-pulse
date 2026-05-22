package dev.shounakmulay.devpulse.core.designsystem.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import dev.shounakmulay.devpulse.core.designsystem.theme.AppTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.darkScheme
import dev.shounakmulay.devpulse.core.designsystem.theme.lightScheme

@PreviewFontScale
@PreviewDynamicColors
@PreviewLightDark
annotation class DPComponentPreview

@PreviewLightDark
@PreviewScreenSizes
annotation class DPScreenPreview

@Composable
fun Preview(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) darkScheme else lightScheme
    AppTheme(
        colorScheme = colorScheme,
        isDarkTheme = isSystemInDarkTheme()
    ) {
        Surface {
            content()
        }
    }
}