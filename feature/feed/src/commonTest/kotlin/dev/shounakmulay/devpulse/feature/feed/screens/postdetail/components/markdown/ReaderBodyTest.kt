package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.readerBodyMarkdown
import kotlin.test.Test
import kotlin.test.assertEquals

class ReaderBodyTest {
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
}
