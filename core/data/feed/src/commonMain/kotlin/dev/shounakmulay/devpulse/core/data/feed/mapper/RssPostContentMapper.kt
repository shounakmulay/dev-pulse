package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalCompressedText
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssPostContentType
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import org.koin.core.annotation.Factory

@Factory
class RssPostContentMapper(
    private val uuidMapper: UuidMapper
) {

    fun toRssPostContent(content: LocalRssPostContent): RssFeedPostContent {
        return RssFeedPostContent(
            postId = uuidMapper.toUuid(content.postId),
            type = toPostContentType(content.type),
            content = content.content.text
        )
    }

    fun fromRssPostContent(content: RssFeedPostContent): LocalRssPostContent {
        return LocalRssPostContent(
            postId = uuidMapper.fromUuid(content.postId),
            type = fromPostContentType(content.type),
            content = LocalCompressedText(content.content)
        )
    }

    fun fromPostContentType(type: RssFeedPostContentType): LocalRssPostContentType = when (type) {
        RssFeedPostContentType.HTML -> LocalRssPostContentType.HTML
        RssFeedPostContentType.MARKDOWN -> LocalRssPostContentType.MARKDOWN
        RssFeedPostContentType.JSON -> LocalRssPostContentType.JSON
    }

    fun toPostContentType(type: LocalRssPostContentType) = when (type) {
        LocalRssPostContentType.HTML -> RssFeedPostContentType.HTML
        LocalRssPostContentType.MARKDOWN -> RssFeedPostContentType.MARKDOWN
        LocalRssPostContentType.JSON -> RssFeedPostContentType.JSON

    }
}