package dev.shounakmulay.devpulse.core.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// =============================================================================
// PRIMARY — Burnt amber orange (deep Claude orange variant)
// =============================================================================

val primaryLight = Color(0xFFD96320)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFE5D0)
val onPrimaryContainerLight = Color(0xFF4A1D02)
val inversePrimaryLight = Color(0xFFFFCDA8)

val primaryDark = Color(0xFFD96320)         // intentionally same — orange holds in dark mode
val onPrimaryDark = Color(0xFFFFFFFF)
val primaryContainerDark = Color(0xFF8B3200)
val onPrimaryContainerDark = Color(0xFFFFE5D0)
val inversePrimaryDark = Color(0xFFD96320)

// =============================================================================
// SECONDARY — Warm sand terracotta
// =============================================================================

val secondaryLight = Color(0xFFA87748)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFF5E6D3)
val onSecondaryContainerLight = Color(0xFF3A2208)

val secondaryDark = Color(0xFFE8C9A0)
val onSecondaryDark = Color(0xFF3D2200)
val secondaryContainerDark = Color(0xFF6B4420)
val onSecondaryContainerDark = Color(0xFFFFF0DC)

// =============================================================================
// TERTIARY — Dusty mauve violet
// =============================================================================

val tertiaryLight = Color(0xFF96609E)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFEFD9F0)
val onTertiaryContainerLight = Color(0xFF2C1030)

val tertiaryDark = Color(0xFFD9B4DC)
val onTertiaryDark = Color(0xFF2C1040)
val tertiaryContainerDark = Color(0xFF6B3872)
val onTertiaryContainerDark = Color(0xFFF8E8FA)

// =============================================================================
// ERROR — Standard M3 red
// =============================================================================

val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFCD010C)
val onErrorContainerLight = Color(0xFFFFDAD6)

val errorDark = Color(0xFFBA1A1A)
val onErrorDark = Color(0xFFFFFFFF)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)

// =============================================================================
// BACKGROUND & SURFACE
// Light: warm cream / Dark: near-black, barely-tinted, desaturated
// =============================================================================

val backgroundLight = Color(0xFFFAF7F0)
val onBackgroundLight = Color(0xFF1C1B18)
val surfaceLight = Color(0xFFFAF7F0)
val onSurfaceLight = Color(0xFF1C1B18)

// Dark: near-black with just a trace of cool — no visible hue, no blue bleed
val backgroundDark = Color(0xFF0C0E14)
val onBackgroundDark = Color(0xFFE4E6F0)
val surfaceDark = Color(0xFF0C0E14)
val onSurfaceDark = Color(0xFFE4E6F0)

// =============================================================================
// SURFACE VARIANTS & CONTAINERS — Light
// =============================================================================

val surfaceVariantLight = Color(0xFFEDE9E0)
val onSurfaceVariantLight = Color(0xFF4E4A41)
val surfaceDimLight = Color(0xFFD8D3C8)
val surfaceBrightLight = Color(0xFFFAF7F0)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF5F1E8)
val surfaceContainerLight = Color(0xFFEDE9E0)
val surfaceContainerHighLight = Color(0xFFE6E2D8)
val surfaceContainerHighestLight = Color(0xFFDDD9D0)

val outlineLight = Color(0xFF8C877C)
val outlineVariantLight = Color(0xFFD8D3C8)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF1C1B18)
val inverseOnSurfaceLight = Color(0xFFF5F1E8)

// =============================================================================
// SURFACE VARIANTS & CONTAINERS — Dark (desaturated — containers barely visible)
// =============================================================================

val surfaceVariantDark = Color(0xFF1C1E28)
val onSurfaceVariantDark = Color(0xFFA8AABF)
val surfaceDimDark = Color(0xFF0C0E14)
val surfaceBrightDark = Color(0xFF282C3A)
val surfaceContainerLowestDark = Color(0xFF07080D)
val surfaceContainerLowDark = Color(0xFF12141C)
val surfaceContainerDark = Color(0xFF181A24)
val surfaceContainerHighDark = Color(0xFF1E2030)
val surfaceContainerHighestDark = Color(0xFF252838)

val outlineDark = Color(0xFF4E5068)
val outlineVariantDark = Color(0xFF282C3A)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE4E6F0)
val inverseOnSurfaceDark = Color(0xFF181A24)

// =============================================================================
// SEMANTIC — success / warning / info (kept for reference; not used in components)
// =============================================================================

val successLight = Color(0xFF386A20)
val onSuccessLight = Color(0xFFFFFFFF)
val successContainerLight = Color(0xFFB7F397)
val onSuccessContainerLight = Color(0xFF0C2000)

val successDark = Color(0xFF9CD67D)
val onSuccessDark = Color(0xFF0E3900)
val successContainerDark = Color(0xFF1F5005)
val onSuccessContainerDark = Color(0xFFB7F397)

val warningLight = Color(0xFF7A5900)
val onWarningLight = Color(0xFFFFFFFF)
val warningContainerLight = Color(0xFFFFDF99)
val onWarningContainerLight = Color(0xFF251A00)

val warningDark = Color(0xFFF5C030)
val onWarningDark = Color(0xFF3F2E00)
val warningContainerDark = Color(0xFF3A2800)
val onWarningContainerDark = Color(0xFFFFDF99)

val infoLight = Color(0xFF2C5FA8)
val onInfoLight = Color(0xFFFFFFFF)
val infoContainerLight = Color(0xFFD6E4FF)
val onInfoContainerLight = Color(0xFF001A41)

val infoDark = Color(0xFFACC7FF)
val onInfoDark = Color(0xFF002E6A)
val infoContainerDark = Color(0xFF0A2040)
val onInfoContainerDark = Color(0xFFACC7FF)

val successSurfaceLight = Color(0xFFE0F7EE)
val warningSurfaceLight = Color(0xFFFFF8ED)
val infoSurfaceLight = Color(0xFFE8F4FB)

data class DPContextColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color
)

val lightDPContextColors = DPContextColors(
    success = successLight,
    onSuccess = onSuccessLight,
    successContainer = successContainerLight,
    onSuccessContainer = onSuccessContainerLight,
    warning = warningLight,
    onWarning = onWarningLight,
    warningContainer = warningContainerLight,
    onWarningContainer = onWarningContainerLight,
    info = infoLight,
    onInfo = onInfoLight,
    infoContainer = infoContainerLight,
    onInfoContainer = onInfoContainerLight
)

val darkDPContextColors = DPContextColors(
    success = successDark,
    onSuccess = onSuccessDark,
    successContainer = successContainerDark,
    onSuccessContainer = onSuccessContainerDark,
    warning = warningDark,
    onWarning = onWarningDark,
    warningContainer = warningContainerDark,
    onWarningContainer = onWarningContainerDark,
    info = infoDark,
    onInfo = onInfoDark,
    infoContainer = infoContainerDark,
    onInfoContainer = onInfoContainerDark
)

val LocalDPContextColors = compositionLocalOf { lightDPContextColors }

// =============================================================================
// CATEGORY COLOURS
// =============================================================================

val categoryAndroid = Color(0xFF3DDC84)
val categoryKotlin = Color(0xFF7F52FF)
val categoryIOS = Color(0xFF1C7ED6)
val categorySwift = Color(0xFFFF6B35)
val categoryFlutter = Color(0xFF54C5F8)
val categoryReactNative = Color(0xFF61DAFB)
val categoryWeb = Color(0xFFF7B731)
val categoryDevOps = Color(0xFFFA5252)
val categoryAI = Color(0xFFCC5DE8)
val categoryGeneral = Color(0xFF8E8C89)

fun categoryColour(category: String): Color = when (category.lowercase()) {
    "android" -> categoryAndroid
    "kotlin" -> categoryKotlin
    "ios" -> categoryIOS
    "swift" -> categorySwift
    "flutter" -> categoryFlutter
    "react native", "reactnative", "react-native" -> categoryReactNative
    "web" -> categoryWeb
    "devops" -> categoryDevOps
    "ai" -> categoryAI
    else -> categoryGeneral
}
