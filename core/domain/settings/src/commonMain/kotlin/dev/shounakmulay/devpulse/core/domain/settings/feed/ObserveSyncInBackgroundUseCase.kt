package dev.shounakmulay.devpulse.core.domain.settings.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.FeedSettingsRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveSyncInBackgroundUseCase(
    private val feedSettingsRepository: FeedSettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(defaultValue: Boolean = true): Flow<Result<Boolean>> {
        return feedSettingsRepository.observeSyncInBackground(defaultValue)
            .flowCachingOnDefault(dispatcherProvider)
    }
}
