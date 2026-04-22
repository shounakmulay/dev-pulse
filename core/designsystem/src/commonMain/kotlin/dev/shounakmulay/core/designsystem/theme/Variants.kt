package dev.shounakmulay.core.designsystem.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Core variant axes shared across every DP* component.
 *
 * Every component consumes these enums (plus its own component-local "style" enum
 * where the set of valid styles is component-specific, e.g. DPButtonStyle) and
 * resolves every Material runtime parameter (colors, shape, elevation, padding,
 * text style) from them via the @Composable helpers in this file.
 */

/** Generic size axis. Defaults to [Medium] everywhere. */
enum class DPSize { Small, Medium, Large }

/**
 * Semantic color intent. Drives container / accent / outline color selection across
 * buttons, chips, badges, snackbars, progress, fields, dialogs, etc.
 *
 * The "accent" color is the solid brand-weight color; the "container" color is the
 * tonal, softer variant. Components pick which slot to render based on their own
 * style axis (filled vs tonal vs outlined).
 */
enum class DPIntent {
    Primary,
    Secondary,
    Tertiary,
    Success,
    Warning,
    Danger,
    Info,
    Neutral,
}

/**
 * Visual weight axis for containment-based components (buttons, chips, cards, FABs).
 * Individual components may not support every value - they expose their own style
 * enum when they need to.
 */
enum class DPEmphasis { Filled, Tonal, Outlined, Text, Elevated }

/** Vertical density axis for rows, list items, text fields, menus, nav items. */
enum class DPDensity { Dense, Default, Comfortable }

/** Discrete elevation levels. Surfaced via [DPTheme.elevation]. */
enum class DPElevationLevel { Level0, Level1, Level2, Level3, Level4, Level5 }

// -----------------------------------------------------------------------------
// Intent -> color resolution
// -----------------------------------------------------------------------------

@Immutable
data class DPIntentColors(
    /** Solid brand-weight color. Use as background for Filled style. */
    val accent: Color,
    /** Contrast color on top of [accent]. */
    val onAccent: Color,
    /** Tonal container color. Use as background for Tonal style. */
    val container: Color,
    /** Contrast color on top of [container]. */
    val onContainer: Color,
    /** Lighter / dimmer tonal surface (e.g. snackbar background, hover state). */
    val containerVariant: Color,
    /** Outline / border color for this intent. Use for Outlined style. */
    val outline: Color,
)

@Composable
@ReadOnlyComposable
fun DPIntent.colors(): DPIntentColors {
    val cs = MaterialTheme.colorScheme
    return when (this) {
        DPIntent.Primary -> DPIntentColors(
            accent = cs.primary,
            onAccent = cs.onPrimary,
            container = cs.primaryContainer,
            onContainer = cs.onPrimaryContainer,
            containerVariant = cs.primaryContainer,
            outline = cs.primary,
        )
        DPIntent.Secondary -> DPIntentColors(
            accent = cs.secondary,
            onAccent = cs.onSecondary,
            container = cs.secondaryContainer,
            onContainer = cs.onSecondaryContainer,
            containerVariant = cs.secondaryContainer,
            outline = cs.secondary,
        )
        DPIntent.Tertiary -> DPIntentColors(
            accent = cs.tertiary,
            onAccent = cs.onTertiary,
            container = cs.tertiaryContainer,
            onContainer = cs.onTertiaryContainer,
            containerVariant = cs.tertiaryContainer,
            outline = cs.tertiary,
        )
        DPIntent.Success -> DPIntentColors(
            accent = cs.success,
            onAccent = cs.onSuccess,
            container = cs.successContainer,
            onContainer = cs.onSuccessContainer,
            containerVariant = cs.successContainer,
            outline = cs.success,
        )
        DPIntent.Warning -> DPIntentColors(
            accent = cs.warning,
            onAccent = cs.onWarning,
            container = cs.warningContainer,
            onContainer = cs.onWarningContainer,
            containerVariant = cs.warningContainer,
            outline = cs.warning,
        )
        DPIntent.Danger -> DPIntentColors(
            accent = cs.error,
            onAccent = cs.onError,
            container = cs.errorContainer,
            onContainer = cs.onErrorContainer,
            containerVariant = cs.errorContainer,
            outline = cs.error,
        )
        DPIntent.Info -> DPIntentColors(
            accent = cs.info,
            onAccent = cs.onInfo,
            container = cs.infoContainer,
            onContainer = cs.onInfoContainer,
            containerVariant = cs.infoContainer,
            outline = cs.info,
        )
        DPIntent.Neutral -> DPIntentColors(
            accent = cs.surfaceContainerHighest,
            onAccent = cs.onSurface,
            container = cs.surfaceContainerHigh,
            onContainer = cs.onSurface,
            containerVariant = cs.surfaceContainer,
            outline = cs.outline,
        )
    }
}

// -----------------------------------------------------------------------------
// Size -> dimension / text style resolution
// -----------------------------------------------------------------------------

/** Minimum height for interactive pill-shaped controls (buttons, chips, fields). */
@Composable
@ReadOnlyComposable
fun DPSize.minHeight(): Dp = when (this) {
    DPSize.Small -> 32.dp
    DPSize.Medium -> 40.dp
    DPSize.Large -> 56.dp
}

/** Standard content padding for buttons / chips at this size. */
@Composable
@ReadOnlyComposable
fun DPSize.contentPadding(): PaddingValues = when (this) {
    DPSize.Small -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    DPSize.Medium -> PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    DPSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 16.dp)
}

/** Icon size for leading / trailing icons at this size. */
@Composable
@ReadOnlyComposable
fun DPSize.iconSize(): Dp {
    val tokens = DPTheme.iconSize
    return when (this) {
        DPSize.Small -> tokens.sm
        DPSize.Medium -> tokens.md
        DPSize.Large -> tokens.lg
    }
}

/** Label typography for buttons / chips at this size. */
@Composable
@ReadOnlyComposable
fun DPSize.labelStyle(): TextStyle = when (this) {
    DPSize.Small -> MaterialTheme.typography.labelSmall
    DPSize.Medium -> MaterialTheme.typography.labelLarge
    DPSize.Large -> MaterialTheme.typography.titleMedium
}

/** Gap between leading/trailing icon and label. */
@Composable
@ReadOnlyComposable
fun DPSize.iconLabelGap(): Dp {
    val spacing = DPTheme.spacing
    return when (this) {
        DPSize.Small -> spacing.xs
        DPSize.Medium -> spacing.sm
        DPSize.Large -> spacing.md
    }
}

// -----------------------------------------------------------------------------
// Density -> row padding resolution
// -----------------------------------------------------------------------------

/** Vertical padding applied to list rows and nav items at this density. */
@Composable
@ReadOnlyComposable
fun DPDensity.verticalPadding(): Dp {
    val spacing = DPTheme.spacing
    return when (this) {
        DPDensity.Dense -> spacing.xs
        DPDensity.Default -> spacing.sm
        DPDensity.Comfortable -> spacing.md
    }
}

/** Horizontal screen-edge padding at this density. */
@Composable
@ReadOnlyComposable
fun DPDensity.horizontalPadding(): Dp {
    val spacing = DPTheme.spacing
    return when (this) {
        DPDensity.Dense -> spacing.md
        DPDensity.Default -> spacing.lg
        DPDensity.Comfortable -> spacing.xl
    }
}

// -----------------------------------------------------------------------------
// Elevation
// -----------------------------------------------------------------------------

/** Resolves an elevation level to its Dp via [DPTheme.elevation]. */
@Composable
@ReadOnlyComposable
fun DPElevationLevel.value(): Dp {
    val tokens = DPTheme.elevation
    return when (this) {
        DPElevationLevel.Level0 -> tokens.level0
        DPElevationLevel.Level1 -> tokens.level1
        DPElevationLevel.Level2 -> tokens.level2
        DPElevationLevel.Level3 -> tokens.level3
        DPElevationLevel.Level4 -> tokens.level4
        DPElevationLevel.Level5 -> tokens.level5
    }
}
