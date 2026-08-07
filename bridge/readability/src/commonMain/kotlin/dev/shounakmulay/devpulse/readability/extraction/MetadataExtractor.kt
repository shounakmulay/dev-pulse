@file:Suppress("unused", "UNCHECKED_CAST")

package dev.shounakmulay.devpulse.readability.extraction

import com.fleeksoft.ksoup.nodes.Document
import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns
import dev.shounakmulay.devpulse.readability.model.ArticleMetadata
import dev.shounakmulay.devpulse.readability.extraction.MetadataExtractor.extractJSONLD
import dev.shounakmulay.devpulse.readability.extraction.MetadataExtractor.extractMetaTags
import dev.shounakmulay.devpulse.readability.heuristics.TagSets
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.text.get
import kotlin.text.iterator

/**
 * Metadata extraction from HTML meta tags and JSON-LD structured data.
 *
 * ## Priority order for each field
 *
 * ### title
 * jsonld.title > jsonld.headline > og:title > twitter:title > dc:title > <title> tag
 *
 * ### byline (author)
 * jsonld.author.name > jsonld.author > article:author > dc:creator >
 * twitter:creator > sailthru.author > author meta
 *
 * ### excerpt (description)
 * jsonld.description > og:description > twitter:description > dc:description >
 * meta description
 *
 * ### siteName
 * jsonld.publisher.name > og:site_name > twitter:site
 *
 * ### publishedTime
 * jsonld.datePublished > jsonld.dateModified > article:published_time >
 * og:article:published_time
 *
 * ### lang
 * jsonld.inLanguage > html[lang] > meta[content-language] > og:locale
 *
 * ### dir
 * jsonld.textDirection > html[dir]
 */
object MetadataExtractor {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // ─────────────────────────────────────────────────────────────
    // 1. extractJSONLD
    // ─────────────────────────────────────────────────────────────

    /**
     * Extract metadata from JSON-LD `<script type="application/ld+json">` blocks.
     *
     * Handles:
     * - Single JSON-LD objects: `{"@type": "NewsArticle", ...}`
     * - Top-level arrays: `[{...}, {...}]`
     * - `@graph` arrays: `{"@graph": [{...}, {...}]}`
     * - Validates `@context` contains a schema.org reference
     * - Filters items by `@type` matching [RegexPatterns.jsonLdArticleTypes] regex
     *
     * @param doc The parsed HTML document.
     * @return [ArticleMetadata] with any fields found in JSON-LD.
     */
    fun extractJSONLD(doc: Document): ArticleMetadata {
        val scriptElements = doc.select("script[type=\"application/ld+json\"]")
        if (scriptElements.isEmpty()) return ArticleMetadata()

        val allItems = mutableListOf<JsonObject>()

        for (script in scriptElements) {
            val jsonText = script.html().trim()
            if (jsonText.isEmpty()) continue

            val element = try {
                json.parseToJsonElement(jsonText)
            } catch (_: Exception) {
                continue
            }

            val flatList = flattenJsonLd(element).filterIsSchemaOrg()
            allItems.addAll(flatList)
        }

        if (allItems.isEmpty()) return ArticleMetadata()

        // Pick the first article-type item that has useful content
        val item = allItems.firstOrNull { obj ->
            obj.containsKey("title") || obj.containsKey("headline") ||
                obj.containsKey("name") || obj.containsKey("description") ||
                obj.containsKey("author") || obj.containsKey("datePublished")
        } ?: allItems.first()

        return ArticleMetadata(
            title = extractJsonLdTitle(item),
            byline = extractJsonLdAuthor(item),
            excerpt = extractJsonLdExcerpt(item),
            siteName = extractJsonLdSiteName(item),
            publishedTime = extractJsonLdPublishedTime(item),
            lang = extractJsonLdLang(item),
            dir = extractJsonLdDir(item),
        )
    }

    // ─────────────────────────────────────────────────────────────
    // 2. extractMetaTags
    // ─────────────────────────────────────────────────────────────

    /**
     * Extract metadata from HTML `<meta>` and `<title>` tags.
     *
     * JSON-LD metadata takes priority over meta tags — any field already
     * set non-null in [jsonld] will NOT be overwritten by meta tag values.
     *
     * @param doc    The parsed HTML document.
     * @param jsonld Previously extracted JSON-LD metadata (priority).
     * @return Merged [ArticleMetadata] (JSON-LD wins where both present).
     */
    fun extractMetaTags(doc: Document, jsonld: ArticleMetadata): ArticleMetadata {
        // ── Title ──────────────────────────────────────────────
        // Priority: jsonld.title > og:title > twitter:title > dc:title > <title>
        val metaTitle = jsonld.title
            ?: getMetaContent(doc, "property", "og:title")
            ?: getMetaContent(doc, "name", "twitter:title")
            ?: getMetaContent(doc, "name", "dc:title")
            ?: getMetaContent(doc, "name", "dcterms.title")
            ?: doc.title()

        // ── Author / byline ────────────────────────────────────
        // Priority: jsonld.byline > article:author > dc:creator > twitter:creator
        //           > sailthru.author > author meta
        val metaByline = jsonld.byline
            ?: getMetaContent(doc, "property", "article:author")
            ?: getMetaContent(doc, "name", "dc:creator")
            ?: getMetaContent(doc, "name", "dcterms.creator")
            ?: getMetaContent(doc, "name", "twitter:creator")
            ?: getMetaContent(doc, "name", "sailthru.author")
            ?: getMetaContent(doc, "name", "author")

        // ── Excerpt / description ──────────────────────────────
        // Priority: jsonld.excerpt > og:description > twitter:description
        //           > dc:description > meta description
        val metaExcerpt = jsonld.excerpt
            ?: getMetaContent(doc, "property", "og:description")
            ?: getMetaContent(doc, "name", "twitter:description")
            ?: getMetaContent(doc, "name", "dc:description")
            ?: getMetaContent(doc, "name", "dcterms.description")
            ?: getMetaContent(doc, "name", "description")

        // ── Site name ──────────────────────────────────────────
        // Priority: jsonld.siteName > og:site_name > twitter:site
        val metaSiteName = jsonld.siteName
            ?: getMetaContent(doc, "property", "og:site_name")
            ?: getMetaContent(doc, "name", "twitter:site")

        // ── Published time ─────────────────────────────────────
        // Priority: jsonld.publishedTime > article:published_time
        //           > og:article:published_time > dc:date
        val metaPublishedTime = jsonld.publishedTime
            ?: getMetaContent(doc, "property", "article:published_time")
            ?: getMetaContent(doc, "name", "article:published_time")
            ?: getMetaContent(doc, "property", "og:article:published_time")
            ?: getMetaContent(doc, "name", "dc:date")
            ?: getMetaContent(doc, "name", "dcterms.date")
            ?: getMetaContent(doc, "name", "date")

        // ── Language ───────────────────────────────────────────
        // Priority: jsonld.lang > html[lang] > meta content-language > og:locale
        val metaLang = jsonld.lang
            ?: doc.select("html").first()?.attr("lang")?.takeIf { it.isNotBlank() }
            ?: getMetaContent(doc, "http-equiv", "content-language")
            ?: getMetaContent(doc, "name", "content-language")
            ?: getMetaContent(doc, "property", "og:locale")

        // ── Direction ──────────────────────────────────────────
        // Priority: jsonld.dir > html[dir]
        val metaDir = jsonld.dir
            ?: doc.select("html").first()?.attr("dir")?.takeIf { it.isNotBlank() }

        return ArticleMetadata(
            title = metaTitle?.let { unescapeHtml(it) },
            byline = metaByline?.let { unescapeHtml(it) },
            excerpt = metaExcerpt?.let { unescapeHtml(it) },
            siteName = metaSiteName?.let { unescapeHtml(it) },
            publishedTime = metaPublishedTime?.let { unescapeHtml(it) },
            lang = metaLang?.let { unescapeHtml(it) },
            dir = metaDir?.let { unescapeHtml(it) },
        )
    }

    // ─────────────────────────────────────────────────────────────
    // 3. extract — orchestrator
    // ─────────────────────────────────────────────────────────────

    /**
     * Full metadata extraction orchestration.
     *
     * 1. Calls [extractJSONLD] unless [disableJSONLD] is `true`.
     * 2. Passes the JSON-LD result into [extractMetaTags] which merges
     *    meta tag data with JSON-LD taking priority.
     *
     * @param doc           The parsed HTML document.
     * @param disableJSONLD If `true`, skip JSON-LD extraction entirely.
     * @return Final merged [ArticleMetadata].
     */
    fun extract(doc: Document, disableJSONLD: Boolean = false): ArticleMetadata {
        val jsonld = if (disableJSONLD) ArticleMetadata() else extractJSONLD(doc)
        return extractMetaTags(doc, jsonld)
    }

    // ─────────────────────────────────────────────────────────────
    // Private helpers: JSON-LD flattening & validation
    // ─────────────────────────────────────────────────────────────

    /**
     * Flatten a JSON-LD element into a list of [JsonObject] items.
     *
     * Handles:
     * - `JsonObject` with optional `@graph` array
     * - `JsonArray` of objects (top-level or inside @graph)
     * - Items that are nested in `@graph`
     */
    private fun flattenJsonLd(element: JsonElement): List<JsonObject> {
        val items = mutableListOf<JsonObject>()

        when (element) {
            is JsonObject -> {
                // Check for @graph
                val graph = element["@graph"]
                if (graph is JsonArray) {
                    for (graphItem in graph) {
                        when (graphItem) {
                            is JsonObject -> items.add(graphItem)
                            is JsonArray -> {
                                for (inner in graphItem) {
                                    if (inner is JsonObject) items.add(inner)
                                }
                            }
                            else -> { /* skip primitives in @graph */ }
                        }
                    }
                } else {
                    items.add(element)
                }
            }

            is JsonArray -> {
                for (item in element) {
                    when (item) {
                        is JsonObject -> items.add(item)
                        is JsonArray -> {
                            for (inner in item) {
                                if (inner is JsonObject) items.add(inner)
                            }
                        }
                        else -> { /* skip primitives in top-level array */ }
                    }
                }
            }

            else -> { /* unexpected root type */ }
        }

        return items
    }

    /**
     * Filter items to only those whose `@context` validates against
     * schema.org and whose `@type` matches [RegexPatterns.jsonLdArticleTypes].
     *
     * Items without a `@context` but with a matching `@type` are also
     * accepted (lenient fallback).
     */
    private fun List<JsonObject>.filterIsSchemaOrg(): List<JsonObject> {
        return this.filter { item ->
            // Must have a recognized article type
            val type = item["@type"]
            val typeStr = when (type) {
                is JsonPrimitive -> type.content
                is JsonArray -> type.firstOrNull()?.jsonPrimitive?.content
                else -> null
            } ?: return@filter false

            if (!RegexPatterns.jsonLdArticleTypes.matches(typeStr)) return@filter false

            // @context validation: must include schema.org URI (lenient)
            val context = item["@context"]
            val contextOk = when (context) {
                null -> true // lenient — some sites omit @context
                is JsonPrimitive -> context.content.contains("schema.org", ignoreCase = true)
                is JsonArray -> context.any { ctx ->
                    ctx is JsonPrimitive && ctx.content.contains("schema.org", ignoreCase = true)
                }
                else -> true
            }

            contextOk
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Private helpers: JSON-LD field extraction
    // ─────────────────────────────────────────────────────────────

    private fun extractJsonLdTitle(item: JsonObject): String? {
        return item.stringOrNull("title")
            ?: item.stringOrNull("headline")
            ?: item.stringOrNull("name")
    }

    private fun extractJsonLdAuthor(item: JsonObject): String? {
        val author = item["author"] ?: return null

        return when (author) {
            is JsonPrimitive -> author.content.takeIf { it.isNotBlank() }

            is JsonObject -> {
                author.stringOrNull("name")
                    ?: author.stringOrNull("givenName")?.let { given ->
                        val family = author.stringOrNull("familyName")
                        if (family != null) "$given $family" else given
                    }
            }

            is JsonArray -> {
                // Take the first author entry
                val first = author.firstOrNull() ?: return null
                when (first) {
                    is JsonPrimitive -> first.content.takeIf { it.isNotBlank() }
                    is JsonObject -> first.stringOrNull("name")
                        ?: first.stringOrNull("givenName")?.let { given ->
                            val family = first.stringOrNull("familyName")
                            if (family != null) "$given $family" else given
                        }
                    else -> null
                }
            }

            else -> null
        }
    }

    private fun extractJsonLdExcerpt(item: JsonObject): String? {
        return item.stringOrNull("description")
            ?: item.stringOrNull("abstract")
    }

    private fun extractJsonLdSiteName(item: JsonObject): String? {
        val publisher = item["publisher"]
        return when (publisher) {
            is JsonObject -> publisher.stringOrNull("name")
            is JsonPrimitive -> publisher.content.takeIf { it.isNotBlank() }
            else -> null
        }
    }

    private fun extractJsonLdPublishedTime(item: JsonObject): String? {
        return item.stringOrNull("datePublished")
            ?: item.stringOrNull("dateModified")
            ?: item.stringOrNull("dateCreated")
    }

    private fun extractJsonLdLang(item: JsonObject): String? {
        return item.stringOrNull("inLanguage")
    }

    private fun extractJsonLdDir(item: JsonObject): String? {
        return item.stringOrNull("textDirection")
    }

    // ─────────────────────────────────────────────────────────────
    // Private helpers: meta tag extraction & HTML unescaping
    // ─────────────────────────────────────────────────────────────

    /**
     * Get the `content` attribute of a `<meta>` tag matching the given
     * attribute name and value on the element.
     *
     * Example: `getMetaContent(doc, "property", "og:title")` returns the
     * value of `<meta property="og:title" content="My Title">`.
     */
    private fun getMetaContent(doc: Document, attrName: String, attrValue: String): String? {
        val meta = doc.select("meta[$attrName=\"$attrValue\"]").first() ?: return null
        return meta.attr("content").takeIf { it.isNotBlank() }
    }

    /**
     * Unescape common HTML entities in a string.
     *
     * Handles:
     * - Named entities: &amp; &lt; &gt; &quot; &apos; &nbsp;
     *   &mdash; &ndash; &hellip; &lsquo; &rsquo; &ldquo; &rdquo;
     *   &laquo; &raquo; &copy; &reg; &trade; &deg; &plusmn;
     *   &times; &divide; &frac12; &frac14; &frac34; &middot;
     *   &bull; &prime; &Prime; &euro; &pound; &yen; &cent;
     *   &sect; &micro; &para; &brvbar;
     * - Decimal numeric entities: `&#123;`
     * - Hex numeric entities: `&#x7B;`
     */
    private fun unescapeHtml(input: String): String {
        // Fast path: no entities
        if (!input.contains('&')) return input

        val buffer = StringBuilder(input.length)
        var i = 0
        while (i < input.length) {
            val ch = input[i]
            if (ch == '&' && i + 1 < input.length) {
                val semicolon = input.indexOf(';', i + 1)
                if (semicolon != -1) {
                    val entity = input.substring(i + 1, semicolon)
                    val resolved = when {
                        entity.startsWith("#x") || entity.startsWith("#X") -> {
                            val hex = entity.substring(2)
                            hex.toIntOrNull(16)?.toChar()
                        }
                        entity.startsWith("#") -> {
                            val dec = entity.substring(1)
                            dec.toIntOrNull()?.toChar()
                        }
                        entity in HTML_ENTITY_MAP -> HTML_ENTITY_MAP[entity]
                        else -> null
                    }

                    if (resolved != null) {
                        buffer.append(resolved)
                        i = semicolon + 1
                        continue
                    }
                }
            }
            buffer.append(ch)
            i++
        }

        return buffer.toString()
    }

    /**
     * Extended HTML entity map.  Complement to [TagSets.HTML_ESCAPE_MAP].
     */
    private val HTML_ENTITY_MAP: Map<String, String> = mapOf(
        // From TagSets — these are the reverse direction, added for completeness
        "lt" to "<",
        "gt" to ">",
        "amp" to "&",
        "quot" to "\"",
        "apos" to "'",

        // Whitespace / punctuation
        "nbsp" to "\u00A0",
        "ensp" to "\u2002",
        "emsp" to "\u2003",
        "thinsp" to "\u2009",
        "zwnj" to "\u200C",
        "zwj" to "\u200D",
        "lrm" to "\u200E",
        "rlm" to "\u200F",
        "shy" to "\u00AD",

        // Dashes
        "ndash" to "\u2013",
        "mdash" to "\u2014",
        "horbar" to "\u2015",

        // Quotes
        "lsquo" to "\u2018",
        "rsquo" to "\u2019",
        "sbquo" to "\u201A",
        "ldquo" to "\u201C",
        "rdquo" to "\u201D",
        "bdquo" to "\u201E",
        "laquo" to "\u00AB",
        "raquo" to "\u00BB",

        // Ellipsis
        "hellip" to "\u2026",

        // Symbols
        "copy" to "\u00A9",
        "reg" to "\u00AE",
        "trade" to "\u2122",
        "deg" to "\u00B0",
        "plusmn" to "\u00B1",
        "times" to "\u00D7",
        "divide" to "\u00F7",
        "frac12" to "\u00BD",
        "frac14" to "\u00BC",
        "frac34" to "\u00BE",
        "middot" to "\u00B7",
        "bull" to "\u2022",
        "prime" to "\u2032",
        "Prime" to "\u2033",
        "euro" to "\u20AC",
        "pound" to "\u00A3",
        "yen" to "\u00A5",
        "cent" to "\u00A2",
        "sect" to "\u00A7",
        "micro" to "\u00B5",
        "para" to "\u00B6",
        "brvbar" to "\u00A6",
    )

    // ─────────────────────────────────────────────────────────────
    // JsonObject convenience extension
    // ─────────────────────────────────────────────────────────────

    /** Get a string value by key, returning null if missing, null, or blank. */
    private fun JsonObject.stringOrNull(key: String): String? {
        val value = this[key] ?: return null
        return value.jsonPrimitive.content.takeIf { it.isNotBlank() }
    }
}
