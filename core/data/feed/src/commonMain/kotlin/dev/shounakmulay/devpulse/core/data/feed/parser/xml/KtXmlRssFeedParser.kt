package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.FeedParser.Companion.TAG_ATOM
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.FeedParser.Companion.TAG_RDF
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.FeedParser.Companion.TAG_RSS
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedImportCandidate
import dev.shounakmulay.devpulse.core.data.feed.repository.RssContentFeedProcessor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.mini.MiniXmlPullParser
import org.koin.core.annotation.Factory

@Factory
internal class KtXmlRssFeedParser(
    private val rssFeedParser: RssFeedParser,
    private val rdfFeedParser: RdfFeedParser,
    private val atomFeedParser: AtomFeedParser,
    private val processor: RssContentFeedProcessor? = null
) : FeedImportCandidate {
    override val id: String
        get() = ID

    override suspend fun import(
        entry: RssFeedQueueEntry,
        xml: String
    ) {
        import(entry = entry, iterator = xml.iterator())
    }

    override suspend fun import(
        entry: RssFeedQueueEntry,
        iterator: CharIterator
    ) {
        val feed = parse(xmlIterator = iterator)
        checkNotNull(processor).process(entry = entry, parsedFeed = feed)
    }

    suspend fun parse(xmlIterator: CharIterator): ParsedFeed {
        val pullParser = MiniXmlPullParser(
            source = xmlIterator,
            relaxed = true,
        )

        while (pullParser.next() != EventType.END_DOCUMENT) {
            if (pullParser.eventType != EventType.START_TAG) continue

            return when (pullParser.name) {
                TAG_RSS -> {
                    rssFeedParser.parse(pullParser)
                }

                TAG_RDF -> {
                    rdfFeedParser.parse(pullParser)
                }

                TAG_ATOM -> {
                    atomFeedParser.parse(pullParser)
                }

                else -> throw IllegalArgumentException("Feed not supported: ${pullParser.name}")
            }
        }

        throw IllegalArgumentException("Feed not supported: ${pullParser.name}")
    }

    private companion object {
        const val ID = "ktxml"
    }
}
