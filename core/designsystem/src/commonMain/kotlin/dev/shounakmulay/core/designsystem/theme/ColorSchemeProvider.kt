package dev.shounakmulay.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
expect fun getColorScheme(dynamicColor: Boolean, darkTheme: Boolean): ColorScheme?