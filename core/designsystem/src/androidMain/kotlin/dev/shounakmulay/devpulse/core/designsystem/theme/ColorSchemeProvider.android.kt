package dev.shounakmulay.devpulse.core.designsystem.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getColorScheme(dynamicColor: Boolean, darkTheme: Boolean): State<ColorScheme?>? {
    return if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        remember(darkTheme, context) {
            derivedStateOf {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
        }
    } else null
}
