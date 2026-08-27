package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import dev.shounakmulay.devpulse.core.data.feed.parser.xml.FeedParser.Companion.TAG_ATOM
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.FeedParser.Companion.TAG_RDF
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.FeedParser.Companion.TAG_RSS
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedImportCandidate
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.mini.MiniXmlPullParser
import org.koin.core.annotation.Factory

@Factory
internal class KtXmlRssFeedParser(
    private val rssFeedParser: RssFeedParser,
    private val rdfFeedParser: RdfFeedParser,
    private val atomFeedParser: AtomFeedParser,
) : FeedImportCandidate {
    override val id: String
        get() = ID

    override suspend fun parse(
        entry: RssFeedQueueEntry,
        xml: String
    ): ParsedFeed {
        return parse(entry = entry, iterator = xml.iterator())
    }

    override suspend fun parse(
        entry: RssFeedQueueEntry,
        iterator: CharIterator
    ): ParsedFeed {
        return parse(xmlIterator = iterator)
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
