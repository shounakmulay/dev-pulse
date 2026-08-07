package dev.shounakmulay.devpulse.readability.pipeline

import dev.shounakmulay.devpulse.readability.cleaning.ContentCleaner
import dev.shounakmulay.devpulse.readability.dom.DomUtils
import dev.shounakmulay.devpulse.readability.dom.NodeTraversal
import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns
import dev.shounakmulay.devpulse.readability.heuristics.ScoringConstants
import dev.shounakmulay.devpulse.readability.heuristics.TagSets
import dev.shounakmulay.devpulse.readability.scoring.ClassWeightCalculator
import dev.shounakmulay.devpulse.readability.scoring.LinkDensityCalculator
import dev.shounakmulay.devpulse.readability.text.TextExtractor
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode

/**
 * Core article extraction algorithm — mirrors Readability.prototype._grabArticle.
 *
 * This is the heart of Readability. It:
 * 1. Walks the DOM tree, removing unlikely candidates and scoring elements
 * 2. Propagates scores to ancestor containers
 * 3. Selects the best candidate container
 * 4. Merges sibling content
 * 5. Cleans the candidate content (prepArticle) and checks against the char threshold
 * 6. Retries with relaxed flags if insufficient content found after cleaning
 */
object ArticleGrabber {

    /** Internal flags matching Readability flag system */
    private const val FLAG_STRIP_UNLIKELYS = 0x1
    private const val FLAG_WEIGHT_CLASSES = 0x2
    private const val FLAG_CLEAN_CONDITIONALLY = 0x4

    data class GrabResult(
        val articleContent: Element,
        val topCandidate: Element?,
        val parentOfTopCandidate: Element?,
        val neededToCreateTopCandidate: Boolean = false,
    )

    data class Candidate(
        val element: Element,
        val score: Double,
    )

    data class Attempt(
        val articleContent: Element,
        val textLength: Int,
    )

    /**
     * Main entry point — extract article content from a page.
     *
     * @param page The body element or page container to extract from
     * @param doc The full document (for creating elements)
     * @param charThreshold Minimum character threshold
     * @param nbTopCandidates Number of top candidates to consider
     * @param linkDensityModifier Link density threshold modifier
     */
    fun grab(
        page: Element,
        doc: Document,
        charThreshold: Int = ScoringConstants.DEFAULT_CHAR_THRESHOLD,
        nbTopCandidates: Int = ScoringConstants.DEFAULT_N_TOP_CANDIDATES,
        linkDensityModifier: Double = 0.0,
    ): Element? {
        val pageCacheHtml = page.html()
        var flags = FLAG_STRIP_UNLIKELYS or FLAG_WEIGHT_CLASSES or FLAG_CLEAN_CONDITIONALLY
        val attempts = mutableListOf<Attempt>()

        while (true) {
            val stripUnlikelyCandidates = (flags and FLAG_STRIP_UNLIKELYS) != 0
            val weightClasses = (flags and FLAG_WEIGHT_CLASSES) != 0
            val cleanConditionally = (flags and FLAG_CLEAN_CONDITIONALLY) != 0

            // ----- Phase 1: Walk DOM, score elements -----
            val elementsToScore = mutableListOf<Element>()
            var node: Element? = doc as? Element

            var shouldRemoveTitleHeader = true

            while (node != null) {
                when (node.tagName().uppercase()) {
                    "HTML" -> {
                        // Capture lang attribute
                        // (stored externally by caller)
                    }
                }

                val matchString = "${node.className()} ${node.id()}"

                // Remove hidden nodes
                if (!DomUtils.isProbablyVisible(node)) {
                    node = DomUtils.removeAndGetNext(node, NodeTraversal::getNextNode)
                    continue
                }

                // Remove aria-modal dialogs
                if (node.attr("aria-modal") == "true" && node.attr("role") == "dialog") {
                    node = DomUtils.removeAndGetNext(node, NodeTraversal::getNextNode)
                    continue
                }

                // Remove unlikely candidates
                if (stripUnlikelyCandidates) {
                    if (RegexPatterns.unlikelyCandidates.containsMatchIn(matchString) &&
                        !RegexPatterns.okMaybeItsACandidate.containsMatchIn(matchString) &&
                        !NodeTraversal.hasAncestorTag(node, "table") &&
                        !NodeTraversal.hasAncestorTag(node, "code") &&
                        node.tagName().uppercase() != "BODY" &&
                        node.tagName().uppercase() != "A"
                    ) {
                        node = DomUtils.removeAndGetNext(node, NodeTraversal::getNextNode)
                        continue
                    }

                    val role = node.attr("role")
                    if (role.isNotEmpty() && TagSets.UNLIKELY_ROLES.contains(role)) {
                        node = DomUtils.removeAndGetNext(node, NodeTraversal::getNextNode)
                        continue
                    }
                }

                // Remove empty DIV, SECTION, HEADER, H1-H6
                val tag = node.tagName().uppercase()
                if (tag in listOf("DIV", "SECTION", "HEADER", "H1", "H2", "H3", "H4", "H5", "H6") &&
                    isElementWithoutContent(node)
                ) {
                    node = DomUtils.removeAndGetNext(node, NodeTraversal::getNextNode)
                    continue
                }

                // Collect scorables
                if (tag in ScoringConstants.DEFAULT_TAGS_TO_SCORE) {
                    elementsToScore.add(node)
                }

                // Div → P conversion for phrasing content
                if (tag == "DIV") {
                    // Wrap phrasing content in paragraphs
                    var p: Element? = null
                    var childNode: Node? = node.firstChild()
                    while (childNode != null) {
                        val nextSibling = childNode.nextSibling()
                        if (isPhrasingContent(childNode)) {
                            if (p != null) {
                                p.appendChild(childNode)
                            } else if (!isWhitespace(childNode)) {
                                p = doc.createElement("p")
                                // Ksoup: replaceChild is private, use insert+remove workaround
                                val idx = childNode.siblingIndex()
                                childNode.remove()
                                p.appendChild(childNode)
                                node.insertChildren(idx, listOf(p))
                            }
                        } else if (p != null) {
                            // Trim trailing whitespace
                            while (p.lastChild() != null && isWhitespace(p.lastChild()!!)) {
                                p.lastChild()!!.remove()
                            }
                            p = null
                        }
                        childNode = nextSibling
                    }

                    // Sites wrap each paragraph in a DIV with just a P inside
                    if (hasSingleTagInsideElement(node, "P") &&
                        LinkDensityCalculator.calculate(node) < 0.25
                    ) {
                        val newNode = node.child(0) as Element
                        node.replaceWith(newNode)
                        node = newNode
                        elementsToScore.add(node)
                    } else if (!hasChildBlockElement(node)) {
                        node = DomUtils.setNodeTag(node, "P")
                        elementsToScore.add(node)
                    }
                }

                node = NodeTraversal.getNextNode(node)
            }

            // ----- Phase 2: Score paragraphs and propagate to ancestors -----
            val candidates = mutableListOf<Element>()
            for (elementToScore in elementsToScore) {
                val parent = elementToScore.parent() as? Element ?: continue
                if (parent.tagName().isEmpty()) continue

                val innerText = TextExtractor.getInnerText(elementToScore)
                if (innerText.length < ScoringConstants.MIN_PARAGRAPH_LENGTH) continue

                val ancestors = NodeTraversal.getNodeAncestors(elementToScore, ScoringConstants.MAX_ANCESTOR_DEPTH)
                if (ancestors.isEmpty()) continue

                // Compute raw score for this paragraph
                var contentScore = ScoringConstants.PARAGRAPH_BASE_SCORE.toDouble()
                contentScore += innerText.split(RegexPatterns.commas).size.toDouble()
                contentScore += minOf(
                    (innerText.length / ScoringConstants.LENGTH_SCORE_INTERVAL).toDouble(),
                    ScoringConstants.MAX_LENGTH_SCORE.toDouble()
                )

                // Propagate to ancestors
                for ((level, ancestor) in ancestors.withIndex()) {
                    if (ancestor.tagName().isEmpty()) continue
                    val ancestorParent = ancestor.parent()
                    if (ancestorParent == null || (ancestorParent as? Element)?.tagName()?.isEmpty() != false) continue

                    val state = DomUtils.ensureReadabilityState(ancestor)
                    if (state.contentScore == 0.0) {
                        // Initialize node: base tag score + class weight
                        initializeNode(ancestor, weightClasses)
                        candidates.add(ancestor)
                    }

                    val scoreDivider = when (level) {
                        0 -> ScoringConstants.PARENT_SCORE_DIVIDER
                        1 -> ScoringConstants.GRANDPARENT_SCORE_DIVIDER
                        else -> (level * ScoringConstants.GREAT_GRANDPARENT_BASE_DIVIDER).toDouble()
                    }
                    state.contentScore += contentScore / scoreDivider
                }
            }

            // ----- Phase 3: Apply link density penalty, select top candidates -----
            for (candidate in candidates) {
                val state = DomUtils.getReadabilityState(candidate) ?: continue
                val linkDensity = LinkDensityCalculator.calculate(candidate)
                state.contentScore = state.contentScore * (1.0 - linkDensity)
            }

            // Sort and keep top N
            candidates.sortByDescending { DomUtils.getReadabilityState(it)?.contentScore ?: 0.0 }
            val topCandidates = candidates.take(nbTopCandidates).toMutableList()

            // ----- Phase 4: Select best top candidate -----
            var topCandidate: Element? = topCandidates.firstOrNull()
            var neededToCreateTopCandidate = false

            if (topCandidate == null || topCandidate.tagName().uppercase() == "BODY") {
                // Create a container DIV and move all body children into it
                topCandidate = doc.createElement("div")
                neededToCreateTopCandidate = true
                val children = ArrayList(page.childNodes())
                for (child in children) {
                    child.remove()
                    topCandidate.appendChild(child)
                }
                page.appendChild(topCandidate)
                initializeNode(topCandidate, weightClasses)
            } else {
                // topCandidate is non-null in this branch
                val tc = topCandidate!!
                // Alternative candidate ancestor merging:
                // If >= 3 alternative candidates (score ≥ 75% of top) share a common ancestor,
                // use that ancestor instead
                val alternativeCandidateAncestors = mutableListOf<List<Element>>()
                for (i in 1 until topCandidates.size) {
                    val altScore = DomUtils.getReadabilityState(topCandidates[i])?.contentScore ?: 0.0
                    val topScore = DomUtils.getReadabilityState(topCandidate!!)?.contentScore ?: 1.0
                    if (altScore / topScore >= ScoringConstants.ALTERNATIVE_CANDIDATE_SCORE_RATIO) {
                        alternativeCandidateAncestors.add(
                            NodeTraversal.getNodeAncestors(topCandidates[i])
                        )
                    }
                }

                if (alternativeCandidateAncestors.size >= ScoringConstants.MINIMUM_TOP_CANDIDATES) {
                    var parentOfTop = topCandidate.parent() as? Element
                    while (parentOfTop != null && parentOfTop.tagName().uppercase() != "BODY") {
                        var listsContaining = 0
                        for (altAncestors in alternativeCandidateAncestors) {
                            if (altAncestors.contains(parentOfTop)) listsContaining++
                            if (listsContaining >= ScoringConstants.MINIMUM_TOP_CANDIDATES) break
                        }
                        if (listsContaining >= ScoringConstants.MINIMUM_TOP_CANDIDATES) {
                            topCandidate = parentOfTop
                            break
                        }
                        parentOfTop = parentOfTop.parent() as? Element
                    }
                }

                // Ensure initialized
                if (DomUtils.getReadabilityState(topCandidate!!) == null) {
                    initializeNode(topCandidate!!, weightClasses)
                }

                // Walk up the tree — if parent's score is higher, use parent
                var parentOfTop = topCandidate.parent() as? Element
                var lastScore = DomUtils.getReadabilityState(topCandidate)?.contentScore ?: 0.0
                val scoreThreshold = lastScore * ScoringConstants.UPWARD_SCORE_THRESHOLD_FACTOR

                while (parentOfTop != null && parentOfTop.tagName().uppercase() != "BODY") {
                    val parentState = DomUtils.getReadabilityState(parentOfTop)
                    if (parentState == null) {
                        parentOfTop = parentOfTop.parent() as? Element
                        continue
                    }
                    val parentScore = parentState.contentScore
                    if (parentScore < scoreThreshold) break
                    if (parentScore > lastScore) {
                        topCandidate = parentOfTop
                        break
                    }
                    lastScore = parentScore
                    parentOfTop = parentOfTop.parent() as? Element
                }

                // If top candidate is the only child, use parent (helps sibling joining)
                var parentOfCandidate = topCandidate!!.parent() as? Element
                while (parentOfCandidate != null &&
                    parentOfCandidate.tagName().uppercase() != "BODY" &&
                    parentOfCandidate.children()!!.size == 1
                ) {
                    topCandidate = parentOfCandidate
                    parentOfCandidate = topCandidate!!.parent() as? Element
                }

                if (DomUtils.getReadabilityState(topCandidate!!) == null) {
                    initializeNode(topCandidate!!, weightClasses)
                }
            }

            // ----- Phase 5: Merge sibling content -----
            val articleContent = doc.createElement("div")
            val parentOfTopCandidate = topCandidate!!.parent() as? Element

            if (parentOfTopCandidate != null) {
                val topScore = DomUtils.getReadabilityState(topCandidate)?.contentScore ?: 0.0
                val siblingScoreThreshold = maxOf(10.0, topScore * ScoringConstants.SIBLING_SCORE_THRESHOLD_FACTOR)
                val siblings = ArrayList(parentOfTopCandidate.children()!!)
                var s = 0
                while (s < siblings.size) {
                    val sibling = siblings[s]
                    var append = false

                    if (sibling == topCandidate) {
                        append = true
                    } else if (sibling is Element) {
                        var contentBonus = 0.0
                        // Class name match bonus
                        if (sibling.className() == topCandidate!!.className() &&
                            topCandidate!!.className().isNotEmpty()
                        ) {
                            contentBonus += topScore * ScoringConstants.SIBLING_CLASS_NAME_BONUS_FACTOR
                        }

                        val siblingState = DomUtils.getReadabilityState(sibling)
                        if (siblingState != null &&
                            siblingState.contentScore + contentBonus >= siblingScoreThreshold
                        ) {
                            append = true
                        } else if (sibling.tagName().uppercase() == "P") {
                            val linkDensity = LinkDensityCalculator.calculate(sibling)
                            val nodeContent = TextExtractor.getInnerText(sibling)
                            val nodeLength = nodeContent.length

                            if (nodeLength > ScoringConstants.SIBLING_MIN_LENGTH &&
                                linkDensity < ScoringConstants.SIBLING_LINK_DENSITY_THRESHOLD
                            ) {
                                append = true
                            } else if (nodeLength < ScoringConstants.SIBLING_MIN_LENGTH &&
                                nodeLength > 0 &&
                                linkDensity == 0.0 &&
                                nodeContent.contains(Regex("\\.( |\$)"))
                            ) {
                                append = true
                            }
                        }
                    }

                    if (append) {
                        if (sibling is Element &&
                            sibling.tagName().uppercase() !in TagSets.ALTER_TO_DIV_EXCEPTIONS
                        ) {
                            DomUtils.setNodeTag(sibling as Element, "DIV")
                        }
                        articleContent.appendChild(sibling)
                        // Refresh siblings list since we modified it
                        val newSiblings = parentOfTopCandidate.children()!!
                        siblings.clear()
                        siblings.addAll(newSiblings)
                        s--
                    }
                    s++
                }
            }

            // ----- Phase 6: Clean and validate -----
            // Clean the merged content on every attempt (mirrors _prepArticle being
            // called before the char-threshold check in Readability.js), so the
            // threshold is evaluated against cleaned text, not raw pre-clean text.
            ContentCleaner.prepArticle(articleContent, cleanConditionally)

            if (neededToCreateTopCandidate) {
                topCandidate.id("readability-page-1")
                topCandidate.addClass("page")
            } else {
                val div = doc.createElement("div")
                div.id("readability-page-1")
                div.addClass("page")
                val children = ArrayList(articleContent.childNodes())
                for (child in children) {
                    child.remove()
                    div.appendChild(child)
                }
                articleContent.appendChild(div)
            }

            // Check if we got enough content
            val textLength = TextExtractor.getInnerText(articleContent, true).length
            if (textLength < charThreshold) {
                // Record attempt and retry with relaxed flags
                attempts.add(Attempt(articleContent, textLength))
                // Restore page HTML for next attempt
                page.html(pageCacheHtml)

                flags = when {
                    (flags and FLAG_STRIP_UNLIKELYS) != 0 -> flags and FLAG_STRIP_UNLIKELYS.inv()
                    (flags and FLAG_WEIGHT_CLASSES) != 0 -> flags and FLAG_WEIGHT_CLASSES.inv()
                    (flags and FLAG_CLEAN_CONDITIONALLY) != 0 -> flags and FLAG_CLEAN_CONDITIONALLY.inv()
                    else -> {
                        // No more flags to remove — return best attempt
                        attempts.sortByDescending { it.textLength }
                        if (attempts.isEmpty() || attempts[0].textLength == 0) return null
                        return attempts[0].articleContent
                    }
                }
            } else {
                return articleContent
            }
        }
    }

    // ---- Phrasing content detection ----

    fun isPhrasingContent(node: Node): Boolean {
        if (node is TextNode) return true
        if (node is Element) {
            return node.tagName().uppercase() in TagSets.PHRASING_ELEMS ||
                    ((node.tagName().uppercase() == "A" ||
                            node.tagName().uppercase() == "DEL" ||
                            node.tagName().uppercase() == "INS") &&
                            node.childNodes().all { isPhrasingContent(it) })
        }
        return false
    }

    fun isWhitespace(node: Node): Boolean {
        if (node is TextNode) return node.text().trim().isEmpty()
        if (node is Element) return node.tagName().uppercase() == "BR"
        return false
    }

    fun isElementWithoutContent(node: Element): Boolean {
        return node.text().trim().isEmpty() &&
                (node.children().isEmpty() ||
                        node.children().size ==
                        node.getElementsByTag("br").size +
                        node.getElementsByTag("hr").size)
    }

    fun hasSingleTagInsideElement(element: Element, tag: String): Boolean {
        val children = element.children()
        return children.size == 1 && children[0].tagName().equals(tag, ignoreCase = true)
    }

    fun hasChildBlockElement(element: Element): Boolean {
        return element.childNodes().any { child ->
            child is Element && (TagSets.DIV_TO_P_ELEMS.contains(child.tagName().uppercase()) ||
                    hasChildBlockElement(child))
        }
    }

    /**
     * Initialize a node with base score from tag type + class weight.
     * Mirrors _initializeNode.
     */
    private fun initializeNode(node: Element, weightClasses: Boolean) {
        val state = DomUtils.ensureReadabilityState(node)
        state.contentScore = 0.0

        state.contentScore += when (node.tagName().uppercase()) {
            "DIV" -> ScoringConstants.DIV_SCORE.toDouble()
            "PRE", "TD", "BLOCKQUOTE" -> ScoringConstants.PRE_TD_BLOCKQUOTE_SCORE.toDouble()
            "ADDRESS", "OL", "UL", "DL", "DD", "DT", "LI", "FORM" -> ScoringConstants.ADDRESS_LIST_FORM_SCORE.toDouble()
            "H1", "H2", "H3", "H4", "H5", "H6", "TH" -> ScoringConstants.HEADING_TH_SCORE.toDouble()
            else -> 0.0
        }

        if (weightClasses) {
            state.contentScore += ClassWeightCalculator.calculate(node).toDouble()
        }
    }
}