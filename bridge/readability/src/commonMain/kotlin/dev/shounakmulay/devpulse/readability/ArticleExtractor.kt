package dev.shounakmulay.devpulse.readability

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import dev.shounakmulay.devpulse.readability.estimate.ReadingTimeEstimator
import dev.shounakmulay.devpulse.readability.extraction.MetadataExtractor
import dev.shounakmulay.devpulse.readability.extraction.TitleExtractor
import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns
import dev.shounakmulay.devpulse.readability.model.ExtractedArticle
import dev.shounakmulay.devpulse.readability.model.ExtractionOptions
import dev.shounakmulay.devpulse.readability.pipeline.ArticleGrabber
import dev.shounakmulay.devpulse.readability.pipeline.DocumentPreprocessor
import dev.shounakmulay.devpulse.readability.pipeline.PostProcessor
import org.koin.core.annotation.Factory

/**
 * Main entry point for article extraction.
 *
 * Usage:
 * ```
 * val extractor = ArticleExtractor()
 * val article = extractor.extract(html, "https://example.com/article")
 * ```
 *
 * This is the Kotlin Multiplatform equivalent of:
 * ```
 * var reader = new Readability(document);
 * var article = reader.parse();
 * ```
 */
@Factory
class ArticleExtractor {

    /**
     * Extract an article from an HTML string.
     *
     * @param html The full HTML content of the page
     * @param baseUrl The base URL for resolving relative URIs (e.g., the page URL)
     * @param options Extraction configuration options
     * @return The extracted article, or null if no article could be extracted
     */
    fun extract(
        html: String,
        baseUrl: String? = null,
        options: ExtractionOptions = ExtractionOptions()
    ): ExtractedArticle? {
        // Parse HTML into DOM
        val doc = if (baseUrl != null) {
            Ksoup.parse(html, baseUrl)
        } else {
            Ksoup.parse(html)
        }

        // Enforce max elements limit
        if (options.maxElemsToParse > 0) {
            val numTags = doc.getElementsByTag("*").size
            if (numTags > options.maxElemsToParse) {
                return null // Document too large
            }
        }

        // Step 1: Preprocess — unwrap noscript images
        DocumentPreprocessor.unwrapNoscriptImages(doc)

        // Step 2: Extract all metadata (JSON-LD + meta tags)
        val metadata = MetadataExtractor.extract(doc, options.disableJSONLD)

        // Step 3: Remove scripts
        DocumentPreprocessor.removeScripts(doc)

        // Step 4: Prep document
        DocumentPreprocessor.prepDocument(doc)

        // Step 5: Extract title
        val title = metadata.title ?: TitleExtractor.extract(doc)

        // Step 6: Grab the article content.
        // Content cleaning (ContentCleaner.prepArticle) now runs internally inside
        // ArticleGrabber.grab() on every retry attempt, before the char-threshold
        // check — mirroring Readability.js, where _prepArticle runs inside the
        // _grabArticle retry loop rather than once after it returns.
        val body = doc.body() ?: return null
        val articleContent = ArticleGrabber.grab(
            page = body,
            doc = doc,
            charThreshold = options.charThreshold,
            nbTopCandidates = options.nbTopCandidates,
            linkDensityModifier = options.linkDensityModifier,
        ) ?: return null

        // Step 7: Post-process (fix URIs, clean classes, simplify nesting)
        val classesToPreserve = listOf("page") + options.classesToPreserve
        PostProcessor.process(articleContent, doc, options.keepClasses, classesToPreserve)

        // Step 8: Extract byline from article content if not found in metadata
        var byline = metadata.byline
        if (byline == null) {
            // Try to find byline in the extracted content
            byline = extractBylineFromArticle(articleContent)
        }

        // Step 9: Extract excerpt
        var excerpt = metadata.excerpt
        if (excerpt == null) {
            val paragraphs = articleContent.getElementsByTag("p")
            if (paragraphs.isNotEmpty()) {
                excerpt = paragraphs[0].text().trim()
            }
        }

        // Step 10: Serialize content — strip any remaining script/style tags
        articleContent.getElementsByTag("script").forEach { it.remove() }
        articleContent.getElementsByTag("style").forEach { it.remove() }
        val content = articleContent.html()
        val textContent = articleContent.text()
        val imageCount = articleContent.getElementsByTag("img").size

        // Step 11: Estimate reading time
        val readingTimeMinutes = ReadingTimeEstimator.estimate(textContent)

        // Step 12: Detect text direction
        var dir: String? = null
        var lang: String? = null
        for (node in articleContent.getAllElements()) {
            if (dir == null && node.hasAttr("dir")) {
                dir = node.attr("dir")
            }
            if (lang == null && node.hasAttr("lang")) {
                lang = node.attr("lang")
            }
            if (dir != null && lang != null) break
        }

        return ExtractedArticle(
            title = title,
            byline = byline,
            dir = dir,
            lang = lang ?: metadata.lang,
            content = content,
            textContent = textContent,
            length = textContent.length,
            excerpt = excerpt,
            siteName = metadata.siteName,
            publishedTime = metadata.publishedTime,
            imageCount = imageCount,
            readingTimeMinutes = readingTimeMinutes,
        )
    }

    /**
     * Try to find a byline in the article content.
     * Looks for elements with rel="author", itemprop containing "author",
     * or class/id matching the byline regex pattern.
     */
    private fun extractBylineFromArticle(articleContent: Element): String? {
        val allElements = articleContent.getAllElements()
        for (el in allElements) {
            val rel = el.attr("rel")
            val itemprop = el.attr("itemprop")
            val matchString = "${el.className()} ${el.id()}"

            val isByline = (rel == "author" ||
                    (itemprop.isNotEmpty() && itemprop.contains("author")) ||
                    RegexPatterns.byline.containsMatchIn(matchString))

            if (isByline) {
                val text = el.text().trim()
                if (text.isNotEmpty() && text.length < 100) {
                    return text
                }
            }
        }
        return null
    }
}