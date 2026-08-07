package dev.shounakmulay.devpulse.readability.cleaning

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import com.fleeksoft.ksoup.select.Elements
import dev.shounakmulay.devpulse.readability.cleaning.ContentCleaner.isDataTable
import dev.shounakmulay.devpulse.readability.cleaning.ContentCleaner.markDataTables
import dev.shounakmulay.devpulse.readability.dom.DomUtils
import dev.shounakmulay.devpulse.readability.dom.NodeTraversal
import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns
import dev.shounakmulay.devpulse.readability.heuristics.ScoringConstants
import dev.shounakmulay.devpulse.readability.heuristics.TagSets
import dev.shounakmulay.devpulse.readability.scoring.ClassWeightCalculator
import dev.shounakmulay.devpulse.readability.scoring.LinkDensityCalculator
import dev.shounakmulay.devpulse.readability.scoring.TextDensityCalculator
import dev.shounakmulay.devpulse.readability.text.TextExtractor

/**
 * Content cleaning pipeline mirroring Readability.js _prepArticle + subordinate cleaning methods.
 *
 * The cleaning pass runs after the top candidate has been selected and its siblings merged.
 * It removes junk elements, presentational attributes, empty containers, and conditionally
 * prunes nodes that pass heuristic filters based on class weight, link density, text density,
 * image/embed counts, and structural patterns (single-cell tables, share-element stubs, etc.).
 */
object ContentCleaner {

    /** Key for attaching data-table flags to elements via userData (mirrors _readabilityDataTable). */
    private const val DATA_TABLE_CLASS = "readability-data-table"

    // ---- Orchestration ----

    /**
     * Prepare the article content node for display.
     *
     * Steps (mirror _prepArticle):
     *   1. Strip presentational style attributes.
     *   2. Mark tables that are genuine data tables (vs layout).
     *   3. Convert lazy-loaded image attributes to src/srcset.
     *   4. Conditionally clean forms and fieldsets; unconditionally clean
     *      object/embed/footer/link/aside.
     *   5. Clean share-elements with textContent shorter than the threshold.
     *   6. Clean iframe/input/textarea/select/button.
     *   7. Clean headers with negative class weight.
     *   8. Conditionally clean remaining tables, lists, and divs.
     *   9. Replace all H1 → H2 (the title is displayed separately).
     *  10. Remove empty paragraphs, BR that immediately precedes P, and
     *      flatten single-cell tables.
     */
    fun prepArticle(articleContent: Element, cleanConditionallyEnabled: Boolean = true) {
        cleanStyles(articleContent)
        markDataTables(articleContent)
        fixLazyImages(articleContent)

        cleanConditionally(articleContent, "form", cleanConditionallyEnabled)
        cleanConditionally(articleContent, "fieldset", cleanConditionallyEnabled)
        clean(articleContent, "object")
        clean(articleContent, "embed")
        clean(articleContent, "footer")
        clean(articleContent, "link")
        clean(articleContent, "aside")

        // Clean share elements with textContent shorter than the threshold.
        // Iterate top-level children so top candidates are preserved even if they
        // happen to contain "share" in their class/id.
        val shareElementThreshold = ScoringConstants.SHARE_ELEMENT_THRESHOLD
        for (topCandidate in articleContent.children()) {
            cleanMatchedNodes(topCandidate) { node, matchString ->
                RegexPatterns.shareElements.containsMatchIn(matchString) &&
                    node.text().length < shareElementThreshold
            }
        }

        clean(articleContent, "iframe")
        clean(articleContent, "input")
        clean(articleContent, "textarea")
        clean(articleContent, "select")
        clean(articleContent, "button")
        cleanHeaders(articleContent)

        cleanConditionally(articleContent, "table", cleanConditionallyEnabled)
        cleanConditionally(articleContent, "ul", cleanConditionallyEnabled)
        cleanConditionally(articleContent, "div", cleanConditionallyEnabled)

        // Replace H1 → H2 — H1 is reserved for the extracted title.
        DomUtils.replaceNodeTags(
            DomUtils.getAllNodesWithTag(articleContent, listOf("h1")),
            "h2"
        )

        // Remove empty paragraphs (no img/embed/object/iframe and no inner text).
        val emptyParagraphs = DomUtils.getAllNodesWithTag(articleContent, listOf("p"))
            .filter { paragraph ->
                val contentElementCount = DomUtils.getAllNodesWithTag(
                    paragraph, listOf("img", "embed", "object", "iframe")
                ).size
                contentElementCount == 0 && TextExtractor.getInnerText(paragraph, normalizeSpaces = false).isEmpty()
            }
        DomUtils.removeNodes(emptyParagraphs.toList())

        // Remove BR immediately before a P node.
        val allBrs = DomUtils.getAllNodesWithTag(articleContent, listOf("br"))
        for (br in allBrs) {
            val next = NodeTraversal.nextNode(br.nextSibling())
            if (next is Element && next.tagName().equals("p", ignoreCase = true)) {
                br.remove()
            }
        }

        // Flatten single-cell tables (tbody → tr → td with single child).
        val allTables = DomUtils.getAllNodesWithTag(articleContent, listOf("table"))
        for (table in allTables) {
            val tbody = if (hasSingleTagInsideElement(table, "tbody"))
                table.firstElementChild()!!
            else
                table

            if (hasSingleTagInsideElement(tbody, "tr")) {
                val row = tbody.firstElementChild()!!
                if (hasSingleTagInsideElement(row, "td")) {
                    val cell = row.firstElementChild()!!
                    val newCell = DomUtils.setNodeTag(
                        cell,
                        if (allChildNodesArePhrasing(cell)) "p" else "div"
                    )
                    table.parent()?.let { parent ->
                        newCell.remove()
                        val idx = table.siblingIndex()
                        parent.insertChildren(idx, listOf(newCell))
                        table.remove()
                    }
                }
            }
        }
    }

    // ---- Cleaning methods ----

    /**
     * Clean all elements of [tag] from [element]'s subtree.
     *
     * For `object`/`embed`/`iframe` tags, video embeds (YouTube / Vimeo / etc.)
     * are preserved — the element is only removed if no attribute or inner HTML
     * matches the `videos` regex.
     *
     * Mirrors _clean.
     */
    fun clean(element: Element, tag: String) {
        val isEmbed = tag in setOf("object", "embed", "iframe")
        val nodes = DomUtils.getAllNodesWithTag(element, listOf(tag))
        DomUtils.removeNodes(nodes.toList()) { node ->
            if (isEmbed) {
                // Check attribute values.
                for (attr in node.attributes()) {
                    if (RegexPatterns.videos.containsMatchIn(attr.value)) {
                        return@removeNodes false
                    }
                }
                // For <object> also check inner HTML.
                if (node.tagName().equals("object", ignoreCase = true) &&
                    RegexPatterns.videos.containsMatchIn(node.html())
                ) {
                    return@removeNodes false
                }
            }
            true // remove this node
        }
    }

    /**
     * Conditionally remove elements of type [tag] that look "fishy".
     *
     * Decision is based on a composite heuristic:
     *   - Class weight (via [ClassWeightCalculator])
     *   - Link density (via [LinkDensityCalculator])
     *   - Comma count, paragraph count, image count, input count
     *   - Heading density, embed count
     *   - Text density against textish tags (SPAN, LI, TD + block-level tags)
     *   - Whether the element or an ancestor is a data table
     *   - Ancestor <code> check
     *
     * For UL/OL: the node is kept if every <li> contains exactly one `<img>`.
     *
     * Mirrors _cleanConditionally.
     */
    fun cleanConditionally(element: Element, tag: String, active: Boolean = true) {
        if (!active) return   // <-- new guard, mirrors _cleanConditionally's flag check

        val isListTag = tag.equals("ul", ignoreCase = true) || tag.equals("ol", ignoreCase = true)
        val nodes = DomUtils.getAllNodesWithTag(element, listOf(tag))

        DomUtils.removeNodes(nodes.toList()) { node ->
            // ---- Guardian: data tables ----
            val isDataTableFn: (Element) -> Boolean = { t ->
                isDataTable(t)
            }

            if (tag.equals("table", ignoreCase = true) && isDataTableFn(node)) {
                return@removeNodes false
            }
            if (hasAncestor(node, "table", maxDepth = -1, isDataTableFn)) {
                return@removeNodes false
            }
            if (hasAncestorTag(node, "code")) {
                return@removeNodes false
            }
            // If the node itself *contains* a data-table descendant, preserve.
            if (node.getElementsByTag("table").any { tbl -> isDataTable(tbl) }) {
                return@removeNodes false
            }

            // Determine whether this node behaves as a list.
            var effectiveIsList = isListTag
            if (!effectiveIsList) {
                val listNodes = DomUtils.getAllNodesWithTag(node, listOf("ul", "ol"))
                val listLength = listNodes.sumOf { TextExtractor.getInnerText(it).length }
                val totalLength = TextExtractor.getInnerText(node).length
                effectiveIsList = totalLength > 0 && listLength.toDouble() / totalLength > 0.9
            }

            // ---- Class weight ----
            val weight = ClassWeightCalculator.calculate(node)
            val contentScore = 0 // mirror original; contentScore here is always 0
            if (weight + contentScore < 0) {
                return@removeNodes true
            }

            // ---- Comma-count gate ----
            val commaCount = TextExtractor.getCharCount(node, ",")
            if (commaCount < 10) {
                val pCount = node.getElementsByTag("p").size
                val imgCount = node.getElementsByTag("img").size
                val liCount = node.getElementsByTag("li").size - 100
                val inputCount = node.getElementsByTag("input").size
                val headingDensity = TextDensityCalculator.calculate(
                    node, listOf("h1", "h2", "h3", "h4", "h5", "h6")
                )

                // Count embeds that are NOT video providers.
                val embeds = DomUtils.getAllNodesWithTag(node, listOf("object", "embed", "iframe"))
                var embedCount = 0
                for (embed in embeds) {
                    // If this embed is a video provider, abort removal entirely.
                    for (attr in embed.attributes()) {
                        if (RegexPatterns.videos.containsMatchIn(attr.value)) {
                            return@removeNodes false
                        }
                    }
                    if (embed.tagName().equals("object", ignoreCase = true) &&
                        RegexPatterns.videos.containsMatchIn(embed.html())
                    ) {
                        return@removeNodes false
                    }
                    embedCount++
                }

                val innerText = TextExtractor.getInnerText(node)

                // Suspicious-word detection.
                if (RegexPatterns.adWords.containsMatchIn(innerText) ||
                    RegexPatterns.loadingWords.containsMatchIn(innerText)
                ) {
                    return@removeNodes true
                }

                val contentLength = innerText.length
                val linkDensity = LinkDensityCalculator.calculate(node)
                val textishTags = listOf("span", "li", "td") +
                    TagSets.DIV_TO_P_ELEMS.map { it.lowercase() }
                val textDensity = TextDensityCalculator.calculate(node, textishTags)
                val isFigureChild = hasAncestorTag(node, "figure")

                // ---- Decision matrix ----
                data class Rule(val description: String, val triggered: Boolean)

                val rules = buildList {
                    if (!isFigureChild && imgCount > 1 && pCount.toDouble() / imgCount < 0.5) {
                        add(Rule("Bad p to img ratio (img=$imgCount, p=$pCount)", true))
                    }
                    if (!effectiveIsList && liCount > pCount) {
                        add(Rule("Too many li's outside of a list (li=$liCount > p=$pCount)", true))
                    }
                    if (inputCount > (pCount / 3)) {
                        add(Rule("Too many inputs per p (input=$inputCount, p=$pCount)", true))
                    }
                    if (!effectiveIsList && !isFigureChild &&
                        headingDensity < 0.9 && contentLength < 25 &&
                        (imgCount == 0 || imgCount > 2) && linkDensity > 0
                    ) {
                        add(Rule(
                            "Suspiciously short (headingDensity=$headingDensity, img=$imgCount, linkDensity=$linkDensity)",
                            true
                        ))
                    }
                    if (!effectiveIsList && weight < ScoringConstants.CLEAN_CONDITIONALLY_WEIGHT_THRESHOLD &&
                        linkDensity > ScoringConstants.CLEAN_CONDITIONALLY_LINK_DENSITY_LOW
                    ) {
                        add(Rule("Low weight and a little linky (linkDensity=$linkDensity)", true))
                    }
                    if (weight >= ScoringConstants.CLEAN_CONDITIONALLY_WEIGHT_THRESHOLD &&
                        linkDensity > ScoringConstants.CLEAN_CONDITIONALLY_LINK_DENSITY_HIGH
                    ) {
                        add(Rule("High weight and mostly links (linkDensity=$linkDensity)", true))
                    }
                    if ((embedCount == 1 && contentLength < ScoringConstants.CLEAN_CONDITIONALLY_EMBED_CONTENT_LENGTH) ||
                        embedCount > 1
                    ) {
                        add(Rule(
                            "Suspicious embed (embedCount=$embedCount, contentLength=$contentLength)",
                            true
                        ))
                    }
                    if (imgCount == 0 && textDensity == 0.0) {
                        add(Rule("No useful content (img=$imgCount, textDensity=$textDensity)", true))
                    }
                }

                val haveToRemove = rules.isNotEmpty()

                // For lists: allow to remain if every <li> contains exactly one <img>.
                if (effectiveIsList && haveToRemove) {
                    for (child in node.children()) {
                        if (child.children().size > 1) {
                            return@removeNodes haveToRemove
                        }
                    }
                    val liCountDirect = node.getElementsByTag("li").size
                    if (imgCount == liCountDirect) {
                        return@removeNodes false
                    }
                }

                return@removeNodes haveToRemove
            }

            // Comma count >= 10 → keep.
            return@removeNodes false
        }
    }

    /**
     * Remove H1 and H2 elements whose class weight is negative.
     *
     * Mirrors _cleanHeaders.
     */
    fun cleanHeaders(element: Element) {
        val headingNodes = DomUtils.getAllNodesWithTag(element, listOf("h1", "h2"))
        DomUtils.removeNodes(headingNodes.toList()) { node ->
            ClassWeightCalculator.calculate(node) < 0
        }
    }

    /**
     * Traverse the subtree of [element] depth-first. For each descendant,
     * call [filter] with the node and `className + " " + id` as the match string.
     * Nodes for which [filter] returns `true` are removed.
     *
     * Mirrors _cleanMatchedNodes.
     */
    fun cleanMatchedNodes(element: Element, filter: (Element, String) -> Boolean) {
        val endMarker = NodeTraversal.getNextNode(element, ignoreSelfAndKids = true)
        var next: Element? = NodeTraversal.getNextNode(element)

        while (next != null && next != endMarker) {
            val matchString = "${next.className()} ${next.id()}"
            next = if (filter(next, matchString)) {
                removeAndGetNext(next)
            } else {
                NodeTraversal.getNextNode(next)
            }
        }
    }

    /**
     * Remove presentational attributes (`style`, `align`, `bgcolor`, etc.)
     * and deprecated size attributes (`width`/`height`) from the subtree.
     * Recurses into children. SVG elements are skipped.
     *
     * Mirrors _cleanStyles.
     */
    fun cleanStyles(element: Element) {
        if (element.tagName().equals("svg", ignoreCase = true)) return

        for (attr in TagSets.PRESENTATIONAL_ATTRIBUTES) {
            element.removeAttr(attr)
        }

        if (TagSets.DEPRECATED_SIZE_ATTRIBUTE_ELEMS.contains(element.tagName().uppercase())) {
            element.removeAttr("width")
            element.removeAttr("height")
        }

        var child = element.firstElementChild()
        while (child != null) {
            cleanStyles(child)
            child = child.nextElementSibling()
        }
    }

    /**
     * Evaluate every `<table>` in [root]'s subtree and mark it as a data table
     * (versus a layout table). The flag is stored as userData under [DATA_TABLE_KEY]
     * and can be queried with [isDataTable].
     *
     * Mirrors _markDataTables.
     */
    fun markDataTables(root: Element) {
        val tables = root.getElementsByTag("table")
        for (table in tables) {
            val role = table.attr("role")
            if (role == "presentation") {
                setDataTableFlag(table, false)
                continue
            }
            val datatable = table.attr("datatable")
            if (datatable == "0") {
                setDataTableFlag(table, false)
                continue
            }
            val summary = table.attr("summary")
            if (summary.isNotEmpty()) {
                setDataTableFlag(table, true)
                continue
            }
            val caption = table.getElementsByTag("caption").firstOrNull()
            if (caption != null && caption.childNodes().isNotEmpty()) {
                setDataTableFlag(table, true)
                continue
            }
            // If the table has a descendant with col/colgroup/tfoot/thead/th → data table.
            val dataTableDescendants = listOf("col", "colgroup", "tfoot", "thead", "th")
            if (dataTableDescendants.any { tag -> table.getElementsByTag(tag).isNotEmpty() }) {
                setDataTableFlag(table, true)
                continue
            }
            // Nested tables → layout.
            if (table.getElementsByTag("table").isNotEmpty()) {
                setDataTableFlag(table, false)
                continue
            }
            // Size-based heuristics.
            val (rows, columns) = getRowAndColumnCount(table)
            if (columns == 1 || rows == 1) {
                setDataTableFlag(table, false)
                continue
            }
            if (rows >= 10 || columns > 4) {
                setDataTableFlag(table, true)
                continue
            }
            setDataTableFlag(table, rows * columns > 10)
        }
    }

    /**
     * Convert lazy-loaded image/figure attributes (e.g. `data-src`, `data-original`)
     * into standard `src`/`srcset` attributes so the content is displayable without JS.
     *
     * Also strips tiny base64 placeholder images from the `src` attribute.
     *
     * Mirrors _fixLazyImages.
     */
    fun fixLazyImages(root: Element) {
        val nodes = DomUtils.getAllNodesWithTag(root, listOf("img", "picture", "figure"))
        for (elem in nodes) {
            // ---- Strip tiny base64 placeholder in src ----
            val src = elem.attr("src")
            if (src.isNotEmpty() && RegexPatterns.b64DataUrl.containsMatchIn(src)) {
                val match = RegexPatterns.b64DataUrl.find(src)
                if (match != null) {
                    val mimeType = match.groupValues[1]
                    // SVG may be meaningful even with small payload.
                    if (mimeType != "image/svg+xml") {
                        // Check whether other attributes hold a real image extension.
                        var srcCouldBeRemoved = false
                        for (attr in elem.attributes()) {
                            if (attr.key == "src") continue
                            if (Regex("\\.(jpg|jpeg|png|webp)", RegexOption.IGNORE_CASE).containsMatchIn(attr.value)) {
                                srcCouldBeRemoved = true
                                break
                            }
                        }
                        if (srcCouldBeRemoved) {
                            val b64Starts = match.value.length
                            val b64Length = src.length - b64Starts
                            if (b64Length < 133) {
                                elem.removeAttr("src")
                            }
                        }
                    }
                }
            }

            // ---- Convert lazy attributes to src/srcset ----
            val currentSrc = elem.attr("src")
            val srcset = elem.attr("srcset")
            val className = elem.className()
            if ((currentSrc.isNotEmpty() || (srcset.isNotEmpty() && srcset != "null")) &&
                !className.lowercase().contains("lazy")
            ) {
                continue
            }

            for (attr in elem.attributes()) {
                val name = attr.key
                val value = attr.value
                if (name == "src" || name == "srcset" || name == "alt") continue

                val copyTo: String? = when {
                    Regex("\\.(jpg|jpeg|png|webp)\\s+\\d", RegexOption.IGNORE_CASE)
                        .containsMatchIn(value) -> "srcset"
                    Regex("^\\s*\\S+\\.(jpg|jpeg|png|webp)\\S*\\s*$", RegexOption.IGNORE_CASE)
                        .matches(value) -> "src"
                    else -> null
                }

                if (copyTo != null) {
                    when {
                        elem.tagName().equals("img", ignoreCase = true) ||
                            elem.tagName().equals("picture", ignoreCase = true) -> {
                            elem.attr(copyTo, value)
                        }
                        elem.tagName().equals("figure", ignoreCase = true) &&
                            DomUtils.getAllNodesWithTag(elem, listOf("img", "picture")).isEmpty() -> {
                            // <figure> with no inner image → create one.
                            val img = Element("img")
                            img.attr(copyTo, value)
                            elem.appendChild(img)
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns `true` when [node] has no text content *and* contains only
     * `<br>` / `<hr>` children (or no children at all).
     *
     * Mirrors _isElementWithoutContent.
     */
    fun isElementWithoutContent(node: Element): Boolean {
        val text = node.text().trim()
        if (text.isNotEmpty()) return false
        val children = node.children()
        if (children.isEmpty()) return true
        val brHrCount = children.count { child ->
            val tag = child.tagName()
            tag.equals("br", ignoreCase = true) || tag.equals("hr", ignoreCase = true)
        }
        return children.size == brHrCount
    }

    /**
     * Determine whether [table] is a genuine data table (as opposed to a layout
     * table). Checks the flag set by [markDataTables].
     *
     * Mirrors the inline `isDataTable` helper in `_cleanConditionally`.
     */
    /** Set of elements marked as data tables */
    private val dataTables = mutableSetOf<Element>()

    fun isDataTable(table: Element): Boolean = table in dataTables

    private fun setDataTableFlag(table: Element, value: Boolean) {
        if (value) dataTables.add(table) else dataTables.remove(table)
    }

    // ---- Internal helpers ----

    /**
     * Returns `true` when [element] contains exactly one element child with tag
     * [tag] and no non-whitespace text nodes.
     *
     * Mirrors _hasSingleTagInsideElement.
     */
    private fun hasSingleTagInsideElement(element: Element, tag: String): Boolean {
        val children = element.children()
        if (children.size != 1) return false
        if (!children[0].tagName().equals(tag, ignoreCase = true)) return false
        // Verify no non-whitespace text nodes.
        for (child in element.childNodes()) {
            if (child is TextNode) {
                if (RegexPatterns.hasContent.containsMatchIn(child.text())) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Check if every child node of [element] qualifies as phrasing content
     * (text nodes or phrasing elements like IMG, SPAN, etc.).
     */
    private fun allChildNodesArePhrasing(element: Element): Boolean {
        return element.children().all { child -> isPhrasingContent(child) } &&
            element.childNodes().all { child ->
            when {
                child is Element -> true // already checked above
                child is TextNode -> true
                else -> false
            }
        }
    }

    /**
     * Determine whether [node] is phrasing content (text node or a phrasing element).
     * For A/DEL/INS, all children must also be phrasing content.
     *
     * Mirrors _isPhrasingContent.
     */
    private fun isPhrasingContent(node: Element): Boolean {
        val tag = node.tagName().uppercase()
        if (tag in TagSets.PHRASING_ELEMS) return true
        if (tag == "A" || tag == "DEL" || tag == "INS") {
            return DomUtils.everyNode(Elements(node.children())) { child ->
                isPhrasingContent(child)
            }
        }
        return false
    }

    /**
     * Check whether [node] has an ancestor matching [tagName], optionally
     * filtered by [filterFn]. [maxDepth] of -1 means no limit.
     *
     * Mirrors _hasAncestorTag.
     */
    private fun hasAncestorTag(
        node: Element,
        tagName: String,
        maxDepth: Int = 3,
        filterFn: ((Element) -> Boolean)? = null
    ): Boolean {
        var depth = 0
        var current: Element? = node.parent() as? Element
        val upperTag = tagName.uppercase()

        while (current != null) {
            if (maxDepth > 0 && depth > maxDepth) return false
            if (current.tagName().uppercase() == upperTag &&
                (filterFn == null || filterFn(current))
            ) {
                return true
            }
            current = current.parent() as? Element
            depth++
        }
        return false
    }

    /**
     * Check whether [node] has an ancestor matching [tagName] with optional
     * [filterFn]. When [filterFn] is provided, the ancestor must satisfy it.
     * [maxDepth] of -1 means unlimited.
     *
     * Mirrors the `_hasAncestorTag(..., filterFn)` overload.
     */
    private fun hasAncestor(
        node: Element,
        tagName: String,
        maxDepth: Int = 3,
        filterFn: (Element) -> Boolean
    ): Boolean {
        return hasAncestorTag(node, tagName, maxDepth, filterFn)
    }

    /**
     * Remove [node] and return the next node in traversal order (after the
     * removed subtree).
     *
     * Mirrors _removeAndGetNext.
     */
    private fun removeAndGetNext(node: Element): Element? {
        val next = NodeTraversal.getNextNode(node, ignoreSelfAndKids = true)
        node.remove()
        return next
    }

    /**
     * Count rows and columns for a table. Takes rowspan/colspan into account.
     *
     * Mirrors _getRowAndColumnCount.
     */
    private fun getRowAndColumnCount(table: Element): Pair<Int, Int> {
        var rows = 0
        var columns = 0
        val trs = table.getElementsByTag("tr")
        for (tr in trs) {
            val rowspan = tr.attr("rowspan").toIntOrNull() ?: 1
            rows += if (rowspan > 0) rowspan else 1

            var columnsInThisRow = 0
            val cells = tr.getElementsByTag("td")
            for (td in cells) {
                val colspan = td.attr("colspan").toIntOrNull() ?: 1
                columnsInThisRow += if (colspan > 0) colspan else 1
            }
            columns = maxOf(columns, columnsInThisRow)
        }
        return Pair(rows, columns)
    }
}
