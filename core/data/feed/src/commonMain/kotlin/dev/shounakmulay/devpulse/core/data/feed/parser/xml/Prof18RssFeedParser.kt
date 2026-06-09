package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import com.prof18.rssparser.RssParser
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedImportCandidate
import dev.shounakmulay.devpulse.core.data.feed.repository.RssContentFeedProcessor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import org.koin.core.annotation.Factory

@Factory
internal class Prof18RssFeedParser(
    private val rssParser: RssParser,
    private val prof18ParsedFeedMapper: Prof18ParsedFeedMapper,
    private val rssContentFeedProcessor: RssContentFeedProcessor? = null
) : FeedImportCandidate {
    override val id: String
        get() = ID

    override suspend fun import(
        entry: RssFeedQueueEntry,
        xml: String
    ) {
        val parsedFeed = prof18ParsedFeedMapper.toParsedFeed(rssParser.parse(xml))
        checkNotNull(rssContentFeedProcessor).process(entry, parsedFeed)
    }

    override suspend fun import(
        entry: RssFeedQueueEntry,
        iterator: CharIterator
    ) {
        val xml = buildString {
            while (iterator.hasNext()) {
                append(iterator.next())
            }
        }
        import(entry = entry, xml = xml)
    }

    private companion object {
        const val ID = "prof18"
    }
}
