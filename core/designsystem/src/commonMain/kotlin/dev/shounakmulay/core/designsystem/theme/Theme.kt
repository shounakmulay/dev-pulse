package dev.shounakmulay.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = appTypography(),
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}



