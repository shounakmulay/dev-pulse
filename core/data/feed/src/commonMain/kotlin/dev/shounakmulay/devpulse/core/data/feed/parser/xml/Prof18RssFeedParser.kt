package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import com.prof18.rssparser.RssParser
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedImportCandidate
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import org.koin.core.annotation.Factory

@Factory
internal class Prof18RssFeedParser(
    private val rssParser: RssParser,
    private val prof18ParsedFeedMapper: Prof18ParsedFeedMapper,
) : FeedImportCandidate {
    override val id: String
        get() = ID

    override suspend fun parse(
        entry: RssFeedQueueEntry,
        xml: String
    ): ParsedFeed {
        return prof18ParsedFeedMapper.toParsedFeed(rssParser.parse(xml))
    }

    override suspend fun parse(
        entry: RssFeedQueueEntry,
        iterator: CharIterator
    ): ParsedFeed {
        val xml = buildString {
            while (iterator.hasNext()) {
                append(iterator.next())
            }
        }
        return parse(entry = entry, xml = xml)
    }

    private companion object {
        const val ID = "prof18"
    }
}
