package dev.shounakmulay.devpulse.readability.dom

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import dev.shounakmulay.devpulse.readability.heuristics.RegexPatterns

/**
 * DOM traversal utilities mirroring Readability.js node navigation.
 */
object NodeTraversal {

    /**
     * Get next element node in depth-first traversal order.
     * @param ignoreSelfAndKids If true, skip this node and its subtree.
     */
    fun getNextNode(node: Element, ignoreSelfAndKids: Boolean = false): Element? {
        // First check for kids (if not ignored)
        if (!ignoreSelfAndKids) {
            val firstChild = node.firstElementChild()
            if (firstChild != null) return firstChild
        }
        // Then for next sibling
        val nextSibling = node.nextElementSibling()
        if (nextSibling != null) return nextSibling
        // Move up the parent chain
        var current: Element? = node
        while (current != null && current.nextElementSibling() == null) {
            current = current.parent() as? Element
        }
        return current?.nextElementSibling()
    }

    /**
     * Get ancestors up to maxDepth.
     */
    fun getNodeAncestors(node: Element, maxDepth: Int = 0): List<Element> {
        val ancestors = mutableListOf<Element>()
        var current: Element? = node.parent() as? Element
        var depth = 0
        while (current != null) {
            ancestors.add(current)
            depth++
            if (maxDepth > 0 && depth >= maxDepth) break
            current = current.parent() as? Element
        }
        return ancestors
    }

    /**
     * Check if node has an ancestor with the given tag name.
     */
    fun hasAncestorTag(node: Element, tagName: String, maxDepth: Int = 10): Boolean {
        var current: Element? = node.parent() as? Element
        var depth = 0
        while (current != null && depth < maxDepth) {
            if (current.tagName().equals(tagName, ignoreCase = true)) return true
            current = current.parent() as? Element
            depth++
        }
        return false
    }

    /**
     * Check if node has an ancestor matching the filter function.
     */
    inline fun hasAncestorTag(
        node: Element,
        maxDepth: Int = 10,
        filterFn: (Element) -> Boolean
    ): Boolean {
        var current: Element? = node.parent() as? Element
        var depth = 0
        while (current != null && depth < maxDepth) {
            if (filterFn(current)) return true
            current = current.parent() as? Element
            depth++
        }
        return false
    }

    /**
     * Find the next meaningful element node, skipping whitespace text nodes.
     */
    fun nextNode(node: Node?): Node? {
        var next: Node? = node
        while (next != null &&
            next !is Element &&
            next is TextNode &&
            RegexPatterns.whitespace.matches(next.text())
        ) {
            next = next.nextSibling()
        }
        return next
    }
}
