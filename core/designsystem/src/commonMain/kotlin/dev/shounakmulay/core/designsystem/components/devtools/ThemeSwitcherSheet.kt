package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.darkScheme
import dev.shounakmulay.core.designsystem.theme.highContrastDarkColorScheme
import dev.shounakmulay.core.designsystem.theme.highContrastLightColorScheme
import dev.shounakmulay.core.designsystem.theme.lightScheme
import dev.shounakmulay.core.designsystem.theme.mediumContrastDarkColorScheme
import dev.shounakmulay.core.designsystem.theme.mediumContrastLightColorScheme

sealed interface ThemeMode {
    val label: String

    data object Light : ThemeMode { override val label = "Light" }
    data object Dark : ThemeMode { override val label = "Dark" }
    data object MediumContrastLight : ThemeMode { override val label = "Medium Contrast · Light" }
    data object MediumContrastDark : ThemeMode { override val label = "Medium Contrast · Dark" }
    data object HighContrastLight : ThemeMode { override val label = "High Contrast · Light" }
    data object HighContrastDark : ThemeMode { override val label = "High Contrast · Dark" }
    data object DynamicLight : ThemeMode { override val label = "Dynamic · Light" }
    data object DynamicDark : ThemeMode { override val label = "Dynamic · Dark" }
}

fun ThemeMode.toColorScheme(): ColorScheme = when (this) {
    ThemeMode.Light -> lightScheme
    ThemeMode.Dark -> darkScheme
    ThemeMode.MediumContrastLight -> mediumContrastLightColorScheme
    ThemeMode.MediumContrastDark -> mediumContrastDarkColorScheme
    ThemeMode.HighContrastLight -> highContrastLightColorScheme
    ThemeMode.HighContrastDark -> highContrastDarkColorScheme
    ThemeMode.DynamicLight -> lightScheme
    ThemeMode.DynamicDark -> darkScheme
}

private val presetModes: List<ThemeMode> = listOf(
    ThemeMode.Light,
    ThemeMode.Dark,
    ThemeMode.MediumContrastLight,
    ThemeMode.MediumContrastDark,
    ThemeMode.HighContrastLight,
    ThemeMode.HighContrastDark,
)

private val dynamicModes: List<ThemeMode> = listOf(
    ThemeMode.DynamicLight,
    ThemeMode.DynamicDark,
)

@Composable
fun ThemeSwitcherSheetContent(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Preset Themes",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        presetModes.forEach { mode ->
            ListItem(
                headlineContent = { Text(mode.label) },
                trailingContent = {
                    if (selected == mode) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                },
                modifier = Modifier.clickable { onSelect(mode) },
            )
        }
        Text(
            text = "Dynamic Color",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
        )
        dynamicModes.forEach { mode ->
            ListItem(
                headlineContent = { Text(mode.label) },
                supportingContent = { Text("Uses wallpaper colors on Android 12+") },
                trailingContent = {
                    if (selected == mode) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                },
                modifier = Modifier.clickable { onSelect(mode) },
            )
        }
    }
}

@DPComponentPreview
@Composable
private fun ThemeSwitcherSheetContentPreview() {
    Preview {
        ThemeSwitcherSheetContent(
            selected = ThemeMode.Light,
            onSelect = {},
        )
    }
}
