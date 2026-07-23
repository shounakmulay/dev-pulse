package dev.shounakmulay.devpulse.feature.feed.interactor

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import devpulse.core.resources.generated.resources.newest
import devpulse.core.resources.generated.resources.oldest
import devpulse.core.resources.generated.resources.title_a_z
import devpulse.core.resources.generated.resources.title_z_a
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.annotation.Factory

@Factory
class PostInteractor {
    fun getDefaultUIPostSortValues(): ImmutableList<UIPostSort> {
        return RssPostSort.entries.map {
            val nameRes = when (it) {
                RssPostSort.PublishedNewest -> stringRes.newest
                RssPostSort.PublishedOldest -> stringRes.oldest
                RssPostSort.TitleAtoZ -> stringRes.title_a_z
                RssPostSort.TitleZtoA -> stringRes.title_z_a
            }
            UIPostSort(
                name = TextResource.fromStringRes(nameRes),
                selected = it == RssPostSort.PublishedNewest,
                sort = it
            )
        }.toImmutableList()
    }
}