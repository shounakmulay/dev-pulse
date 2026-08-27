package dev.shounakmulay.devpulse.core.domain.models.raw.parsed

import kotlinx.coroutines.flow.Flow

data class ParsedFeed(
    val metadata: ParsedFeedMetadata,
    val items: Flow<ParsedFeedItem>,
    val issues: List<ParsedFeedIssue>
)
