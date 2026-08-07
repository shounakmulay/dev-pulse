package dev.shounakmulay.devpulse.readability.scoring

import dev.shounakmulay.devpulse.readability.dom.DomUtils
import dev.shounakmulay.devpulse.readability.dom.NodeTraversal
import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns
import dev.shounakmulay.devpulse.readability.heuristics.ScoringConstants
import dev.shounakmulay.devpulse.readability.scoring.CandidateScorer.scoreElements
import dev.shounakmulay.devpulse.readability.scoring.CandidateScorer.selectTopCandidates
import com.fleeksoft.ksoup.nodes.Element
import dev.shounakmulay.devpulse.readability.text.TextExtractor

/**
 * Scores paragraph-level elements and propagates scores to ancestor candidates.
 *
 * This is the core scoring engine of Readability.js. It mirrors the scoring loop
 * and top-candidate selection in Readability.prototype._grabArticle.
 *
 * ## Algorithm Overview
 * 1. [scoreElements] iterates over paragraph-like elements (p, td, pre, h2–h6, section),
 *    computes a per-paragraph content score, and propagates that score up to ancestors
 *    with a distance penalty. Ancestors that newly receive a readability state are
 *    collected as "candidates."
 * 2. After all paragraphs are scored, a link-density penalty is applied to each
 *    candidate (high link density suggests navigation, not article content).
 * 3. Candidates are sorted by score descending.
 * 4. [selectTopCandidates] picks the top N and applies alternative-candidate ancestor
 *    merging: if several high-scoring candidates share a common ancestor, that ancestor
 *    is promoted as the best candidate.
 */
object CandidateScorer {

    // ---- Scoring entry point ----

    /**
     * Score a list of paragraph-level elements (typically those matching
     * [ScoringConstants.DEFAULT_TAGS_TO_SCORE]) and return scored candidate
     * ancestor elements, sorted by score descending.
     *
     * ### Readability.js origin
     * This implements the scoring loop inside `_grabArticle`:
     * ```
     * this._forEachNode(elementsToScore, function(elementToScore) { ... });
     * ```
     * followed by the link-density penalty pass and sort.
     *
     * @param elements The paragraph-level elements to score (e.g. <p>, <td>, <pre>, <h2>–<h6>, <section>).
     * @return Candidate ancestor elements with initialized readability state, sorted by score descending.
     */
    fun scoreElements(elements: List<Element>): List<Element> {
        val candidates = mutableListOf<Element>()

        for (element in elements) {
            scoreSingleParagraph(element, candidates)
        }

        // ---- Link-density penalty (after all scoring) ----
        // Good content should have a relatively small link density (5% or less).
        // Mirrors the post-scoring loop in _grabArticle that multiplies each
        // candidate's score by (1 - linkDensity).
        for (candidate in candidates) {
            val state = DomUtils.getReadabilityState(candidate) ?: continue
            val linkDensity = LinkDensityCalculator.calculate(candidate)
            state.contentScore *= (1.0 - linkDensity)
        }

        // Sort by score descending — highest score first
        candidates.sortByDescending { DomUtils.getReadabilityState(it)?.contentScore ?: 0.0 }

        return candidates
    }

    /**
     * Select the top N candidates from a scored-and-sorted list, with
     * alternative-candidate ancestor merging.
     *
     * ### Readability.js origin
     * Mirrors the top-candidate selection in `_grabArticle`, including the
     * "alternative candidate ancestor" merging logic that walks up the DOM
     * looking for a common ancestor when several runners-up have scores
     * >= 75% of the top candidate.
     *
     * @param candidates Previously scored and sorted candidates (output of [scoreElements]).
     * @param nbTopCandidates Maximum number of candidates to return (default: [ScoringConstants.DEFAULT_N_TOP_CANDIDATES]).
     * @return Up to [nbTopCandidates] top candidates, with the best candidate first.
     */
    fun selectTopCandidates(
        candidates: List<Element>,
        nbTopCandidates: Int = ScoringConstants.DEFAULT_N_TOP_CANDIDATES
    ): List<Element> {
        if (candidates.isEmpty()) return emptyList()

        val topCandidates = candidates.take(nbTopCandidates).toMutableList()
        val topCandidate = topCandidates.first()
        val topScore = DomUtils.getReadabilityState(topCandidate)?.contentScore ?: 0.0

        // ---- Alternative candidate ancestor merging ----
        // Collect ancestor chains of runners-up whose score is >= 75% of the top score.
        val alternativeAncestors = mutableListOf<List<Element>>()
        for (i in 1 until topCandidates.size) {
            val score = DomUtils.getReadabilityState(topCandidates[i])?.contentScore ?: 0.0
            if (topScore > 0 && score / topScore >= ScoringConstants.ALTERNATIVE_CANDIDATE_SCORE_RATIO) {
                // getNodeAncestors with no maxDepth walks all the way to <html>
                alternativeAncestors.add(NodeTraversal.getNodeAncestors(topCandidates[i]))
            }
        }

        // If at least 3 alternative candidates exist, try to find a common ancestor
        // that contains at least 3 of them; if found, promote it as the new #1.
        if (alternativeAncestors.size >= ScoringConstants.MINIMUM_TOP_CANDIDATES) {
            var parentOfTop: Element? = topCandidate.parent() as? Element

            while (parentOfTop != null && !parentOfTop.tagName().equals("BODY", ignoreCase = true)) {
                var listsContainingThisAncestor = 0

                for (ancestorList in alternativeAncestors) {
                    if (listsContainingThisAncestor >= ScoringConstants.MINIMUM_TOP_CANDIDATES) break
                    if (ancestorList.contains(parentOfTop)) {
                        listsContainingThisAncestor++
                    }
                }

                if (listsContainingThisAncestor >= ScoringConstants.MINIMUM_TOP_CANDIDATES) {
                    // Ensure the promoted ancestor has readability state
                    ensureNodeInitialized(parentOfTop)
                    // Insert as new #1; trim to nbTopCandidates
                    topCandidates.add(0, parentOfTop)
                    if (topCandidates.size > nbTopCandidates) {
                        topCandidates.removeAt(topCandidates.lastIndex)
                    }
                    break
                }

                parentOfTop = parentOfTop.parent() as? Element
            }
        }

        return topCandidates
    }

    // ---- Private helpers ----

    /**
     * Score a single paragraph-level element and propagate the score to its
     * ancestors, collecting newly-initialized ancestors as candidates.
     *
     * ### Readability.js origin
     * Inner body of the `_forEachNode(elementsToScore, ...)` callback.
     */
    private fun scoreSingleParagraph(element: Element, candidates: MutableList<Element>) {
        // Skip elements whose parent is missing or has no tagName
        val parent = element.parent() as? Element ?: return
        if (parent.tagName().isEmpty()) return

        // Get inner text; skip if too short (< 25 chars)
        // Mirrors: if (innerText.length < 25) return;
        val innerText = TextExtractor.getInnerText(element)
        if (innerText.length < ScoringConstants.MIN_PARAGRAPH_LENGTH) return

        // Collect ancestors (up to 5 levels). Skip if none.
        // Mirrors: var ancestors = this._getNodeAncestors(elementToScore, 5);
        val ancestors = NodeTraversal.getNodeAncestors(element, ScoringConstants.MAX_ANCESTOR_DEPTH)
        if (ancestors.isEmpty()) return

        // ---- Paragraph content score ----
        // Base: 1 point for being a paragraph.
        // Commas: +1 per comma (commas correlate with substantial content).
        // Length: +1 per 100 chars, capped at 3.
        // Mirrors:
        //   contentScore += 1;
        //   contentScore += innerText.split(REGEXPS.commas).length;
        //   contentScore += Math.min(Math.floor(innerText.length / 100), 3);
        var contentScore = ScoringConstants.PARAGRAPH_BASE_SCORE.toDouble()
        // Mirrors: innerText.split(this.REGEXPS.commas).length
        // Split returns commaCount + 1 segments (each comma adds 1 point)
        contentScore += innerText.split(RegexPatterns.commas).size.toDouble()
        contentScore += minOf(
            (innerText.length / ScoringConstants.LENGTH_SCORE_INTERVAL).toDouble(),
            ScoringConstants.MAX_LENGTH_SCORE.toDouble()
        )

        // ---- Propagate to ancestors ----
        // Ancestor level: 0 = parent, 1 = grandparent, 2+ = great-grandparent+
        // Score dividers:
        //   level 0 → 1 (full score to parent)
        //   level 1 → 2 (half to grandparent)
        //   level 2+ → level * 3 (diminishing returns)
        for ((level, ancestor) in ancestors.withIndex()) {
            // Skip ancestors with no tagName or no parent (safety guard)
            if (ancestor.tagName().isEmpty()) continue
            val ancestorParent = ancestor.parent() as? Element ?: continue
            if (ancestorParent.tagName().isEmpty()) continue

            val wasNew = ensureNodeInitialized(ancestor)
            if (wasNew) {
                candidates.add(ancestor)
            }

            val scoreDivider = when (level) {
                0 -> ScoringConstants.PARENT_SCORE_DIVIDER
                1 -> ScoringConstants.GRANDPARENT_SCORE_DIVIDER
                else -> (level * ScoringConstants.GREAT_GRANDPARENT_BASE_DIVIDER).toDouble()
            }

            DomUtils.ensureReadabilityState(ancestor).contentScore += contentScore / scoreDivider
        }
    }

    /**
     * Ensure an element has an initialized readability state.
     * If the state is newly created, apply tag-based base scores and class weight.
     *
     * ### Readability.js origin
     * Mirrors `_initializeNode` — assigns a base score based on tag name
     * (DIV: +5, PRE/TD/BLOCKQUOTE: +3, ADDRESS/OL/UL/form: -3, H1-H6/TH: -5)
     * plus class-weight bonus/penalty from [ClassWeightCalculator].
     *
     * @return `true` if the state was newly created, `false` if it already existed.
     */
    private fun ensureNodeInitialized(element: Element): Boolean {
        val existing = DomUtils.getReadabilityState(element)
        if (existing != null) return false

        val state = DomUtils.ensureReadabilityState(element)

        // Tag-based initialization scores
        // Mirrors switch statement in _initializeNode
        when (element.tagName().uppercase()) {
            "DIV" -> state.contentScore += ScoringConstants.DIV_SCORE.toDouble()
            "PRE", "TD", "BLOCKQUOTE" -> state.contentScore += ScoringConstants.PRE_TD_BLOCKQUOTE_SCORE.toDouble()
            "ADDRESS", "OL", "UL", "DL", "DD", "DT", "LI", "FORM" ->
                state.contentScore += ScoringConstants.ADDRESS_LIST_FORM_SCORE.toDouble()
            "H1", "H2", "H3", "H4", "H5", "H6", "TH" ->
                state.contentScore += ScoringConstants.HEADING_TH_SCORE.toDouble()
        }

        // Class weight (positive/negative bonus based on className and id)
        // Mirrors: node.readability.contentScore += this._getClassWeight(node);
        state.contentScore += ClassWeightCalculator.calculate(element).toDouble()

        return true
    }
}
