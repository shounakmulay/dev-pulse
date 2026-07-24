package dev.shounakmulay.devpulse.feature.feed.interactor.post

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PostFilterAndSort(
    @Transient
    val filters: ImmutableList<RssPostFilter> = persistentListOf(),
    @Transient
    val sortValues: ImmutableList<UIPostSort> = persistentListOf()
)

