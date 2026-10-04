package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort

import dev.shounakmulay.devpulse.core.common.serializer.ImmutableListSerializer
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

@Serializable
data class PostSortAndFilterState(
    val screen: Screen,
    val filters: @Serializable(with = ImmutableListSerializer::class) ImmutableList<RssPostFilter> = PostInteractor.getDefaultFiltersFor(
        screen
    ),
    val sortValues: @Serializable(with = ImmutableListSerializer::class) ImmutableList<UIPostSort> = PostInteractor.DEFAULT_SORT_OPTIONS
) : ScreenState