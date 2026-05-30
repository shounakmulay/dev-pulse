package dev.shounakmulay.devpulse.core.data.feed.parser.model

import kotlinx.coroutines.flow.Flow

data class ParsedFeed(
    val metadata: ParsedFeedMetadata,
    val items: Flow<ParsedFeedItem>,
    val itemCount: Int?,
    val issues: List<ParsedFeedIssue>
)
