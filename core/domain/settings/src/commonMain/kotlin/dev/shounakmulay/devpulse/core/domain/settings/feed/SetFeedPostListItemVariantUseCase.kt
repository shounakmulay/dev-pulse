package dev.shounakmulay.devpulse.core.domain.settings.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.FeedSettingsRepository
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import org.koin.core.annotation.Factory

@Factory
class SetFeedPostListItemVariantUseCase(
    private val feedSettingsRepository: FeedSettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(variant: FeedsPostListItemVariant): Result<Unit> =
        dispatcherProvider.runCatchingOnDefault {
            feedSettingsRepository.setFeedPostListItemVariant(variant)
        }
}
