package dev.shounakmulay.devpulse.readability.dom

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import dev.shounakmulay.devpulse.readability.model.NodeReadabilityState

/**
 * DOM utility functions mirroring Readability.js DOM operations.
 * Uses external WeakHashMap for node state since Ksoup lacks userData.
 */
object DomUtils {

    /** External storage for readability state (Ksoup doesn't support userData) */
    private val readabilityStates = mutableMapOf<Element, NodeReadabilityState>()

    fun getReadabilityState(el: Element): NodeReadabilityState? = readabilityStates[el]

    fun ensureReadabilityState(el: Element): NodeReadabilityState {
        return readabilityStates.getOrPut(el) { NodeReadabilityState() }
    }

    fun clearState() = readabilityStates.clear()

    /** Copy state from one element to another during tag replacement */
    fun transferState(from: Element, to: Element) {
        readabilityStates[from]?.let { readabilityStates[to] = it }
        readabilityStates.remove(from)
    }

    // ---- Node collection ----

    fun getAllNodesWithTag(root: Element, tagNames: Iterable<String>): Elements {
        val selector = tagNames.joinToString(",") { it.lowercase() }
        return root.select(selector)
    }

    fun getAllNodesWithTag(root: Element, tagName: String): Elements {
        return root.getElementsByTag(tagName)
    }

    // ---- Node iteration ----

    inline fun forEachNode(elements: Elements, fn: (Element) -> Unit) {
        for (el in elements) fn(el)
    }

    inline fun findNode(elements: Elements, fn: (Element) -> Boolean): Element? {
        for (el in elements) if (fn(el)) return el
        return null
    }

    inline fun someNode(elements: Elements, fn: (Element) -> Boolean): Boolean {
        for (el in elements) if (fn(el)) return true
        return false
    }

    inline fun everyNode(elements: Elements, fn: (Element) -> Boolean): Boolean {
        for (el in elements) if (!fn(el)) return false
        return true
    }

    // ---- Node removal ----

    fun removeNodes(nodeList: List<Element>, filterFn: ((Element) -> Boolean)? = null) {
        for (i in nodeList.size - 1 downTo 0) {
            val node = nodeList[i]
            if (filterFn == null || filterFn(node)) {
                readabilityStates.remove(node)
                node.remove()
            }
        }
    }

    fun removeAndGetNext(node: Element, getNextFn: (Element, Boolean) -> Element?): Element? {
        val next = getNextFn(node, true)
        readabilityStates.remove(node)
        node.remove()
        return next
    }

    // ---- Tag manipulation ----

    fun replaceNodeTags(nodeList: Elements, newTagName: String) {
        for (node in nodeList) setNodeTag(node, newTagName)
    }

    fun setNodeTag(node: Element, tag: String): Element {
        val replacement = Element(tag.lowercase())
        // Move all children
        val children = ArrayList(node.childNodes())
        for (child in children) {
            child.remove()
            replacement.appendChild(child)
        }
        // Copy attributes
        for (attr in node.attributes()) {
            replacement.attr(attr.key, attr.value)
        }
        // Transfer readability state
        transferState(node, replacement)
        // Replace in DOM
        node.replaceWith(replacement)
        return replacement
    }

    // ---- Visibility ----

    fun isProbablyVisible(node: Element): Boolean {
        val style = node.attr("style")
        if (style.contains("display:none", ignoreCase = true) ||
            style.contains("display: none", ignoreCase = true)) return false
        if (style.contains("visibility:hidden", ignoreCase = true) ||
            style.contains("visibility: hidden", ignoreCase = true)) return false
        if (node.hasAttr("hidden")) return false
        val ariaHidden = node.attr("aria-hidden")
        if (ariaHidden == "true") {
            val cls = node.attr("class")
            if (!cls.contains("fallback-image")) return false
        }
        return true
    }
}
