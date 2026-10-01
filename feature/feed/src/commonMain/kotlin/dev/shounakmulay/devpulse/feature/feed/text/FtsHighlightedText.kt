package dev.shounakmulay.devpulse.feature.feed.text

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.ui.text.TextResource

fun parseFtsHighlightedText(text: String): TextResource {
    if (HL_START !in text) return TextResource.fromText(text)

    return TextResource.fromAnnotatedString(
        buildAnnotatedString {
            var cursor = 0

            while (cursor < text.length) {
                val start = text.indexOf(HL_START, cursor)

                if (start == -1) {
                    append(text.substring(cursor))
                    break
                }

                if (start > cursor) {
                    append(text.substring(cursor, start))
                }

                val contentStart = start + HL_START.length
                val end = text.indexOf(HL_END, contentStart)

                if (end == -1) {
                    append(text.substring(contentStart))
                    break
                }

                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(text.substring(contentStart, end))
                }

                cursor = end + HL_END.length
            }
        }
    )
}
