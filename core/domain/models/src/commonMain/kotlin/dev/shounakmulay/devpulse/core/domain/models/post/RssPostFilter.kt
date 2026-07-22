package dev.shounakmulay.devpulse.core.domain.models.post

sealed interface RssPostFilter {
   data class FeedIds(val values: Set<String>) : RssPostFilter
}