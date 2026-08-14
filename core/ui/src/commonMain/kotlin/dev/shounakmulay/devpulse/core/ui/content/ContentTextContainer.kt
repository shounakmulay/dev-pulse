package dev.shounakmulay.devpulse.core.ui.content

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ContentTextContainer(content: @Composable () -> Unit) {
    val contentTextSettings by LocalContentTextSettings.current.collectAsStateWithLifecycle()
    val newTypography = rememberTransformedTypography(contentTextSettings)
    val density = LocalDensity.current
    val scaledText = remember(density, contentTextSettings) {
        Density(
            density = density.density,
            fontScale = density.fontScale * contentTextSettings.textScale
        )
    }
    MaterialTheme(typography = newTypography) {
        CompositionLocalProvider(
            LocalDensity provides scaledText,
        ) {
            content()
        }
    }
}

@Composable
private fun rememberTransformedTypography(
    contentTextSettings: UIContentTextSettings
): Typography {
    val typography = MaterialTheme.typography
    return remember(typography, contentTextSettings) {
        typography.copy(
            displayLarge = typography.displayLarge.copy(lineHeight = typography.displayLarge.lineHeight * contentTextSettings.lineHeightScale),
            displayMedium = typography.displayMedium.copy(lineHeight = typography.displayMedium.lineHeight * contentTextSettings.lineHeightScale),
            displaySmall = typography.displaySmall.copy(lineHeight = typography.displaySmall.lineHeight * contentTextSettings.lineHeightScale),
            headlineLarge = typography.headlineLarge.copy(lineHeight = typography.headlineLarge.lineHeight * contentTextSettings.lineHeightScale),
            headlineMedium = typography.headlineMedium.copy(lineHeight = typography.headlineMedium.lineHeight * contentTextSettings.lineHeightScale),
            headlineSmall = typography.headlineSmall.copy(lineHeight = typography.headlineSmall.lineHeight * contentTextSettings.lineHeightScale),
            titleLarge = typography.titleLarge.copy(lineHeight = typography.titleLarge.lineHeight * contentTextSettings.lineHeightScale),
            titleMedium = typography.titleMedium.copy(lineHeight = typography.titleMedium.lineHeight * contentTextSettings.lineHeightScale),
            titleSmall = typography.titleSmall.copy(lineHeight = typography.titleSmall.lineHeight * contentTextSettings.lineHeightScale),
            bodyLarge = typography.bodyLarge.copy(lineHeight = typography.bodyLarge.lineHeight * contentTextSettings.lineHeightScale),
            bodyMedium = typography.bodyMedium.copy(lineHeight = typography.bodyMedium.lineHeight * contentTextSettings.lineHeightScale),
            bodySmall = typography.bodySmall.copy(lineHeight = typography.bodySmall.lineHeight * contentTextSettings.lineHeightScale),
            labelLarge = typography.labelLarge.copy(lineHeight = typography.labelLarge.lineHeight * contentTextSettings.lineHeightScale),
            labelMedium = typography.labelMedium.copy(lineHeight = typography.labelMedium.lineHeight * contentTextSettings.lineHeightScale),
            labelSmall = typography.labelSmall.copy(lineHeight = typography.labelSmall.lineHeight * contentTextSettings.lineHeightScale),
            displayLargeEmphasized = typography.displayLargeEmphasized.copy(lineHeight = typography.displayLargeEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            displayMediumEmphasized = typography.displayMediumEmphasized.copy(lineHeight = typography.displayMediumEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            displaySmallEmphasized = typography.displaySmallEmphasized.copy(lineHeight = typography.displaySmallEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            headlineLargeEmphasized = typography.headlineLargeEmphasized.copy(lineHeight = typography.headlineLargeEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            headlineMediumEmphasized = typography.headlineMediumEmphasized.copy(lineHeight = typography.headlineMediumEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            headlineSmallEmphasized = typography.headlineSmallEmphasized.copy(lineHeight = typography.headlineSmallEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            titleLargeEmphasized = typography.titleLargeEmphasized.copy(lineHeight = typography.titleLargeEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            titleMediumEmphasized = typography.titleMediumEmphasized.copy(lineHeight = typography.titleMediumEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            titleSmallEmphasized = typography.titleSmallEmphasized.copy(lineHeight = typography.titleSmallEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            bodyLargeEmphasized = typography.bodyLargeEmphasized.copy(lineHeight = typography.bodyLargeEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            bodyMediumEmphasized = typography.bodyMediumEmphasized.copy(lineHeight = typography.bodyMediumEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            bodySmallEmphasized = typography.bodySmallEmphasized.copy(lineHeight = typography.bodySmallEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            labelLargeEmphasized = typography.labelLargeEmphasized.copy(lineHeight = typography.labelLargeEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            labelMediumEmphasized = typography.labelMediumEmphasized.copy(lineHeight = typography.labelMediumEmphasized.lineHeight * contentTextSettings.lineHeightScale),
            labelSmallEmphasized = typography.labelSmallEmphasized.copy(lineHeight = typography.labelSmallEmphasized.lineHeight * contentTextSettings.lineHeightScale)
        )
    }
}
