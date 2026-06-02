package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.feed.parser.Prof18RssFeedParser
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import org.koin.core.annotation.Factory

@Factory
internal class Prof18FeedImportCandidate(
    private val parser: Prof18RssFeedParser,
    private val processor: RssContentFeedProcessor
) : FeedImportCandidate {
    override val id: String = Id

    override suspend fun import(entry: RssFeedQueueEntry) {
        val rssChannel = parser.parseFeed(entry.url)
        processor.process(entry = entry, rssChannel = rssChannel)
    }

    private companion object {
        const val Id = "prof18"
    }
}
