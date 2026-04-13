package dev.shounakmulay.core.designsystem.foundation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// SPACING
// 4dp base grid. All values are multiples of 4 (except xxs hairline).
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class DPSpacing(
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp,
    val xxxl: Dp,
    val hero: Dp,

    // Semantic spatial tokens
    val screenHorizontal: Dp,
    val sectionGap: Dp,
    val iconTextGap: Dp,
    val touchTarget: Dp,
    val listItemHeight: Dp,
    val listItemHeightCompact: Dp,
    val bottomNavHeight: Dp,
    val topBarHeight: Dp,
)

val DefaultSpacing = DPSpacing(
    xxs = 2.dp,
    xs = 4.dp,
    sm = 8.dp,
    md = 12.dp,
    lg = 16.dp,
    xl = 20.dp,
    xxl = 24.dp,
    xxxl = 32.dp,
    hero = 48.dp,

    screenHorizontal = 20.dp,
    sectionGap = 24.dp,
    iconTextGap = 8.dp,
    touchTarget = 48.dp,
    listItemHeight = 56.dp,
    listItemHeightCompact = 48.dp,
    bottomNavHeight = 64.dp,
    topBarHeight = 56.dp,
)

val LocalDPSpacing = staticCompositionLocalOf { DefaultSpacing }

// ─────────────────────────────────────────────────────────────────────────────
// SHAPE
// Named corner radius tokens. Pass to RoundedCornerShape(DPTheme.shape.md).
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class DPShape(
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val pill: Dp,
)

val DefaultShape = DPShape(
    xs = 3.dp,
    sm = 6.dp,
    md = 10.dp,
    lg = 14.dp,
    pill = 100.dp,
)

val LocalDPShape = staticCompositionLocalOf { DefaultShape }

// ─────────────────────────────────────────────────────────────────────────────
// ELEVATION
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class DPElevation(
    val none: Dp,
    val low: Dp,
    val medium: Dp,
    val high: Dp,
    val overlay: Dp,
)

val DefaultElevation = DPElevation(
    none = 0.dp,
    low = 1.dp,
    medium = 4.dp,
    high = 8.dp,
    overlay = 16.dp,
)

val LocalDPElevation = staticCompositionLocalOf { DefaultElevation }

// ─────────────────────────────────────────────────────────────────────────────
// STROKE
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class DPStroke(
    val hairline: Dp,
    val thin: Dp,
    val medium: Dp,
)

val DefaultStroke = DPStroke(
    hairline = 0.5.dp,
    thin = 1.dp,
    medium = 2.dp,
)

val LocalDPStroke = staticCompositionLocalOf { DefaultStroke }

// ─────────────────────────────────────────────────────────────────────────────
// ICON SIZES
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class DPIconSize(
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
)

val DefaultIconSize = DPIconSize(
    xs = 14.dp,
    sm = 16.dp,
    md = 20.dp,
    lg = 24.dp,
)

val LocalDPIconSize = staticCompositionLocalOf { DefaultIconSize }

// ─────────────────────────────────────────────────────────────────────────────
// MOTION
// All animations use DPMotion constants.
// Never hardcode milliseconds or easing curves.
// ─────────────────────────────────────────────────────────────────────────────

object DPMotion {

    // Duration constants (ms)
    const val MICRO = 80
    const val FAST = 150
    const val STANDARD = 250
    const val COMFORTABLE = 350
    const val RELAXED = 450

    // Easing curves
    val standard: Easing = FastOutSlowInEasing
    val decelerate: Easing = LinearOutSlowInEasing
    val accelerate: Easing = CubicBezierEasing(0.4f, 0f, 1f, 1f)
    val emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    // Pre-built AnimationSpec factories
    fun <T> microSpec(): FiniteAnimationSpec<T> = tween(MICRO, easing = standard)
    fun <T> fastSpec(): FiniteAnimationSpec<T> = tween(FAST, easing = standard)
    fun <T> standardSpec(): FiniteAnimationSpec<T> = tween(STANDARD, easing = standard)
    fun <T> comfortableSpec(): FiniteAnimationSpec<T> = tween(COMFORTABLE, easing = emphasized)
    fun <T> relaxedSpec(): FiniteAnimationSpec<T> = tween(RELAXED, easing = emphasized)

    fun <T> enterSpec(): FiniteAnimationSpec<T> = tween(COMFORTABLE, easing = decelerate)
    fun <T> exitSpec(): FiniteAnimationSpec<T> = tween(FAST, easing = accelerate)
}
