package dev.shounakmulay.core.designsystem.foundation

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// FONT FAMILIES
//
// Newsreader — editorial serif (displayFontFamily)
// Inter      — clean humanist sans (bodyFontFamily)
// DM Mono    — monospace for code, timestamps, version numbers, eyebrows
//
// To wire up custom fonts:
//   1. Download TTF files from Google Fonts:
//      - Newsreader: fonts.google.com/specimen/Newsreader
//      - Inter:      fonts.google.com/specimen/Inter
//      - DM Mono:    fonts.google.com/specimen/DM+Mono
//   2. Place files in: composeApp/src/commonMain/composeResources/font/
//      Named: newsreader_light.ttf, newsreader_regular.ttf, newsreader_medium.ttf,
//             newsreader_semibold.ttf, newsreader_light_italic.ttf,
//             newsreader_italic.ttf, newsreader_medium_italic.ttf,
//             inter_light.ttf, inter_regular.ttf, inter_medium.ttf,
//             inter_semibold.ttf,
//             dmmono_regular.ttf, dmmono_medium.ttf, dmmono_italic.ttf
//   3. Replace the FontFamily definitions below with:
//      val NewsreaderFamily = FontFamily(
//          Font(Res.font.newsreader_light, FontWeight.Light), ...
//      )
//
// Using system font fallbacks until font files are added.
// ─────────────────────────────────────────────────────────────────────────────

val NewsreaderFamily: FontFamily = FontFamily.Serif
val InterFamily: FontFamily      = FontFamily.SansSerif
val DMMonoFamily: FontFamily     = FontFamily.Monospace

// ─────────────────────────────────────────────────────────────────────────────
// TYPE SCALE
// Role-named, not size-named. Use the role, never hardcode sp values.
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class DPTypography(
    // ── Display — Newsreader serif (hero moments, empty states, screen titles)
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,

    // ── Headline — Inter sans (screen titles, section headers)
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,

    // ── Title — Inter sans (feed article titles, list item primaries)
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,

    // ── Body — Inter sans (article body, descriptions, excerpts)
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,

    // ── Label — Inter sans (UI labels, buttons, tab labels)
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,

    // ── Code / Mono — DM Mono (version numbers, timestamps, IDs, API keys)
    val codeMedium: TextStyle,
    val codeSmall: TextStyle,
    val eyebrow: TextStyle,
)

// ─────────────────────────────────────────────────────────────────────────────
// DEFAULT TYPE SCALE
// ─────────────────────────────────────────────────────────────────────────────

private val baseLHS = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

val DefaultTypography = DPTypography(

    displayLarge = TextStyle(
        fontFamily = NewsreaderFamily,
        fontWeight = FontWeight.Light,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.5).sp,
        lineHeightStyle = baseLHS,
    ),
    displayMedium = TextStyle(
        fontFamily = NewsreaderFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.3).sp,
        lineHeightStyle = baseLHS,
    ),
    displaySmall = TextStyle(
        fontFamily = NewsreaderFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp,
        lineHeightStyle = baseLHS,
    ),

    headlineLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
        lineHeightStyle = baseLHS,
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = (-0.1).sp,
        lineHeightStyle = baseLHS,
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),

    titleLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),
    titleMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),
    titleSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),

    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp,
        lineHeightStyle = baseLHS,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Light,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),

    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
        lineHeightStyle = baseLHS,
    ),

    codeMedium = TextStyle(
        fontFamily = DMMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),
    codeSmall = TextStyle(
        fontFamily = DMMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = baseLHS,
    ),
    eyebrow = TextStyle(
        fontFamily = DMMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp,
        lineHeightStyle = baseLHS,
    ),
)

val LocalDPTypography = staticCompositionLocalOf { DefaultTypography }

// ─────────────────────────────────────────────────────────────────────────────
// MATERIAL TYPOGRAPHY BRIDGE
// Maps DPTypography to Material3 Typography for MaterialTheme wiring.
// ─────────────────────────────────────────────────────────────────────────────

val DefaultMaterial3Typography = Typography(
    displayLarge   = DefaultTypography.displayLarge,
    displayMedium  = DefaultTypography.displayMedium,
    displaySmall   = DefaultTypography.displaySmall,
    headlineLarge  = DefaultTypography.headlineLarge,
    headlineMedium = DefaultTypography.headlineMedium,
    headlineSmall  = DefaultTypography.headlineSmall,
    titleLarge     = DefaultTypography.titleLarge,
    titleMedium    = DefaultTypography.titleMedium,
    titleSmall     = DefaultTypography.titleSmall,
    bodyLarge      = DefaultTypography.bodyLarge,
    bodyMedium     = DefaultTypography.bodyMedium,
    bodySmall      = DefaultTypography.bodySmall,
    labelLarge     = DefaultTypography.labelLarge,
    labelMedium    = DefaultTypography.labelMedium,
    labelSmall     = DefaultTypography.labelSmall,
)
