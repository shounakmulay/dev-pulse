package dev.shounakmulay.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable() () -> Unit
) {
    val platformColorScheme = getColorScheme(
        dynamicColor = dynamicColor,
        darkTheme = darkTheme
    )
    val colorScheme = when {
        platformColorScheme != null -> platformColorScheme
        darkTheme -> darkScheme
        else -> lightScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography(),
        content = content
    )
}



