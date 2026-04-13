package dev.shounakmulay.devpulse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import dev.shounakmulay.devpulse.ui.foundation.DPColours
import dev.shounakmulay.devpulse.ui.foundation.DPElevation
import dev.shounakmulay.devpulse.ui.foundation.DPIconSize
import dev.shounakmulay.devpulse.ui.foundation.DPPalette
import dev.shounakmulay.devpulse.ui.foundation.DPShape
import dev.shounakmulay.devpulse.ui.foundation.DPSpacing
import dev.shounakmulay.devpulse.ui.foundation.DPStroke
import dev.shounakmulay.devpulse.ui.foundation.DPTypography
import dev.shounakmulay.devpulse.ui.foundation.DarkColours
import dev.shounakmulay.devpulse.ui.foundation.DefaultElevation
import dev.shounakmulay.devpulse.ui.foundation.DefaultIconSize
import dev.shounakmulay.devpulse.ui.foundation.DefaultMaterial3Typography
import dev.shounakmulay.devpulse.ui.foundation.DefaultShape
import dev.shounakmulay.devpulse.ui.foundation.DefaultSpacing
import dev.shounakmulay.devpulse.ui.foundation.DefaultStroke
import dev.shounakmulay.devpulse.ui.foundation.DefaultTypography
import dev.shounakmulay.devpulse.ui.foundation.LightColours
import dev.shounakmulay.devpulse.ui.foundation.LocalDPColours
import dev.shounakmulay.devpulse.ui.foundation.LocalDPElevation
import dev.shounakmulay.devpulse.ui.foundation.LocalDPIconSize
import dev.shounakmulay.devpulse.ui.foundation.LocalDPShape
import dev.shounakmulay.devpulse.ui.foundation.LocalDPSpacing
import dev.shounakmulay.devpulse.ui.foundation.LocalDPStroke
import dev.shounakmulay.devpulse.ui.foundation.LocalDPTypography

// ─────────────────────────────────────────────────────────────────────────────
// MATERIAL 3 COLOUR SCHEMES
// Wired from DPPalette to keep Material3 components coherent with the
// design system. Used inside DPTheme for MaterialTheme wrapping.
// ─────────────────────────────────────────────────────────────────────────────

private val DPDarkColorScheme = darkColorScheme(
    primary         = DPPalette.Primary200,
    onPrimary       = DPPalette.Primary700,
    primaryContainer = DPPalette.Primary500,
    onPrimaryContainer = DPPalette.Primary300,
    secondary       = DPPalette.Teal300,
    onSecondary     = DPPalette.Teal700,
    secondaryContainer = DPPalette.Teal500,
    onSecondaryContainer = DPPalette.Teal200,
    tertiary        = DPPalette.Indigo300,
    onTertiary      = DPPalette.Indigo700,
    tertiaryContainer = DPPalette.Indigo500,
    onTertiaryContainer = DPPalette.Indigo100,
    error           = DPPalette.Error300,
    onError         = DPPalette.Error700,
    errorContainer  = DPPalette.Error500,
    onErrorContainer = DPPalette.Error200,
    background      = DPPalette.Surface900,
    onBackground    = DPPalette.Surface100,
    surface         = DPPalette.Surface850,
    onSurface       = DPPalette.Surface100,
    surfaceVariant  = DPPalette.Surface400,
    onSurfaceVariant = DPPalette.Surface200,
    outline         = DPPalette.Surface300,
)

private val DPLightColorScheme = lightColorScheme(
    primary         = DPPalette.Primary600,
    onPrimary       = DPPalette.White,
    primaryContainer = DPPalette.Primary500,
    onPrimaryContainer = DPPalette.Primary700,
    secondary       = DPPalette.Teal600,
    onSecondary     = DPPalette.White,
    secondaryContainer = DPPalette.Teal200,
    onSecondaryContainer = DPPalette.Teal700,
    tertiary        = DPPalette.Indigo600,
    onTertiary      = DPPalette.White,
    tertiaryContainer = DPPalette.Indigo100,
    onTertiaryContainer = DPPalette.Indigo700,
    error           = DPPalette.Error600,
    onError         = DPPalette.White,
    errorContainer  = DPPalette.Error300,
    onErrorContainer = DPPalette.Error700,
    background      = DPPalette.Surface50,
    onBackground    = DPPalette.Surface950,
    surface         = DPPalette.White,
    onSurface       = DPPalette.Surface950,
    surfaceVariant  = DPPalette.Surface200,
    onSurfaceVariant = DPPalette.Surface400,
    outline         = DPPalette.Surface300,
)

// ─────────────────────────────────────────────────────────────────────────────
// DEVPULSE THEME
//
// Wraps MaterialTheme and provides all DPTheme design system tokens.
// Use at your app root:
//
//   DPTheme {
//       DevPulseNavigation()
//   }
//
// Token access from any composable inside DPTheme:
//   DPTheme.colours.signalSuccessText
//   DPTheme.typography.titleLarge
//   DPTheme.spacing.lg
//   DPTheme.shape.sm
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colours: DPColours = if (darkTheme) DarkColours else LightColours,
    typography: DPTypography = DefaultTypography,
    spacing: DPSpacing = DefaultSpacing,
    shape: DPShape = DefaultShape,
    elevation: DPElevation = DefaultElevation,
    stroke: DPStroke = DefaultStroke,
    iconSize: DPIconSize = DefaultIconSize,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DPDarkColorScheme else DPLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = DefaultMaterial3Typography,
    ) {
        CompositionLocalProvider(
            LocalDPColours    provides colours,
            LocalDPTypography provides typography,
            LocalDPSpacing    provides spacing,
            LocalDPShape      provides shape,
            LocalDPElevation  provides elevation,
            LocalDPStroke     provides stroke,
            LocalDPIconSize   provides iconSize,
            content = content,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// THEME ACCESSOR
// Single object. Access all design system tokens anywhere in the composition.
// ─────────────────────────────────────────────────────────────────────────────

object DPTheme {
    val colours: DPColours
        @Composable @ReadOnlyComposable get() = LocalDPColours.current

    val typography: DPTypography
        @Composable @ReadOnlyComposable get() = LocalDPTypography.current

    val spacing: DPSpacing
        @Composable @ReadOnlyComposable get() = LocalDPSpacing.current

    val shape: DPShape
        @Composable @ReadOnlyComposable get() = LocalDPShape.current

    val elevation: DPElevation
        @Composable @ReadOnlyComposable get() = LocalDPElevation.current

    val stroke: DPStroke
        @Composable @ReadOnlyComposable get() = LocalDPStroke.current

    val iconSize: DPIconSize
        @Composable @ReadOnlyComposable get() = LocalDPIconSize.current
}
