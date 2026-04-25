package dev.shounakmulay.devpulse.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val platformColorScheme = getColorScheme(
        dynamicColor = dynamicColor,
        darkTheme = darkTheme,
    )
    val colorScheme = when {
        platformColorScheme != null -> platformColorScheme
        darkTheme -> darkScheme
        else -> lightScheme
    }
    CompositionLocalProvider(
        LocalDPSpacing provides DefaultSpacing,
        LocalDPIconSize provides DefaultIconSize,
        LocalDPElevation provides DefaultElevation,
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
}
