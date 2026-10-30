package dev.shounakmulay.devpulse.core.domain.settings.content

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCachingOnIO
import dev.shounakmulay.devpulse.core.data.settings.ContentSettingsRepository
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import org.koin.core.annotation.Factory

@Factory
class SetBrowserOpeningModeUseCase(
    private val repository: ContentSettingsRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(mode: BrowserOpeningMode): Result<Unit> = dispatcherProvider.runCachingOnIO {
        repository.setBrowserOpeningMode(mode)
    }
}
