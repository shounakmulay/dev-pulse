package dev.shounakmulay.devpulse.feature.feed.components.feedOptions

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Immutable
data class FeedOptionsTarget(
    val feedId: UUID,
    val items: ImmutableList<FeedOptionsMenuItem>,
)

@Immutable
sealed interface FeedOptionsState {
    data class Open(val target: FeedOptionsTarget) : FeedOptionsState
    data class ConfirmingDelete(val feedId: UUID, val option: FeedOptionsMenuItem.Delete) : FeedOptionsState
}

internal fun UIFeed.toFeedOptionsTarget(includePin: Boolean = true) = FeedOptionsTarget(
    feedId = id,
    items = buildList {
        if (includePin) add(FeedOptionsMenuItem.Pin(pinned))
        add(FeedOptionsMenuItem.Share(title = title, sourceUrl = sourceUrl))
        add(FeedOptionsMenuItem.Delete(title))
    }.toPersistentList(),
)

internal fun FeedOptionsState?.dismissMenu(feedId: UUID): FeedOptionsState? =
    if (this is FeedOptionsState.Open && target.feedId == feedId) null else this
