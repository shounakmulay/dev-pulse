package dev.shounakmulay.devpulse.core.domain.settings.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.FeedSettingsRepository
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveFeedPostListItemVariantUseCase(
    private val feedSettingsRepository: FeedSettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(
        defaultVariant: FeedsPostListItemVariant = FeedsPostListItemVariant.DEFAULT
    ): Flow<Result<FeedsPostListItemVariant>> {
        return feedSettingsRepository.observeFeedPostListItemVariant(defaultVariant)
            .flowCachingOnDefault(dispatcherProvider)
    }
}
