package dev.shounakmulay.devpulse.core.data.feed.parser.html

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.safety.Cleaner
import com.fleeksoft.ksoup.safety.Safelist
import dev.shounakmulay.devpulse.bridge.markdownconverter.HtmlToMarkdownConverter
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient.Companion
import dev.shounakmulay.devpulse.core.network.bodyAsText
import org.koin.core.annotation.Factory

@Factory
class HtmlParser(
    private val networkClient: DevPulseNetworkClient,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend fun parse(articleUrl: String): String? = dispatcherProvider.runCatchingOnDefault {
        val html = networkClient.get(
            url = articleUrl,
            headers = mapOf(
                Companion.HttpHeaders.USER_AGENT to Companion.HttpUserAgent.BROWSER
            )
        ).bodyAsText()
        val document = Ksoup.parse(html)

        val cleanedHtml = Cleaner(safelist = createStructuralSafelist())
            .clean(document)
            .html()

        cleanedHtml
    }.getOrNull()

    private fun createStructuralSafelist(): Safelist {
        return Safelist.relaxed()
            // Document Structure & Layout
            .addTags(
                Tag.ARTICLE, Tag.SECTION, Tag.FOOTER, Tag.MAIN, Tag.ASIDE,
                Tag.FIGURE, Tag.FIGCAPTION, Tag.DETAILS, Tag.SUMMARY, Tag.DIV, Tag.SPAN
            )
            // Headings & Text Formatting
            .addTags(
                Tag.H1,
                Tag.H2,
                Tag.H3,
                Tag.H4,
                Tag.H5,
                Tag.H6,
                Tag.HR,
                Tag.MARK,
                Tag.SMALL,
                Tag.KBD,
                Tag.SAMP
            )
            // Code blocks & Syntax Highlighting
            .addTags(Tag.PRE, Tag.CODE)
            .addAttributes(Tag.CODE, Attr.CLASS) // Preserves language classes like language-kotlin
            .addAttributes(Tag.PRE, Attr.CLASS)
            // Lists
            .addTags(Tag.UL, Tag.OL, Tag.LI, Tag.DL, Tag.DT, Tag.DD)
            // Embedded Media
            .addTags(Tag.AUDIO, Tag.VIDEO, Tag.SOURCE, Tag.IFRAME)
            .addAttributes(Tag.AUDIO, Attr.SRC, Attr.CONTROLS, Attr.AUTOPLAY)
            .addAttributes(Tag.VIDEO, Attr.SRC, Attr.CONTROLS, Attr.POSTER, Attr.WIDTH, Attr.HEIGHT)
            .addAttributes(Tag.SOURCE, Attr.SRC, Attr.TYPE)
            .addAttributes(
                Tag.IFRAME,
                Attr.SRC,
                Attr.WIDTH,
                Attr.HEIGHT,
                Attr.FRAMEBORDER,
                Attr.ALLOWFULLSCREEN
            )
            // Links & Images (preserves standard href / src / alt)
            .addAttributes(Tag.A, Attr.HREF, Attr.TITLE, Attr.TARGET)
            .addAttributes(Tag.IMG, Attr.SRC, Attr.ALT, Attr.TITLE, Attr.WIDTH, Attr.HEIGHT)
            // Tables
            .addTags(
                Tag.TABLE,
                Tag.THEAD,
                Tag.TBODY,
                Tag.TFOOT,
                Tag.TR,
                Tag.TH,
                Tag.TD,
                Tag.CAPTION,
                Tag.COLGROUP,
                Tag.COL
            )
            .addAttributes(Tag.TD, Attr.COLSPAN, Attr.ROWSPAN)
            .addAttributes(Tag.TH, Attr.COLSPAN, Attr.ROWSPAN, Attr.SCOPE)
    }

    private object Tag {
        const val ARTICLE = "article"
        const val SECTION = "section"
        const val FOOTER = "footer"
        const val MAIN = "main"
        const val ASIDE = "aside"
        const val FIGURE = "figure"
        const val FIGCAPTION = "figcaption"
        const val DETAILS = "details"
        const val SUMMARY = "summary"
        const val DIV = "div"
        const val SPAN = "span"
        const val H1 = "h1";
        const val H2 = "h2";
        const val H3 = "h3"
        const val H4 = "h4";
        const val H5 = "h5";
        const val H6 = "h6"
        const val HR = "hr";
        const val MARK = "mark";
        const val SMALL = "small"
        const val KBD = "kbd";
        const val SAMP = "samp"
        const val PRE = "pre";
        const val CODE = "code"
        const val UL = "ul";
        const val OL = "ol";
        const val LI = "li"
        const val DL = "dl";
        const val DT = "dt";
        const val DD = "dd"
        const val AUDIO = "audio";
        const val VIDEO = "video"
        const val SOURCE = "source";
        const val IFRAME = "iframe"
        const val A = "a";
        const val IMG = "img"
        const val TABLE = "table";
        const val THEAD = "thead";
        const val TBODY = "tbody"
        const val TFOOT = "tfoot";
        const val TR = "tr";
        const val TH = "th"
        const val TD = "td";
        const val CAPTION = "caption"
        const val COLGROUP = "colgroup";
        const val COL = "col"
    }

    private object Attr {
        const val CLASS = "class"
        const val SRC = "src";
        const val CONTROLS = "controls";
        const val AUTOPLAY = "autoplay"
        const val POSTER = "poster";
        const val WIDTH = "width";
        const val HEIGHT = "height"
        const val TYPE = "type";
        const val FRAMEBORDER = "frameborder"
        const val ALLOWFULLSCREEN = "allowfullscreen"
        const val HREF = "href";
        const val TITLE = "title";
        const val TARGET = "target"
        const val ALT = "alt"
        const val COLSPAN = "colspan";
        const val ROWSPAN = "rowspan";
        const val SCOPE = "scope"
    }
}

