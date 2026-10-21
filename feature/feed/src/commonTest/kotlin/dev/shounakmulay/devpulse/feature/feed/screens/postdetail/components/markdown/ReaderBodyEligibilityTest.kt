package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.ReaderVisitReadDecision
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.ReaderVisitReadState
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.readerBodyMarkdown
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReaderBodyEligibilityTest {
    @Test
    fun `Given parsed markdown When body contains prose links or code Then it qualifies as readable content`() {
        for (markdown in listOf("A readable paragraph.", "[Read this article](https://example.com)", "`val value = 1`", "    val value = 1", "```kotlin\nval value = 1\n```")) {
            assertTrue(hasReadableBodyText(parse(markdown), markdown), markdown)
        }
    }

    @Test
    fun `Given parsed markdown When body has only images whitespace or separators Then it cannot trigger a read`() {
        for (markdown in listOf("", " \n\t ", "![An image with descriptive alt text](https://example.com/image.png)", "[![Linked image](https://example.com/image.png)](https://example.com)", "<img src=\"https://example.com/image.png\" alt=\"Descriptive image\" />", "---")) {
            assertFalse(hasReadableBodyText(parse(markdown), markdown), markdown)
        }
        val proseAfterImage = "![Image](https://example.com/image.png)\n\nA readable paragraph."
        assertTrue(hasReadableBodyText(parse(proseAfterImage), proseAfterImage))
    }

    @Test
    fun `Given laid out text When body matches selected content and screen is resumed Then it is exposed`() {
        assertTrue(isReaderBodyExposed(renderedContent = "body", selectedContent = "body", hasText = true, isResumed = true, isVisible = true, isEligible = true))
        assertFalse(isReaderBodyExposed(renderedContent = "body", selectedContent = "body", hasText = true, isResumed = true, isVisible = true, isEligible = false))
        assertFalse(isReaderBodyExposed(renderedContent = "old section", selectedContent = "body", hasText = true, isResumed = true, isVisible = true))
        assertFalse(isReaderBodyExposed(renderedContent = "body", selectedContent = "body", hasText = false, isResumed = true, isVisible = true))
        assertFalse(isReaderBodyExposed(renderedContent = "body", selectedContent = "body", hasText = true, isResumed = false, isVisible = true))
        assertFalse(isReaderBodyExposed(renderedContent = "body", selectedContent = "body", hasText = true, isResumed = true, isVisible = false))
    }

    @Test
    fun `Given cached visible body before metadata When metadata arrives Then only the pending visit becomes exposed`() {
        val body = "Readable cached body"
        val hasText = hasReadableBodyText(parse(body), body)
        fun exposed(visit: ReaderVisitReadState, hasPost: Boolean) = isReaderBodyExposed(
            renderedContent = body,
            selectedContent = body,
            hasText = hasText,
            isResumed = true,
            isVisible = true,
            isEligible = hasPost && visit.decision == ReaderVisitReadDecision.Pending,
        )
        val beforeSnapshot = ReaderVisitReadState()
        assertFalse(exposed(beforeSnapshot, hasPost = false))
        assertTrue(exposed(beforeSnapshot, hasPost = true))
        assertFalse(exposed(beforeSnapshot.consume(), hasPost = true))
        assertFalse(exposed(beforeSnapshot.suppress(), hasPost = true))
        assertFalse(exposed(beforeSnapshot.suppress(), hasPost = false))
    }

    @Test
    fun `Given RSS section When article is ready Then only RSS or description body is selected`() {
        assertEquals("rss body", readerBodyMarkdown(section = PostDetailScreenSection.RSS, article = "article body", rss = "rss body", description = "description body"))
        assertEquals("description body", readerBodyMarkdown(section = PostDetailScreenSection.RSS, article = "article body", rss = " \n", description = "description body"))
        assertEquals("", readerBodyMarkdown(section = PostDetailScreenSection.RSS, article = "article body", rss = null, description = null))
    }

    @Test
    fun `Given extracted section When article is unavailable Then falls back to RSS description and empty body`() {
        assertEquals("article body", readerBodyMarkdown(section = PostDetailScreenSection.EXTRACTED, article = "article body", rss = "rss body", description = "description body"))
        assertEquals("rss body", readerBodyMarkdown(section = PostDetailScreenSection.EXTRACTED, article = " \n", rss = "rss body", description = "description body"))
        assertEquals("description body", readerBodyMarkdown(section = PostDetailScreenSection.EXTRACTED, article = null, rss = " \t", description = "description body"))
        assertEquals("", readerBodyMarkdown(section = PostDetailScreenSection.EXTRACTED, article = null, rss = null, description = null))
    }

    private fun parse(markdown: String) = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)
}
