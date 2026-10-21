package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model

internal fun readerBodyMarkdown(section: PostDetailScreenSection, article: String?, rss: String?, description: String?): String =
    when (section) {
        PostDetailScreenSection.RSS -> rss?.takeIf { it.isNotBlank() } ?: description.orEmpty()
        PostDetailScreenSection.EXTRACTED -> article?.takeIf { it.isNotBlank() } ?: rss?.takeIf { it.isNotBlank() } ?: description.orEmpty()
    }
