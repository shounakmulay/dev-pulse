package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.data.feed.IdentityGenerator
import org.koin.core.annotation.Factory

@Factory
class RssItemMapper(
    private val idGenerator: IdentityGenerator
) {

//    suspend fun toLocalRssContentFeedPost(item: RssItem): LocalRssContentFeedPost {
//        LocalRssContentFeedPost(
//            id = idGenerator.generateSortableId(),
//            feedId = ,
//            fingerprint = idGenerator.generateHash(),
//            lastSeenAt = TODO(),
//            userBookmarked = TODO(),
//            guid = TODO(),
//            title = TODO(),
//            author = TODO(),
//            link = TODO(),
//            pubDate = TODO(),
//            description = TODO(),
//            content = TODO(),
//            image = TODO(),
//            audio = TODO(),
//            video = TODO(),
//            sourceName = TODO(),
//            sourceUrl = TODO(),
//            categories = TODO(),
//            commentsUrl = TODO(),
//            youtubeData = TODO(),
//            rawEnclosure = TODO(),
//            rawMedia = TODO()
//        )
//    }
}