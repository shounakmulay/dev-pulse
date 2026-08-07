package dev.shounakmulay.devpulse.readability.pipeline

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import dev.shounakmulay.devpulse.readability.dom.DomUtils
import dev.shounakmulay.devpulse.readability.dom.NodeTraversal
import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns

/**
 * Post-processing steps after article extraction.
 * Mirrors Readability._postProcessContent, _fixRelativeUris, _simplifyNestedElements, _cleanClasses.
 */
object PostProcessor {

    /**
     * Run all post-processing on the extracted article content.
     */
    fun process(articleContent: Element, doc: Document, keepClasses: Boolean, classesToPreserve: List<String>) {
        fixRelativeUris(articleContent, doc)
        simplifyNestedElements(articleContent)
        if (!keepClasses) {
            cleanClasses(articleContent, classesToPreserve)
        }
    }

    /**
     * Convert relative URIs to absolute URIs in <a>, <img>, <video>, etc.
     * Removes javascript: links (converting to spans or text).
     */
    fun fixRelativeUris(articleContent: Element, doc: Document) {
        val baseUri = doc.baseUri()

        fun toAbsoluteUri(uri: String): String {
            if (uri.isEmpty()) return uri
            if (baseUri == doc.location() && uri.startsWith("#")) return uri
            return resolveUri(baseUri, uri)
        }

        // Fix <a> hrefs
        val links = DomUtils.getAllNodesWithTag(articleContent, listOf("a"))
        for (link in links) {
            val href = link.attr("href")
            if (href.isNotEmpty()) {
                if (href.startsWith("javascript:")) {
                    // Replace javascript: links
                    if (link.childNodes().size == 1 &&
                        link.childNodes()[0].nodeName() == "#text"
                    ) {
                        val text = link.text()
                        link.replaceWith(TextNode(text))
                    } else {
                        val container = doc.createElement("span")
                        val children = ArrayList(link.childNodes())
                        for (child in children) {
                            child.remove()
                            container.appendChild(child)
                        }
                        link.replaceWith(container)
                    }
                } else {
                    link.attr("href", toAbsoluteUri(href))
                }
            }
        }

        // Fix <img>, <picture>, <figure>, <video>, <audio>, <source>
        val medias = DomUtils.getAllNodesWithTag(articleContent,
            listOf("img", "picture", "figure", "video", "audio", "source"))
        for (media in medias) {
            val src = media.attr("src")
            val poster = media.attr("poster")
            val srcset = media.attr("srcset")

            if (src.isNotEmpty()) media.attr("src", toAbsoluteUri(src))
            if (poster.isNotEmpty()) media.attr("poster", toAbsoluteUri(poster))
            if (srcset.isNotEmpty()) {
                val newSrcset = RegexPatterns.srcsetUrl.replace(srcset) { match ->
                    val groups = match.groupValues
                    toAbsoluteUri(groups[1]) + (if (groups.size > 2) groups[2] else "") +
                            (if (groups.size > 3) groups[3] else "")
                }
                media.attr("srcset", newSrcset)
            }
        }
    }

    /**
     * Remove DIV and SECTION wrappers that contain only a single DIV/SECTION.
     * Remove empty DIVs and SECTIONs.
     */
    fun simplifyNestedElements(articleContent: Element) {
        var node: Element? = articleContent
        while (node != null) {
            val tag = node.tagName().uppercase()
            if (node.parent() != null &&
                (tag == "DIV" || tag == "SECTION") &&
                !(node.id().isNotEmpty() && node.id().startsWith("readability"))
            ) {
                if (ArticleGrabber.isElementWithoutContent(node)) {
                    node = DomUtils.removeAndGetNext(node, NodeTraversal::getNextNode)
                    continue
                } else if (ArticleGrabber.hasSingleTagInsideElement(node, "DIV") ||
                    ArticleGrabber.hasSingleTagInsideElement(node, "SECTION")
                ) {
                    val child = node.child(0) as Element
                    // Copy attributes from parent to child
                    for (attr in node.attributes()) {
                        child.attr(attr.key, attr.value)
                    }
                    node.replaceWith(child)
                    node = child
                    continue
                }
            }
            node = NodeTraversal.getNextNode(node)
        }
    }

    /**
     * Remove class attributes from all elements except those in preserve list.
     */
    fun cleanClasses(node: Element, classesToPreserve: List<String>) {
        val className = node.className()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() && it in classesToPreserve }
            .joinToString(" ")

        if (className.isNotEmpty()) {
            node.attr("class", className)
        } else {
            node.removeAttr("class")
        }

        var child = node.firstElementChild()
        while (child != null) {
            cleanClasses(child, classesToPreserve)
            child = child.nextElementSibling()
        }
    }

    /**
     * KMP-compatible URI resolution with ./ and ../ normalization.
     * Resolves a relative URI against a base URI.
     */
    private fun resolveUri(baseUri: String, relativeUri: String): String {
        if (relativeUri.startsWith("http://") || relativeUri.startsWith("https://") ||
            relativeUri.startsWith("//") || relativeUri.startsWith("data:") ||
            relativeUri.startsWith("mailto:") || relativeUri.startsWith("tel:") ||
            relativeUri.startsWith("javascript:") || baseUri.isEmpty()
        ) {
            return relativeUri
        }

        // Build the full URI
        val resolved = if (relativeUri.startsWith("/")) {
            // Absolute path: resolve against domain root
            val slashIndex = baseUri.indexOf('/', 8) // after https://
            if (slashIndex < 0) "$baseUri$relativeUri"
            else baseUri.substring(0, slashIndex) + relativeUri
        } else {
            // Relative path: resolve against base path
            val lastSlash = baseUri.lastIndexOf('/')
            if (lastSlash < 8) "$baseUri/$relativeUri"
            else baseUri.substring(0, lastSlash + 1) + relativeUri
        }

        // Normalize ./ and ../ segments
        return normalizePath(resolved)
    }

    /**
     * Remove ./ and resolve ../ segments from a URI path.
     */
    private fun normalizePath(uri: String): String {
        // Split into scheme+authority and path
        val schemeEnd = uri.indexOf("://")
        if (schemeEnd < 0) return uri

        val pathStart = uri.indexOf('/', schemeEnd + 3)
        if (pathStart < 0) return uri // No path to normalize

        val prefix = uri.substring(0, pathStart)
        val path = uri.substring(pathStart)

        val segments = path.split('/')
        val result = mutableListOf<String>()

        for (seg in segments) {
            when (seg) {
                "", "." -> { /* skip empty segments and current-dir */ }
                ".." -> if (result.isNotEmpty()) result.removeAt(result.size - 1)
                else -> result.add(seg)
            }
        }

        return "$prefix/${result.joinToString("/")}"
    }
}