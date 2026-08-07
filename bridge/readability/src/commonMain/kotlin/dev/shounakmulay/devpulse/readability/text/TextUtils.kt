package dev.shounakmulay.devpulse.readability.text

import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns
import dev.shounakmulay.devpulse.readability.heuristics.TagSets
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode

/**
 * Text extraction utilities that mirror Readability.js text functions.
 */
object TextExtractor {

    /**
     * Get the inner text of an element, optionally normalizing whitespace.
     * Mirrors _getInnerText.
     *
     * @param normalizeSpaces If true, collapse multiple whitespace to single space.
     */
    fun getInnerText(element: Element, normalizeSpaces: Boolean = true): String {
        val text = element.text()
        return if (normalizeSpaces) {
            text.replace(RegexPatterns.normalize, " ").trim()
        } else {
            text.trim()
        }
    }

    /**
     * Get character count including approximate spaces between block elements.
     * Mirrors _getCharCount.
     */
    fun getCharCount(element: Element, separator: String = ","): Int {
        var count = 0
        // Walk child nodes and count text
        element.traverse { node, depth ->
            if (node is TextNode) {
                count += node.text().length
            }
        }
        // Add separators between block children
        val blockChildren = element.children().count { child ->
            val tag = child.tagName().uppercase()
            tag !in TagSets.PHRASING_ELEMS
        }
        count += maxOf(0, (blockChildren - 1) * separator.length)
        return count
    }

    /**
     * Split text into words.
     */
    fun wordCount(text: String): Int {
        return text.split(Regex("\\s+")).count { it.isNotEmpty() }
    }
}

/**
 * Text similarity computation.
 * Mirrors _textSimilarity.
 */
object TextSimilarity {

    /**
     * Compare two texts and return a similarity score from 0.0 to 1.0.
     * 1.0 = identical word sets, 0.0 = completely different.
     *
     * Algorithm: tokenize both, find words unique to textB,
     * compute distance as ratio of unique-word-length to total-word-length,
     * return 1 - distance.
     */
    fun compare(textA: String, textB: String): Double {
        val tokensA = textA.lowercase()
            .split(RegexPatterns.tokenize)
            .filter { it.isNotEmpty() }
        val tokensB = textB.lowercase()
            .split(RegexPatterns.tokenize)
            .filter { it.isNotEmpty() }

        if (tokensA.isEmpty() || tokensB.isEmpty()) return 0.0

        val uniqTokensB = tokensB.filter { it !in tokensA }
        val distanceB = uniqTokensB.joinToString(" ").length.toDouble() /
            tokensB.joinToString(" ").length
        return 1.0 - distanceB
    }
}
