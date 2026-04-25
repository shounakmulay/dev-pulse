package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryAndroid
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryAI
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryDevOps
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryFlutter
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryGeneral
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryIOS
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryKotlin
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryReactNative
import dev.shounakmulay.devpulse.core.designsystem.theme.categorySwift
import dev.shounakmulay.devpulse.core.designsystem.theme.categoryWeb
import dev.shounakmulay.devpulse.core.designsystem.theme.info
import dev.shounakmulay.devpulse.core.designsystem.theme.infoContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.onInfo
import dev.shounakmulay.devpulse.core.designsystem.theme.onInfoContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.onSuccess
import dev.shounakmulay.devpulse.core.designsystem.theme.onSuccessContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.onWarning
import dev.shounakmulay.devpulse.core.designsystem.theme.onWarningContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.success
import dev.shounakmulay.devpulse.core.designsystem.theme.successContainer
import dev.shounakmulay.devpulse.core.designsystem.theme.warning
import dev.shounakmulay.devpulse.core.designsystem.theme.warningContainer

private data class ColorEntry(val name: String, val color: Color)

@Composable
fun ColorBoard(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val m3Colors = m3ColorEntries(cs)
    val semanticColors = semanticColorEntries(cs)
    val categoryColors = categoryColorEntries()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Material 3 Color Roles",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        items(m3Colors) { entry -> ColorSwatch(entry) }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Semantic Colors",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        items(semanticColors) { entry -> ColorSwatch(entry) }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Category Colors",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        items(categoryColors) { entry -> ColorSwatch(entry) }
    }
}

@Composable
private fun ColorSwatch(entry: ColorEntry) {
    val shape = RoundedCornerShape(8.dp)
    val textColor = if (entry.color.luminance() > 0.5f) Color.Black else Color.White
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .background(entry.color, shape)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(6.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Column {
            Text(
                text = entry.name,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                maxLines = 2,
            )
        }
    }
}

private fun m3ColorEntries(cs: ColorScheme): List<ColorEntry> = listOf(
    ColorEntry("primary", cs.primary),
    ColorEntry("onPrimary", cs.onPrimary),
    ColorEntry("primaryContainer", cs.primaryContainer),
    ColorEntry("onPrimaryContainer", cs.onPrimaryContainer),
    ColorEntry("secondary", cs.secondary),
    ColorEntry("onSecondary", cs.onSecondary),
    ColorEntry("secondaryContainer", cs.secondaryContainer),
    ColorEntry("onSecondaryContainer", cs.onSecondaryContainer),
    ColorEntry("tertiary", cs.tertiary),
    ColorEntry("onTertiary", cs.onTertiary),
    ColorEntry("tertiaryContainer", cs.tertiaryContainer),
    ColorEntry("onTertiaryContainer", cs.onTertiaryContainer),
    ColorEntry("error", cs.error),
    ColorEntry("onError", cs.onError),
    ColorEntry("errorContainer", cs.errorContainer),
    ColorEntry("onErrorContainer", cs.onErrorContainer),
    ColorEntry("background", cs.background),
    ColorEntry("onBackground", cs.onBackground),
    ColorEntry("surface", cs.surface),
    ColorEntry("onSurface", cs.onSurface),
    ColorEntry("surfaceVariant", cs.surfaceVariant),
    ColorEntry("onSurfaceVariant", cs.onSurfaceVariant),
    ColorEntry("outline", cs.outline),
    ColorEntry("outlineVariant", cs.outlineVariant),
    ColorEntry("inverseSurface", cs.inverseSurface),
    ColorEntry("inverseOnSurface", cs.inverseOnSurface),
    ColorEntry("inversePrimary", cs.inversePrimary),
    ColorEntry("surfaceTint", cs.surfaceTint),
    ColorEntry("surfaceContainerLowest", cs.surfaceContainerLowest),
    ColorEntry("surfaceContainerLow", cs.surfaceContainerLow),
    ColorEntry("surfaceContainer", cs.surfaceContainer),
    ColorEntry("surfaceContainerHigh", cs.surfaceContainerHigh),
    ColorEntry("surfaceContainerHighest", cs.surfaceContainerHighest),
    ColorEntry("scrim", cs.scrim),
)

private fun semanticColorEntries(cs: ColorScheme): List<ColorEntry> = listOf(
    ColorEntry("success", cs.success),
    ColorEntry("onSuccess", cs.onSuccess),
    ColorEntry("successContainer", cs.successContainer),
    ColorEntry("onSuccessContainer", cs.onSuccessContainer),
    ColorEntry("warning", cs.warning),
    ColorEntry("onWarning", cs.onWarning),
    ColorEntry("warningContainer", cs.warningContainer),
    ColorEntry("onWarningContainer", cs.onWarningContainer),
    ColorEntry("info", cs.info),
    ColorEntry("onInfo", cs.onInfo),
    ColorEntry("infoContainer", cs.infoContainer),
    ColorEntry("onInfoContainer", cs.onInfoContainer),
)

private fun categoryColorEntries(): List<ColorEntry> = listOf(
    ColorEntry("Android", categoryAndroid),
    ColorEntry("Kotlin", categoryKotlin),
    ColorEntry("iOS", categoryIOS),
    ColorEntry("Swift", categorySwift),
    ColorEntry("Flutter", categoryFlutter),
    ColorEntry("React Native", categoryReactNative),
    ColorEntry("Web", categoryWeb),
    ColorEntry("DevOps", categoryDevOps),
    ColorEntry("AI", categoryAI),
    ColorEntry("General", categoryGeneral),
)
