package dev.shounakmulay.devpulse.core.designsystem.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Generic size axis. Defaults to [Medium] everywhere. */
enum class DPSize { Small, Medium, Large }

/** Vertical density axis for rows, list items, text fields, menus, nav items. */
enum class DPDensity { Dense, Default, Comfortable }

/** Discrete elevation levels. Surfaced via [DPTheme.elevation]. */
enum class DPElevationLevel { Level0, Level1, Level2, Level3, Level4, Level5 }

// -----------------------------------------------------------------------------
// Shared color pair used by component variant resolvers
// -----------------------------------------------------------------------------

/** Resolved container + content color pair for a given component variant. */
data class DPVariantColors(
    val container: Color,
    val onContainer: Color,
    val accent: Color,
    val onAccent: Color,
    val outline: Color,
)

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

// -----------------------------------------------------------------------------
// Shared P/S/T variant color resolver — used by components that need colored
// variants following the DPTextViewVariant pattern
// -----------------------------------------------------------------------------

@Composable
@ReadOnlyComposable
internal fun dpPrimaryVariantColors(): DPVariantColors {
    val cs = MaterialTheme.colorScheme
    return DPVariantColors(
        container = cs.primaryContainer,
        onContainer = cs.onPrimaryContainer,
        accent = cs.primary,
        onAccent = cs.onPrimary,
        outline = cs.primary,
    )
}

@Composable
@ReadOnlyComposable
internal fun dpSecondaryVariantColors(): DPVariantColors {
    val cs = MaterialTheme.colorScheme
    return DPVariantColors(
        container = cs.secondaryContainer,
        onContainer = cs.onSecondaryContainer,
        accent = cs.secondary,
        onAccent = cs.onSecondary,
        outline = cs.secondary,
    )
}

@Composable
@ReadOnlyComposable
internal fun dpTertiaryVariantColors(): DPVariantColors {
    val cs = MaterialTheme.colorScheme
    return DPVariantColors(
        container = cs.tertiaryContainer,
        onContainer = cs.onTertiaryContainer,
        accent = cs.tertiary,
        onAccent = cs.onTertiary,
        outline = cs.tertiary,
    )
}
