package dev.shounakmulay.devpulse.core.designsystem.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    val paragraphGap: Dp,
    val screenHorizontal: Dp,
    val sectionGap: Dp,
    val touchTarget: Dp,
    val listItemHeight: Dp,
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
    paragraphGap = 12.dp,
    screenHorizontal = 20.dp,
    sectionGap = 32.dp,
    touchTarget = 48.dp,
    listItemHeight = 56.dp,
)

val LocalDPSpacing = staticCompositionLocalOf { DefaultSpacing }

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

@Immutable
data class DPElevation(
    val level0: Dp,
    val level1: Dp,
    val level2: Dp,
    val level3: Dp,
    val level4: Dp,
    val level5: Dp,
)

val DefaultElevation = DPElevation(
    level0 = 0.dp,
    level1 = 1.dp,
    level2 = 3.dp,
    level3 = 6.dp,
    level4 = 8.dp,
    level5 = 12.dp,
)

val LocalDPElevation = staticCompositionLocalOf { DefaultElevation }

val dpShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun calmMotionScheme(): MotionScheme = CalmMotionSchemeImpl

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Suppress("UNCHECKED_CAST")
private object CalmMotionSchemeImpl : MotionScheme {

    private val defaultSpatial = spring<Any>(
        dampingRatio = 0.95f,
        stiffness = 220f,
    )
    private val fastSpatial = spring<Any>(
        dampingRatio = 0.9f,
        stiffness = 400f,
    )
    private val slowSpatial = spring<Any>(
        dampingRatio = 1.0f,
        stiffness = 120f,
    )
    private val defaultEffects = spring<Any>(
        dampingRatio = 1.0f,
        stiffness = 600f,
    )
    private val fastEffects = spring<Any>(
        dampingRatio = 1.0f,
        stiffness = 1000f,
    )
    private val slowEffects = spring<Any>(
        dampingRatio = 1.0f,
        stiffness = 300f,
    )

    override fun <T> defaultSpatialSpec(): FiniteAnimationSpec<T> =
        defaultSpatial as FiniteAnimationSpec<T>

    override fun <T> fastSpatialSpec(): FiniteAnimationSpec<T> =
        fastSpatial as FiniteAnimationSpec<T>

    override fun <T> slowSpatialSpec(): FiniteAnimationSpec<T> =
        slowSpatial as FiniteAnimationSpec<T>

    override fun <T> defaultEffectsSpec(): FiniteAnimationSpec<T> =
        defaultEffects as FiniteAnimationSpec<T>

    override fun <T> fastEffectsSpec(): FiniteAnimationSpec<T> =
        fastEffects as FiniteAnimationSpec<T>

    override fun <T> slowEffectsSpec(): FiniteAnimationSpec<T> =
        slowEffects as FiniteAnimationSpec<T>
}
