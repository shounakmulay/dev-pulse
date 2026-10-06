package dev.shounakmulay.devpulse.core.domain.settings.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.FeedSettingsRepository
import org.koin.core.annotation.Factory

@Factory
class SetSyncInBackgroundUseCase(
    private val feedSettingsRepository: FeedSettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> =
        dispatcherProvider.runCatchingOnDefault {
            feedSettingsRepository.setSyncInBackground(enabled)
        }
}
