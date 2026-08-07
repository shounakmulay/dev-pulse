package dev.shounakmulay.devpulse.readability.extraction

import dev.shounakmulay.devpulse.readability.dom.DomUtils
import com.fleeksoft.ksoup.nodes.Document
import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns

/**
 * Title extraction algorithm mirroring Readability.prototype._getArticleTitle.
 *
 * Strategy (in order):
 * 1. Start with document.title
 * 2. If title has separator (|, -, \, /, >, »), remove the last segment
 * 3. If the result is too short (< 3 words), remove the first segment instead
 * 4. If title has ": ", check if any H1/H2 matches the full title;
 *    if not, extract the part after the last colon
 * 5. If title is unusually long (>150 chars) or short (<15 chars),
 *    try using the H1 content
 * 6. Fall back to original title if result is ≤ 4 words
 */
object TitleExtractor {

    /**
     * Extract the article title from a document.
     */
    fun extract(doc: Document): String {
        var curTitle = ""
        var origTitle = ""

        try {
            curTitle = doc.title().trim()
            origTitle = curTitle
        } catch (_: Exception) {
            // Try getting from <title> element directly
            val titleElements = doc.getElementsByTag("title")
            if (titleElements.isNotEmpty()) {
                curTitle = titleElements[0].text().trim()
                origTitle = curTitle
            }
        }

        if (curTitle.isEmpty()) return ""

        val titleHadHierarchicalSeparators =
            RegexPatterns.hierarchicalSeparator.containsMatchIn(curTitle)

        // Strategy 1: Title has a separator like "Site Name | Article Title"
        if (RegexPatterns.titleSeparator.containsMatchIn(curTitle)) {
            // Find all separator positions
            val separatorMatches = RegexPatterns.titleSeparator.findAll(origTitle).toList()
            if (separatorMatches.isNotEmpty()) {
                val lastSep = separatorMatches.last()
                curTitle = origTitle.substring(0, lastSep.range.first)

                // If too short, try removing the first part
                if (wordCount(curTitle) < 3) {
                    val firstSep = separatorMatches.first()
                    curTitle = origTitle.substring(firstSep.range.last + 1).trim()
                }
            }
        }
        // Strategy 2: Title has colon
        else if (curTitle.contains(": ")) {
            val headings = DomUtils.getAllNodesWithTag(doc, listOf("h1", "h2"))
            val trimmedTitle = curTitle.trim()
            val hasHeadingMatch = DomUtils.someNode(headings) { heading ->
                heading.text().trim() == trimmedTitle
            }

            if (!hasHeadingMatch) {
                curTitle = origTitle.substring(origTitle.lastIndexOf(":") + 1)

                if (wordCount(curTitle) < 3) {
                    curTitle = origTitle.substring(origTitle.indexOf(":") + 1)
                } else if (wordCount(origTitle.substring(0, origTitle.indexOf(":"))) > 5) {
                    // Too many words before colon — keep original
                    curTitle = origTitle
                }
            }
        }
        // Strategy 3: Title is unusually long or short
        else if (curTitle.length > 150 || curTitle.length < 15) {
            val hOnes = doc.getElementsByTag("h1")
            if (hOnes.size == 1) {
                curTitle = hOnes[0].text().trim()
            }
        }

        // Normalize whitespace
        curTitle = curTitle.trim().replace(RegexPatterns.normalize, " ")

        // Fallback: if result is ≤ 4 words and no hierarchical separators,
        // or word count decreased by more than 1, use original
        val curTitleWordCount = wordCount(curTitle)
        if (curTitleWordCount <= 4 &&
            (!titleHadHierarchicalSeparators ||
                curTitleWordCount != wordCount(
                    origTitle.replace(Regex("[|\\-\\\\/>»]+"), "")
                ) - 1)
        ) {
            curTitle = origTitle
        }

        return curTitle
    }

    private fun wordCount(str: String): Int {
        return str.split(Regex("\\s+")).count { it.isNotEmpty() }
    }
}
