package dev.shounakmulay.devpulse.core.domain.settings.content

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.ContentSettingsRepository
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class ObserveBrowserOpeningModeUseCase(
    private val repository: ContentSettingsRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    operator fun invoke(): Flow<Result<BrowserOpeningMode>> = flow {
        emitAll(repository.observeBrowserOpeningMode(BrowserOpeningMode.IN_APP_BROWSER))
    }.flowCachingOnDefault(dispatcherProvider)
}
