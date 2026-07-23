package dev.shounakmulay.devpulse.core.data.feed.parser.common

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.safety.Cleaner
import com.fleeksoft.ksoup.safety.Safelist
import org.koin.core.annotation.Factory

/**
 * Parses raw HTML content from feed entries (e.g. `content:encoded`, `<description>`,
 * Atom `<content>` / `<summary>`) to extract structured metadata:
 *
 * - **heroImage**: the first non-GIF `<img src>` URL found in the HTML body
 * - **textContent**: the visible text content of the cleaned HTML body
 * - **cleanedHtml**: the sanitised HTML (scripts, styles, etc. removed)
 * - **audioUrl**: the first `<audio>` tag's `src` URL (fallback when no audio enclosure)
 *
 * Ported from Twine's `ArticleHtmlParser` (github.com/msasikanth/twine).
 * Twine is GPL-3.0; DevPulse's author should ensure licence compatibility.
 */
@Factory
class ArticleHtmlParser {

    companion object {
        private const val TAG_BODY = "body"
        private const val TAG_IMG = "img"
        private const val TAG_FIGCAPTION = "figcaption"
        private const val ATTR_SRC = "src"
        private const val TAG_AUDIO = "audio"
        private const val TAG_SOURCE = "source"
        private const val ATTR_TYPE = "type"

        private const val MAX_CONTENT_SIZE = 10 * 1024 * 1024 // 10MB

        private val gifRegex = Regex("/\\.gif(\\?.*)?\\$/i")
    }

    private val allowedContentTags by lazy {
        Safelist()
            .addTags(TAG_FIGCAPTION, TAG_IMG, TAG_AUDIO, TAG_SOURCE)
            .addAttributes(TAG_IMG, ATTR_SRC)
            .addAttributes(TAG_AUDIO, ATTR_SRC)
            .addAttributes(TAG_SOURCE, ATTR_SRC, ATTR_TYPE)
    }

    fun parse(htmlContent: String): Result? {
        if (htmlContent.isBlank() || htmlContent.length > MAX_CONTENT_SIZE) return null

        return try {
            val originalHtmlDocument =
                Ksoup.parse(htmlContent).also {
                    it.head().remove()
                    it.select("script, style, noscript").remove()
                }
            val cleanedHtmlDocument = Cleaner(allowedContentTags).clean(originalHtmlDocument)
            val body = cleanedHtmlDocument.body().first()
            val heroImage =
                body.firstNotNullOfOrNull {
                    val imageUrl = it.attr(ATTR_SRC)
                    if (it.tagName() == TAG_IMG && !gifRegex.containsMatchIn(imageUrl)) {
                        imageUrl.removeSurrounding("\"")
                    } else {
                        null
                    }
                }

            val audioUrl =
                body.select(TAG_AUDIO).firstOrNull()?.let { audio ->
                    val src = audio.attr(ATTR_SRC)
                    src.ifBlank { audio.select(TAG_SOURCE).attr(ATTR_SRC) }
                }

            Result(
                heroImage = heroImage,
                textContent = body.ownText(),
                cleanedHtml = originalHtmlDocument.html(),
                audioUrl = audioUrl,
            )
        } catch (e: Exception) {
            null
        }
    }

    data class Result(
        val heroImage: String?,
        val textContent: String,
        val cleanedHtml: String,
        val audioUrl: String? = null,
    )
}