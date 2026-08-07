@file:Suppress("unused")

package dev.shounakmulay.devpulse.readability.model

import kotlinx.serialization.Serializable

/**
 * The result of article extraction.
 *
 * Mirrors the return value of Readability.prototype.parse().
 */
@Serializable
data class ExtractedArticle(
    /** Extracted article title */
    val title: String,
    /** Author byline, if detected */
    val byline: String? = null,
    /** Text direction: "ltr", "rtl", or null */
    val dir: String? = null,
    /** Document language */
    val lang: String? = null,
    /** Extracted article HTML content */
    val content: String,
    /** Plain text content of the article */
    val textContent: String,
    /** Character count of textContent */
    val length: Int,
    /** Short excerpt / first paragraph */
    val excerpt: String? = null,
    /** Site name from metadata */
    val siteName: String? = null,
    /** Published date from metadata, ISO format if available */
    val publishedTime: String? = null,
    /** Count of <img> tags in the extracted content */
    val imageCount: Int = 0,
    /** Estimated reading time in minutes */
    val readingTimeMinutes: Int = 0,
)

/**
 * Configuration options for extraction.
 *
 * Mirrors Readability constructor options.
 */
@Serializable
data class ExtractionOptions(
    /** Minimum character threshold to consider extraction successful (default: 500) */
    val charThreshold: Int = 500,
    /** Maximum number of elements to parse (0 = no limit) */
    val maxElemsToParse: Int = 0,
    /** Number of top candidates to consider (default: 5) */
    val nbTopCandidates: Int = 5,
    /** Whether to keep CSS classes in the output HTML */
    val keepClasses: Boolean = false,
    /** Additional CSS classes to preserve */
    val classesToPreserve: List<String> = emptyList(),
    /** Whether to disable JSON-LD metadata extraction */
    val disableJSONLD: Boolean = false,
    /** Base URL for resolving relative URIs */
    val baseUrl: String? = null,
    /** Link density modifier — added to link density threshold */
    val linkDensityModifier: Double = 0.0,
    /** Enable debug logging */
    val debug: Boolean = false,
    /** Custom function to serialize the content element (default: outerHtml) */
    val serializeContent: Boolean = true,
)

/**
 * Metadata extracted from HTML meta tags and JSON-LD.
 */
@Serializable
data class ArticleMetadata(
    val title: String? = null,
    val byline: String? = null,
    val excerpt: String? = null,
    val siteName: String? = null,
    val publishedTime: String? = null,
    val dir: String? = null,
    val lang: String? = null,
)

/**
 * Internal state attached to DOM nodes during scoring.
 */
class NodeReadabilityState(
    var contentScore: Double = 0.0,
)
