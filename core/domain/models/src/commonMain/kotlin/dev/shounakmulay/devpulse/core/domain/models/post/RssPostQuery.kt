package dev.shounakmulay.devpulse.core.domain.models.post

data class RssPostQuery(
    val filters: List<RssPostFilter>,
    val sort: RssPostSort,
    val order: RssPostSortOrder
)
