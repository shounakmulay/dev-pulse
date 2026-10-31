package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetFeedPinnedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.settings.telemetry.ObserveTelemetryConsentUseCase
import dev.shounakmulay.devpulse.core.domain.settings.telemetry.SetTelemetryConsentUseCase
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuProcessor
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.dismissMenu
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.toFeedOptionsTarget
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import devpulse.core.resources.generated.resources.telemetry_error
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.orbitmvi.orbit.syntax.Syntax

@KoinViewModel
class FeedViewModel(
    private val feedInteractor: FeedInteractor,
    private val postInteractor: PostInteractor,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase,
    private val setFeedPinnedUseCase: SetFeedPinnedUseCase,
    observeTelemetryConsentUseCase: ObserveTelemetryConsentUseCase,
    private val setTelemetryConsentUseCase: SetTelemetryConsentUseCase,
    private val feedOptionsMenuProcessor: FeedOptionsMenuProcessor,
) : MviViewModel<FeedScreenState, FeedScreenEffect>(FeedScreenState()),
    EventHandler<FeedScreenEvent> {

    init {
        viewModelScope.launch {
            observeTelemetryConsentUseCase().collect { result ->
                result.onSuccess { consent ->
                    setState { copy(telemetryConsent = consent, isTelemetryConsentLoaded = true) }
                }.onFailure {
                    postEffect(FeedScreenEffect.ShowToast(TextResource.fromStringRes(stringRes.telemetry_error)))
                }
            }
        }
    }

    private fun setAnalyticsEnabled(event: FeedScreenEvent.SetAnalyticsEnabled) = intent {
        if (state.isSavingTelemetryConsent || !state.isTelemetryConsentLoaded) return@intent
        setState { copy(isSavingTelemetryConsent = true) }
        val consent = (state.telemetryConsent ?: DPAnalyticsConsent()).copy(
            usageAnalytics = event.enabled,
            performanceMetrics = event.enabled,
        )
        setTelemetryConsentUseCase(consent).onFailure {
            postEffect(FeedScreenEffect.ShowToast(TextResource.fromStringRes(stringRes.telemetry_error)))
        }
        setState { copy(isSavingTelemetryConsent = false) }
    }

    override fun createStateSerializer() = FeedScreenState.serializer()

    val pinnedAndRecentFeeds = feedInteractor
        .getPinnedAndRecentsUIFeedFlow()
        .onEach {
            setState {
                copy(
                    isFeedLoading = false,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = persistentListOf(),
        )

    val recentArticles = postInteractor
        .getRecentArticlesFlow()
        .onEach {
            setState {
                copy(
                    isArticlesLoading = false,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = persistentListOf(),
        )

    private fun setPostRead(postId: UUID, isRead: Boolean) {
        intent {
            postInteractor.setPostRead(postId = postId, isRead = isRead)
        }
    }

    override fun onEvent(event: FeedScreenEvent) {
        when (event) {
            is FeedScreenEvent.OpenInBrowser -> intent {
                postEffect(FeedScreenEffect.OpenInBrowser(postInteractor.getBrowserOpeningTarget(event.url)))
            }

            is FeedScreenEvent.SetAnalyticsEnabled -> setAnalyticsEnabled(event)

            is FeedScreenEvent.SetPostRead -> setPostRead(event.postId, event.isRead)

            FeedScreenEvent.ConfirmDelete -> confirmDelete()

            FeedScreenEvent.DismissDelete -> intent {
                if (state.feedOptions is FeedOptionsState.ConfirmingDelete) {
                    setState { copy(feedOptions = null) }
                }
            }

            is FeedScreenEvent.OnFeedOptionSelected -> selectFeedOption(event.feedId, event.menuItem)

            is FeedScreenEvent.OnFeedLongClick -> showFeedOptions(event.feed)

            is FeedScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked,
            )

            is FeedScreenEvent.OnFeedPinChanged -> onFeedPinChanged(
                feedId = event.feedId,
                pinned = event.pinned,
            )

            is FeedScreenEvent.OnShowFeedOptions -> showFeedOptions(event.feed)

            is FeedScreenEvent.HideFeedOptions -> dismissFeedOptions(event.feedId)
        }
    }

    private fun onPostBookmarkChanged(postId: UUID, bookmarked: Boolean) {
        viewModelScope.launch {
            setPostBookmarkedUseCase(id = postId, bookmarked = bookmarked)
        }
    }

    private fun onFeedPinChanged(feedId: UUID, pinned: Boolean) {
        viewModelScope.launch {
            setFeedPinnedUseCase(id = feedId, pinned = pinned)
        }
    }

    private fun showFeedOptions(feed: UIFeed) = intent {
        setState { copy(feedOptions = FeedOptionsState.Open(feed.toFeedOptionsTarget())) }
    }

    private fun dismissFeedOptions(feedId: UUID) = intent {
        setState { copy(feedOptions = feedOptions.dismissMenu(feedId)) }
    }

    private fun selectFeedOption(feedId: UUID, option: FeedOptionsMenuItem) = intent {
        val open = state.feedOptions as? FeedOptionsState.Open ?: return@intent
        if (open.target.feedId != feedId || option !in open.target.items) return@intent
        if (option is FeedOptionsMenuItem.Delete) {
            setState { copy(feedOptions = FeedOptionsState.ConfirmingDelete(feedId, option)) }
        } else {
            processFeedOption(feedId = feedId, option = option)
        }
    }

    private fun confirmDelete() = intent {
        val confirmation = state.feedOptions as? FeedOptionsState.ConfirmingDelete ?: return@intent
        processFeedOption(feedId = confirmation.feedId, option = confirmation.option)
    }

    private suspend fun Syntax<FeedScreenState, FeedScreenEffect>.processFeedOption(
        feedId: UUID,
        option: FeedOptionsMenuItem,
    ) {
        setState { copy(feedOptions = null) }
        feedOptionsMenuProcessor.processFeedOption(
            feedId = feedId,
            option = option,
            onShare = { postEffect(FeedScreenEffect.Share(it)) },
            onShowToast = { postEffect(FeedScreenEffect.ShowToast(it)) },
            onFeedDeleted = { postEffect(FeedScreenEffect.FeedDeleted(it)) },
        )
    }
}
