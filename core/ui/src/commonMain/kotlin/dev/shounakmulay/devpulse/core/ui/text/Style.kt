package dev.shounakmulay.devpulse.core.ui.text

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.serialization.Serializable

@Serializable
enum class Style {
    BOLD,
    ITALIC,
}

internal fun Style.toSpanStyle(): SpanStyle = when (this) {
    Style.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
    Style.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
}