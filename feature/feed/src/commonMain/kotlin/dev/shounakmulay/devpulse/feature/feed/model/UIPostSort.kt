package dev.shounakmulay.devpulse.feature.feed.model

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import kotlinx.serialization.Serializable

@Serializable
data class UIPostSort(
    val name: TextResource,
    val selected: Boolean,
    val sort: RssPostSort
)
