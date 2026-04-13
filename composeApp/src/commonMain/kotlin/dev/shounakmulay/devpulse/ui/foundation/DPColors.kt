package dev.shounakmulay.devpulse.ui.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// DEVPULSE COLOUR PALETTE
// Sourced from Material Theme Builder export (forest green / teal / indigo).
// Raw values — never use these directly in UI code.
// Always use DPTheme.colours.* (semantic roles).
// ─────────────────────────────────────────────────────────────────────────────

internal object DPPalette {

    // ── Neutrals — Material Theme Builder dark surface hierarchy ─────────────
    val Surface950   = Color(0xFF0E0E0E)   // surfaceContainerLowestDark
    val Surface900   = Color(0xFF131412)   // backgroundDark
    val Surface850   = Color(0xFF141313)   // surfaceDark
    val Surface800   = Color(0xFF1C1B1B)   // surfaceContainerLowDark
    val Surface750   = Color(0xFF201F1F)   // surfaceContainerDark
    val Surface700   = Color(0xFF2A2A29)   // surfaceContainerHighDark
    val Surface600   = Color(0xFF353434)   // surfaceContainerHighestDark
    val Surface500   = Color(0xFF3A3939)   // surfaceBrightDark
    val Surface400   = Color(0xFF444844)   // surfaceVariantDark
    val Surface300   = Color(0xFF8E928D)   // outlineDark
    val Surface200   = Color(0xFFC5C7C2)   // onSurfaceVariantDark
    val Surface100   = Color(0xFFE5E2E1)   // onSurfaceDark / inverseSurfaceDark
    val Surface50    = Color(0xFFFBF9F6)   // backgroundLight
    val White        = Color(0xFFFCF8F7)   // surfaceLight

    // ── Primary — Sage Green ──────────────────────────────────────────────────
    val Primary700   = Color(0xFF263424)   // onPrimaryDark
    val Primary600   = Color(0xFF293727)   // primaryLight
    val Primary500   = Color(0xFF3F4E3D)   // primaryContainerLight / Dark
    val Primary300   = Color(0xFFAEBFA9)   // onPrimaryContainerDark
    val Primary200   = Color(0xFFBACBB5)   // primaryDark ← main dark accent

    // ── Secondary — Teal ─────────────────────────────────────────────────────
    val Teal700      = Color(0xFF00363A)   // onSecondaryDark
    val Teal600      = Color(0xFF114549)   // secondaryLight
    val Teal500      = Color(0xFF2D5D61)   // secondaryContainerDark
    val Teal300      = Color(0xFF9FCFD3)   // secondaryDark
    val Teal200      = Color(0xFFA3D4D8)   // onSecondaryContainerDark

    // ── Tertiary — Indigo / Lavender ──────────────────────────────────────────
    val Indigo700    = Color(0xFF2D128F)   // onTertiaryDark
    val Indigo600    = Color(0xFF5140B3)   // tertiaryLight
    val Indigo500    = Color(0xFF6A5ACD)   // tertiaryContainerDark
    val Indigo300    = Color(0xFFC8BFFF)   // tertiaryDark
    val Indigo100    = Color(0xFFF0EBFF)   // onTertiaryContainerDark

    // ── Error ─────────────────────────────────────────────────────────────────
    val Error700     = Color(0xFF611116)   // onErrorDark
    val Error600     = Color(0xFF7C2628)   // errorLight
    val Error500     = Color(0xFF9B3D3D)   // errorContainerDark
    val Error300     = Color(0xFFFFB3B0)   // errorDark
    val Error200     = Color(0xFFFFC7C4)   // onErrorContainerDark

    // ── Extended: Success ─────────────────────────────────────────────────────
    val Success700   = Color(0xFF18371B)   // onSuccessDark
    val Success600   = Color(0xFF436443)   // successLight
    val Success500   = Color(0xFF779975)   // successContainerDark
    val Success300   = Color(0xFFABD0A8)   // successDark
    val Success100   = Color(0xFFE0F7EE)   // approx successLight container light

    // ── Extended: Warning ─────────────────────────────────────────────────────
    val Warning700   = Color(0xFF452B00)   // onWarningDark
    val Warning600   = Color(0xFF7D571A)   // warningLight
    val Warning500   = Color(0xFFC29451)   // warningContainerDark
    val Warning300   = Color(0xFFF0BE76)   // warningDark
    val Warning100   = Color(0xFFFFF8ED)   // approx warning light bg

    // ── Extended: Info ────────────────────────────────────────────────────────
    val Info700      = Color(0xFF073545)   // onInfoDark
    val Info600      = Color(0xFF2C5364)   // infoLight
    val Info500      = Color(0xFF456B7D)   // infoContainerDark
    val Info300      = Color(0xFFA5CCE1)   // infoDark
    val Info100      = Color(0xFFE8F4FB)   // approx info light bg

    // ── Source category colours — fixed, theme-independent ────────────────────
    val CategoryAndroid  = Color(0xFF3DDC84)
    val CategoryKotlin   = Color(0xFF7F52FF)
    val CategoryIOS      = Color(0xFF1C7ED6)
    val CategorySwift    = Color(0xFFFF6B35)
    val CategoryFlutter  = Color(0xFF54C5F8)
    val CategoryRN       = Color(0xFF61DAFB)
    val CategoryWeb      = Color(0xFFF7B731)
    val CategoryDevOps   = Color(0xFFFA5252)
    val CategoryAI       = Color(0xFFCC5DE8)
    val CategoryGeneral  = Color(0xFF8E8C89)
}

// ─────────────────────────────────────────────────────────────────────────────
// SEMANTIC COLOUR ROLES
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class DPColours(
    // Backgrounds
    val backgroundBase: Color,
    val backgroundElevated: Color,
    val backgroundOverlay: Color,
    val backgroundSubtle: Color,
    val backgroundInteractive: Color,

    // Borders
    val borderDefault: Color,
    val borderStrong: Color,
    val borderSubtle: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val textOnAccent: Color,

    // Primary accent — Sage Green
    val accentPrimary: Color,
    val accentPrimaryText: Color,
    val accentPrimarySubtle: Color,
    val accentPrimaryBorder: Color,

    // Signal: Success
    val signalSuccessText: Color,
    val signalSuccessIcon: Color,
    val signalSuccessSurface: Color,
    val signalSuccessBorder: Color,

    // Signal: Warning
    val signalWarningText: Color,
    val signalWarningIcon: Color,
    val signalWarningSurface: Color,
    val signalWarningBorder: Color,

    // Signal: Error
    val signalErrorText: Color,
    val signalErrorIcon: Color,
    val signalErrorSurface: Color,
    val signalErrorBorder: Color,

    // Signal: Info
    val signalInfoText: Color,
    val signalInfoIcon: Color,
    val signalInfoSurface: Color,
    val signalInfoBorder: Color,

    // Signal: Waiting / Pending (indigo)
    val signalWaitingText: Color,
    val signalWaitingIcon: Color,
    val signalWaitingSurface: Color,
    val signalWaitingBorder: Color,

    // Interactive
    val interactivePrimaryFill: Color,
    val interactivePrimaryText: Color,
    val interactivePrimaryPressed: Color,
    val interactiveSecondaryFill: Color,
    val interactiveSecondaryBorder: Color,
    val interactiveDestructiveFill: Color,
    val interactiveDestructiveText: Color,

    // Source categories — always fixed
    val categoryAndroid: Color,
    val categoryKotlin: Color,
    val categoryIOS: Color,
    val categorySwift: Color,
    val categoryFlutter: Color,
    val categoryRN: Color,
    val categoryWeb: Color,
    val categoryDevOps: Color,
    val categoryAI: Color,
    val categoryGeneral: Color,

    val isDark: Boolean,
)

// ─────────────────────────────────────────────────────────────────────────────
// DARK COLOUR SET
// ─────────────────────────────────────────────────────────────────────────────

val DarkColours = DPColours(
    backgroundBase          = DPPalette.Surface900,
    backgroundElevated      = DPPalette.Surface850,
    backgroundOverlay       = DPPalette.Surface800,
    backgroundSubtle        = DPPalette.Surface750,
    backgroundInteractive   = DPPalette.Surface700,

    borderDefault           = Color(0x14FFFFFF),
    borderStrong            = Color(0x26FFFFFF),
    borderSubtle            = Color(0x0AFFFFFF),

    textPrimary             = DPPalette.Surface100,
    textSecondary           = DPPalette.Surface200,
    textTertiary            = DPPalette.Surface300,
    textDisabled            = DPPalette.Surface400,
    textOnAccent            = DPPalette.Primary700,

    accentPrimary           = DPPalette.Primary200,
    accentPrimaryText       = DPPalette.Primary700,
    accentPrimarySubtle     = Color(0x1ABACBB5),
    accentPrimaryBorder     = Color(0x33BACBB5),

    signalSuccessText       = DPPalette.Success300,
    signalSuccessIcon       = DPPalette.Success300,
    signalSuccessSurface    = DPPalette.Success700,
    signalSuccessBorder     = Color(0x2BABD0A8),

    signalWarningText       = DPPalette.Warning300,
    signalWarningIcon       = DPPalette.Warning300,
    signalWarningSurface    = DPPalette.Warning700,
    signalWarningBorder     = Color(0x2BF0BE76),

    signalErrorText         = DPPalette.Error300,
    signalErrorIcon         = DPPalette.Error300,
    signalErrorSurface      = DPPalette.Error700,
    signalErrorBorder       = Color(0x2BFFB3B0),

    signalInfoText          = DPPalette.Info300,
    signalInfoIcon          = DPPalette.Info300,
    signalInfoSurface       = DPPalette.Info700,
    signalInfoBorder        = Color(0x2BA5CCE1),

    signalWaitingText       = DPPalette.Indigo300,
    signalWaitingIcon       = DPPalette.Indigo300,
    signalWaitingSurface    = DPPalette.Indigo700,
    signalWaitingBorder     = Color(0x2BC8BFFF),

    interactivePrimaryFill     = DPPalette.Surface100,
    interactivePrimaryText     = DPPalette.Surface950,
    interactivePrimaryPressed  = DPPalette.Surface200,
    interactiveSecondaryFill   = Color.Transparent,
    interactiveSecondaryBorder = Color(0x26FFFFFF),
    interactiveDestructiveFill = DPPalette.Error500,
    interactiveDestructiveText = DPPalette.Error300,

    categoryAndroid  = DPPalette.CategoryAndroid,
    categoryKotlin   = DPPalette.CategoryKotlin,
    categoryIOS      = DPPalette.CategoryIOS,
    categorySwift    = DPPalette.CategorySwift,
    categoryFlutter  = DPPalette.CategoryFlutter,
    categoryRN       = DPPalette.CategoryRN,
    categoryWeb      = DPPalette.CategoryWeb,
    categoryDevOps   = DPPalette.CategoryDevOps,
    categoryAI       = DPPalette.CategoryAI,
    categoryGeneral  = DPPalette.CategoryGeneral,

    isDark = true,
)

// ─────────────────────────────────────────────────────────────────────────────
// LIGHT COLOUR SET
// ─────────────────────────────────────────────────────────────────────────────

val LightColours = DPColours(
    backgroundBase          = DPPalette.Surface50,
    backgroundElevated      = DPPalette.White,
    backgroundOverlay       = DPPalette.White,
    backgroundSubtle        = Color(0xFFF1EDEC),
    backgroundInteractive   = Color(0xFFEBE7E6),

    borderDefault           = Color(0x14000000),
    borderStrong            = Color(0x26000000),
    borderSubtle            = Color(0x0A000000),

    textPrimary             = Color(0xFF1C1B1B),
    textSecondary           = Color(0xFF444844),
    textTertiary            = Color(0xFF757874),
    textDisabled            = Color(0xFFC5C7C2),
    textOnAccent            = DPPalette.White,

    accentPrimary           = DPPalette.Primary600,
    accentPrimaryText       = DPPalette.White,
    accentPrimarySubtle     = Color(0x1A293727),
    accentPrimaryBorder     = Color(0x33293727),

    signalSuccessText       = DPPalette.Success600,
    signalSuccessIcon       = DPPalette.Success600,
    signalSuccessSurface    = DPPalette.Success100,
    signalSuccessBorder     = Color(0xFFA8D9BE),

    signalWarningText       = DPPalette.Warning600,
    signalWarningIcon       = DPPalette.Warning600,
    signalWarningSurface    = DPPalette.Warning100,
    signalWarningBorder     = Color(0xFFF5C878),

    signalErrorText         = DPPalette.Error600,
    signalErrorIcon         = DPPalette.Error600,
    signalErrorSurface      = Color(0xFFFDEBEB),
    signalErrorBorder       = Color(0xFFF5A8A2),

    signalInfoText          = DPPalette.Info600,
    signalInfoIcon          = DPPalette.Info600,
    signalInfoSurface       = DPPalette.Info100,
    signalInfoBorder        = Color(0xFFA8D0EC),

    signalWaitingText       = DPPalette.Indigo600,
    signalWaitingIcon       = DPPalette.Indigo600,
    signalWaitingSurface    = DPPalette.Indigo100,
    signalWaitingBorder     = Color(0xFFC4A8F5),

    interactivePrimaryFill     = DPPalette.Primary600,
    interactivePrimaryText     = DPPalette.White,
    interactivePrimaryPressed  = DPPalette.Primary500,
    interactiveSecondaryFill   = Color.Transparent,
    interactiveSecondaryBorder = Color(0x26000000),
    interactiveDestructiveFill = DPPalette.Error600,
    interactiveDestructiveText = DPPalette.White,

    categoryAndroid  = DPPalette.CategoryAndroid,
    categoryKotlin   = DPPalette.CategoryKotlin,
    categoryIOS      = DPPalette.CategoryIOS,
    categorySwift    = DPPalette.CategorySwift,
    categoryFlutter  = DPPalette.CategoryFlutter,
    categoryRN       = DPPalette.CategoryRN,
    categoryWeb      = DPPalette.CategoryWeb,
    categoryDevOps   = DPPalette.CategoryDevOps,
    categoryAI       = DPPalette.CategoryAI,
    categoryGeneral  = DPPalette.CategoryGeneral,

    isDark = false,
)

val LocalDPColours = staticCompositionLocalOf { DarkColours }

// ─────────────────────────────────────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────────────────────────────────────

fun DPColours.categoryColour(category: String): Color = when (category.lowercase()) {
    "android"               -> categoryAndroid
    "kotlin", "kmp"         -> categoryKotlin
    "ios"                   -> categoryIOS
    "swift"                 -> categorySwift
    "flutter"               -> categoryFlutter
    "react native", "rn"    -> categoryRN
    "web"                   -> categoryWeb
    "devops", "ci", "ci/cd" -> categoryDevOps
    "ai", "ml"              -> categoryAI
    else                    -> categoryGeneral
}

fun Color.atTenPercent(): Color     = this.copy(alpha = 0.10f)
fun Color.atFifteenPercent(): Color = this.copy(alpha = 0.15f)
fun Color.atTwentyPercent(): Color  = this.copy(alpha = 0.20f)
