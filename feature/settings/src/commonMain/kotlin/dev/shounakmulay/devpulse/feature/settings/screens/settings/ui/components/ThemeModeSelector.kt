package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPSegmentedButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPSingleChoiceSegmentedButtonRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode

@Composable
internal fun ThemeModeSelector(
    selectedThemeMode: ThemeMode,
    onValueSelected: (ThemeMode) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelMedium
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val iconSize = SegmentedButtonDefaults.IconSize
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val showLabels = with(density) {
            val count = ThemeMode.entries.size
            val overlap = SegmentedButtonDefaults.BorderWidth.roundToPx()
            val width = (constraints.maxWidth + overlap * (count - 1)) / count
            val padding = SegmentedButtonDefaults.ContentPadding
            val contentInset = padding.calculateStartPadding(layoutDirection).roundToPx() +
                    padding.calculateEndPadding(layoutDirection).roundToPx() +
                    iconSize.roundToPx() + 8.dp.roundToPx()
            ThemeMode.entries.all { themeMode ->
                val textWidth = textMeasurer.measure(
                    text = themeMode.label(),
                    style = textStyle,
                    maxLines = 1,
                    softWrap = false,
                ).size.width
                textWidth <= width - contentInset
            }
        }
        DPSingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ThemeMode.entries.forEachIndexed { index, themeMode ->
                DPSegmentedButton(
                    selected = themeMode == selectedThemeMode,
                    onClick = { onValueSelected(themeMode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ThemeMode.entries.size
                    ),
                    icon = {
                        val icon = when (themeMode) {
                            ThemeMode.LIGHT -> DPIcons.LightTheme
                            ThemeMode.DARK -> DPIcons.DarkTheme
                            ThemeMode.SYSTEM -> DPIcons.SystemTheme
                        }
                        Icon(
                            imageVector = icon,
                            modifier = Modifier.size(iconSize),
                            contentDescription = themeMode.label()
                        )
                    },
                    label = {
                        DPTextView(
                            text = if (showLabels) themeMode.label() else "",
                            variant = DPTextViewVariant.LabelMedium,
                        )
                    }
                )
            }
        }
    }
}


private fun ThemeMode.label(): String =
    when (this) {
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
        ThemeMode.SYSTEM -> "System"
    }
