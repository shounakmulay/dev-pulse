package dev.shounakmulay.devpulse.readability.scoring

import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns
import dev.shounakmulay.devpulse.readability.heuristics.ScoringConstants
import com.fleeksoft.ksoup.nodes.Element

/**
 * Calculates link density for an element.
 *
 * linkDensity = total link text length / total text length
 *
 * A high link density (close to 1.0) suggests the element is a list of links
 * (navigation, sidebar, etc.) rather than article content.
 *
 * Mirrors Readability.prototype._getLinkDensity.
 */
object LinkDensityCalculator {

    /**
     * Calculate link density for an element.
     *
     * @return A value from 0.0 (no links) to 1.0 (entirely links).
     */
    fun calculate(element: Element): Double {
        val textLength = element.text().length
        if (textLength == 0) return 0.0

        val linkTextLength = element.getElementsByTag("a")
            .sumOf { it.text().length }

        return linkTextLength.toDouble() / textLength
    }
}

/**
 * Calculates class name weight for scoring.
 *
 * Positive patterns (article, body, content, post, etc.) add weight.
 * Negative patterns (comment, sidebar, footer, ad, etc.) subtract weight.
 *
 * Mirrors Readability.prototype._getClassWeight.
 */
object ClassWeightCalculator {

    /**
     * Calculate class weight based on className and id.
     *
     * @return Positive for likely-content elements, negative for unlikely ones.
     */
    fun calculate(element: Element): Int {
        var weight = 0
        val className = element.className()
        val id = element.id()

        val matchString = "$className $id"

        if (RegexPatterns.positive.containsMatchIn(matchString)) {
            weight += ScoringConstants.CLASS_WEIGHT_BONUS
        }
        if (RegexPatterns.negative.containsMatchIn(matchString)) {
            weight -= ScoringConstants.CLASS_WEIGHT_BONUS
        }

        return weight
    }
}

/**
 * Calculates text density for element cleaning decisions.
 *
 * Mirrors _getTextDensity.
 */
object TextDensityCalculator {

    /**
     * Calculate text density: characters of text per tag count.
     *
     * @param tags Tag names to count for density calculation.
     */
    fun calculate(element: Element, tags: List<String>): Double {
        val textLength = element.text().length
        if (textLength == 0) return 0.0

        val tagCount = tags.sumOf { tag ->
            element.getElementsByTag(tag).size
        }
        if (tagCount == 0) return 0.0

        return textLength.toDouble() / tagCount
    }
}
