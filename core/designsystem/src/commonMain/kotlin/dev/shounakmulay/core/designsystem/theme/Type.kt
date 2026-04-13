package dev.shounakmulay.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import devpulse.core.designsystem.generated.resources.Res
import devpulse.core.designsystem.generated.resources.dmmono_italic
import devpulse.core.designsystem.generated.resources.dmmono_light
import devpulse.core.designsystem.generated.resources.dmmono_light_italic
import devpulse.core.designsystem.generated.resources.dmmono_medium
import devpulse.core.designsystem.generated.resources.dmmono_medium_italic
import devpulse.core.designsystem.generated.resources.dmmono_regular
import devpulse.core.designsystem.generated.resources.inter
import devpulse.core.designsystem.generated.resources.newsreader
import org.jetbrains.compose.resources.Font

@Composable
fun bodyFontFamily() = FontFamily(
    Font(
        Res.font.inter,
        FontWeight.Thin,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Thin,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.inter,
        FontWeight.ExtraLight,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.ExtraLight,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.inter,
        FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.inter,
        FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.inter,
        FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.inter,
        FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.inter,
        FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.inter,
        FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.ExtraBold,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.inter,
        FontWeight.Black,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Black,
            style = FontStyle.Normal,
        ),
    ),
)

@Composable
fun displayFontFamily() = FontFamily(
    Font(
        Res.font.newsreader,
        FontWeight.ExtraLight,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.ExtraLight,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.newsreader,
        FontWeight.Light,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Light,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.newsreader,
        FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.newsreader,
        FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Medium,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.newsreader,
        FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.newsreader,
        FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
        ),
    ),
    Font(
        Res.font.newsreader,
        FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(
            weight = FontWeight.ExtraBold,
            style = FontStyle.Normal,
        ),
    ),
)

@Composable
fun monoFontFamily() = FontFamily(
    Font(Res.font.dmmono_light, FontWeight.Light),
    Font(Res.font.dmmono_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(Res.font.dmmono_regular, FontWeight.Normal),
    Font(Res.font.dmmono_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.dmmono_medium, FontWeight.Medium),
    Font(Res.font.dmmono_medium_italic, FontWeight.Medium, FontStyle.Italic),
)

class DPTextVariants(
    displayFont: FontFamily,
    bodyFont: FontFamily,
) {
    val DisplayLarge = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W400,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    )
    val DisplayMedium = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W400,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    )
    val DisplaySmall = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    )
    val HeadingLarge = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W600,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    )
    val HeadingMedium = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W600,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    )
    val HeadingSmall = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    )
    val TitleLarge = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W600,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    )
    val TitleMedium = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    )
    val TitleSmall = TextStyle(
        fontFamily = displayFont,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    val BodyLarge = TextStyle(
        fontFamily = bodyFont,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    )
    val BodyMedium = TextStyle(
        fontFamily = bodyFont,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    )
    val BodySmall = TextStyle(
        fontFamily = bodyFont,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    )
    val LabelLarge = TextStyle(
        fontFamily = bodyFont,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )
    val LabelMedium = TextStyle(
        fontFamily = bodyFont,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
    val LabelSmall = TextStyle(
        fontFamily = bodyFont,
        fontWeight = FontWeight.W500,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
}

@Composable
fun dpTextVariants(): DPTextVariants = DPTextVariants(
    displayFont = displayFontFamily(),
    bodyFont = bodyFontFamily(),
)

@Composable
fun appTypography(): Typography {
    val variants = dpTextVariants()

    return Typography(
        displayLarge = variants.DisplayLarge,
        displayMedium = variants.DisplayMedium,
        displaySmall = variants.DisplaySmall,
        headlineLarge = variants.HeadingLarge,
        headlineMedium = variants.HeadingMedium,
        headlineSmall = variants.HeadingSmall,
        titleLarge = variants.TitleLarge,
        titleMedium = variants.TitleMedium,
        titleSmall = variants.TitleSmall,
        bodyLarge = variants.BodyLarge,
        bodyMedium = variants.BodyMedium,
        bodySmall = variants.BodySmall,
        labelLarge = variants.LabelLarge,
        labelMedium = variants.LabelMedium,
        labelSmall = variants.LabelSmall,
    )
}

