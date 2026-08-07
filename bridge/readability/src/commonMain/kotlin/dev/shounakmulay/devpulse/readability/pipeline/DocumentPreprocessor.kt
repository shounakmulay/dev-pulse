package dev.shounakmulay.devpulse.readability.pipeline

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import dev.shounakmulay.devpulse.readability.dom.DomUtils
import dev.shounakmulay.devpulse.readability.dom.NodeTraversal

/**
 * Document preprocessing mirroring Readability.prototype._prepDocument +
 * _removeScripts + _unwrapNoscriptImages.
 */
object DocumentPreprocessor {

    /**
     * Remove all script tags from the document.
     */
    fun removeScripts(doc: Document) {
        val scripts = doc.getElementsByTag("script")
        for (script in scripts) {
            script.remove()
        }
    }

    /**
     * Unwrap images from <noscript> tags.
     * Mirrors _unwrapNoscriptImages — improves image quality on sites like Medium.
     */
    fun unwrapNoscriptImages(doc: Document) {
        // Remove placeholder images without src/srcset/data-src/data-srcset
        val imgs = doc.getElementsByTag("img")
        for (img in imgs) {
            var hasImageAttr = false
            for (attr in img.attributes()) {
                when (attr.key.lowercase()) {
                    "src", "srcset", "data-src", "data-srcset" -> {
                        hasImageAttr = true
                        break
                    }
                }
                if (Regex("""\.(jpg|jpeg|png|webp)""", RegexOption.IGNORE_CASE)
                        .containsMatchIn(attr.value)
                ) {
                    hasImageAttr = true
                    break
                }
            }
            if (!hasImageAttr) {
                img.remove()
            }
        }

        // Process noscript tags containing images
        val noscripts = doc.getElementsByTag("noscript")
        for (noscript in noscripts) {
            if (!isSingleImage(noscript)) continue

            val tmp = doc.createElement("div")
            tmp.html(noscript.html())

            val prevElement = noscript.previousElementSibling()
            if (prevElement != null && isSingleImage(prevElement)) {
                val prevImg = if (prevElement.tagName().uppercase() == "IMG") {
                    prevElement
                } else {
                    prevElement.getElementsByTag("img").firstOrNull()
                }

                val newImg = tmp.getElementsByTag("img").firstOrNull()
                if (prevImg != null && newImg != null) {
                    // Copy attributes from old img to new img
                    for (attr in prevImg.attributes()) {
                        if (attr.value.isEmpty()) continue
                        when (attr.key.lowercase()) {
                            "src", "srcset" -> continue // New img already has these
                        }
                        if (!newImg.hasAttr(attr.key)) {
                            newImg.attr(attr.key, attr.value)
                        }
                    }
                    prevImg.replaceWith(newImg)
                    noscript.remove()
                }
            }
        }
    }

    /**
     * Prepare the document: remove styles, replace <br> sequences, font→span.
     */
    fun prepDocument(doc: Document) {
        // Remove all style tags
        val styles = doc.getElementsByTag("style")
        for (style in styles) {
            style.remove()
        }

        // Replace <br> sequences in body
        val body = doc.body()
        if (body != null) {
            replaceBrs(body, doc)
        }

        // Replace <font> tags with <span>
        val fonts = doc.getElementsByTag("font")
        for (font in fonts) {
            DomUtils.setNodeTag(font, "SPAN")
        }
    }

    /**
     * Replace sequences of 2+ <br> tags with <p> tags.
     * Mirrors _replaceBrs.
     */
    private fun replaceBrs(elem: Element, doc: Document) {
        val brs = elem.getElementsByTag("br")
        for (br in brs) {
            var next = br.nextSibling()
            var replaced = false

            // Chain of consecutive <br>s
            var current = NodeTraversal.nextNode(next)
            while (current is Element && current.tagName().uppercase() == "BR") {
                replaced = true
                val brSibling = current.nextSibling()
                current.remove()
                current = brSibling
                next = current
                if (current != null) {
                    current = NodeTraversal.nextNode(current)
                }
            }

            if (replaced) {
                val p = doc.createElement("p")
                br.replaceWith(p)

                var siblingToMove = p.nextSibling()
                while (siblingToMove != null) {
                    if (siblingToMove is Element && siblingToMove.tagName().uppercase() == "BR") {
                        val nextElem = NodeTraversal.nextNode(siblingToMove.nextSibling())
                        if (nextElem is Element && nextElem.tagName().uppercase() == "BR") {
                            break
                        }
                    }

                    if (siblingToMove is Element && !ArticleGrabber.isPhrasingContent(siblingToMove)) {
                        break
                    }

                    val nextSib = siblingToMove.nextSibling()
                    siblingToMove.remove()
                    p.appendChild(siblingToMove)
                    siblingToMove = nextSib
                }

                // Trim trailing whitespace
                while (p.lastChild() != null && ArticleGrabber.isWhitespace(p.lastChild()!!)) {
                    p.lastChild()!!.remove()
                }

                val parent = p.parent() as? Element
                if (parent != null && parent.tagName().uppercase() == "P") {
                    DomUtils.setNodeTag(parent, "DIV")
                }
            }
        }
    }

    // ---- Image detection helpers ----

    private fun isSingleImage(node: Element): Boolean {
        var current: Element? = node
        while (current != null) {
            if (current.tagName().uppercase() == "IMG") return true
            if (current.children().size != 1 || current.text().trim().isNotEmpty()) return false
            current = current.child(0) as? Element
        }
        return false
    }
}
