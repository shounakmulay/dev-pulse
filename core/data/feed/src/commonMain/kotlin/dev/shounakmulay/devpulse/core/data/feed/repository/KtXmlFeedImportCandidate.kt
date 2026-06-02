package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.feed.parser.KtXmlRssFeedParser
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import org.koin.core.annotation.Factory

@Factory
internal class KtXmlFeedImportCandidate(
    private val parser: KtXmlRssFeedParser,
    private val processor: RssContentFeedProcessor
) : FeedImportCandidate {
    override val id: String = Id

    override suspend fun import(entry: RssFeedQueueEntry) {
        val parsedFeed = parser.parseFeed(entry.url)
        processor.process(entry = entry, parsedFeed = parsedFeed)
    }

    private companion object {
        const val Id = "ktxml"
    }
}
