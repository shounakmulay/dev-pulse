package dev.shounakmulay.devpulse.core.domain.settings.content

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCachingOnIO
import dev.shounakmulay.devpulse.core.data.settings.ContentSettingsRepository
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class GetBrowserOpeningTargetUseCase(
    private val repository: ContentSettingsRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(url: String): Result<BrowserOpeningTarget> = dispatcherProvider.runCachingOnIO {
        when (repository.observeBrowserOpeningMode(BrowserOpeningMode.IN_APP_BROWSER).first()) {
            BrowserOpeningMode.IN_APP_BROWSER -> BrowserOpeningTarget.InApp(url)
            BrowserOpeningMode.EXTERNAL_BROWSER -> BrowserOpeningTarget.External(url)
        }
    }
}
