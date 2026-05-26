package dev.shounakmulay.devpulse.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import dev.shounakmulay.devpulse.core.logging.DPLog

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    colorScheme: ColorScheme,
    isDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(colorScheme, isDarkTheme) {
        DPLog.e("THEME") {
            "colorScheme: ${colorScheme.hashCode()} | isDarkTheme = $isDarkTheme"
        }
    }

    val contextColors = remember(isDarkTheme) {
        if (isDarkTheme) darkDPContextColors else lightDPContextColors
    }

    CompositionLocalProvider(
        LocalDPSpacing provides DefaultSpacing,
        LocalDPIconSize provides DefaultIconSize,
        LocalDPElevation provides DefaultElevation,
        LocalDPDarkTheme provides isDarkTheme,
        LocalDPContextColors provides contextColors
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = appTypography(),
            shapes = dpShapes,
            motionScheme = calmMotionScheme(),
            content = content,
        )
    }
}

object DPTheme {
    val spacing: DPSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalDPSpacing.current

    val iconSize: DPIconSize
        @Composable
        @ReadOnlyComposable
        get() = LocalDPIconSize.current

    val elevation: DPElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalDPElevation.current

    val isDarkTheme: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalDPDarkTheme.current
}
