package dev.shounakmulay.devpulse.feature.feed.components.feedOptions

import dev.shounakmulay.devpulse.core.domain.feed.feed.DeleteFeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import devpulse.core.resources.generated.resources.failed_to_delete_feed
import devpulse.core.resources.generated.resources.failed_to_update_feed
import devpulse.core.resources.generated.resources.feed_deleted
import org.koin.core.annotation.Factory

interface FeedOptionsMenuProcessor {
    suspend fun processFeedOption(
        feedId: UUID,
        option: FeedOptionsMenuItem,
        onShare: (TextResource) -> Unit,
        onShowToast: (TextResource) -> Unit,
        onFeedDeleted: (UUID) -> Unit,
    )
}

@Factory
internal class FeedOptionsMenuProcessorDelegate(
    private val deleteFeedUseCase: DeleteFeedUseCase,
    private val setPinFeedPinnedUseCase: SetFeedPinnedUseCase,
) : FeedOptionsMenuProcessor {
    override suspend fun processFeedOption(
        feedId: UUID,
        option: FeedOptionsMenuItem,
        onShare: (TextResource) -> Unit,
        onShowToast: (TextResource) -> Unit,
        onFeedDeleted: (UUID) -> Unit,
    ) {
        when (option) {
            is FeedOptionsMenuItem.Delete -> onDeleteFeed(
                feedId = feedId,
                onShowToast = onShowToast,
                onFeedDeleted = onFeedDeleted,
            )

            is FeedOptionsMenuItem.Pin -> setPinFeedPinnedUseCase(feedId, !option.pinned)
                .onFailure {
                    onShowToast(TextResource.fromStringRes(stringRes.failed_to_update_feed))
                }

            is FeedOptionsMenuItem.Share -> {
                val shareText = TextResource.fromText("${option.title}: ${option.sourceUrl}")
                onShare(shareText)
            }
        }
    }

    private suspend fun onDeleteFeed(
        feedId: UUID,
        onShowToast: (TextResource) -> Unit,
        onFeedDeleted: (UUID) -> Unit,
    ) {
        val result = deleteFeedUseCase(feedId)

        result.fold(
            onSuccess = {
                onFeedDeleted(feedId)
                onShowToast(
                    TextResource.fromStringRes(stringRes.feed_deleted),
                )
            },
            onFailure = {
                onShowToast(
                    TextResource.fromStringRes(stringRes.failed_to_delete_feed),
                )
            },
        )
    }
}
