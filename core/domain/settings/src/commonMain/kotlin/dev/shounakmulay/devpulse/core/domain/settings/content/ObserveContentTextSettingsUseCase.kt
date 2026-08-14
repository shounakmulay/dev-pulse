package dev.shounakmulay.devpulse.core.domain.settings.content

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.ContentSettingsRepository
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.ContentTextSettings
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveContentTextSettingsUseCase(
    private val contentSettingsRepository: ContentSettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(
        defaultSettings: ContentTextSettings = ContentTextSettings(
            textScale = 1f,
            lineHeightScale = 1f
        )
    ): Flow<Result<ContentTextSettings>> {
        return contentSettingsRepository.observeContentTextSettings(defaultSettings)
            .flowCachingOnDefault(dispatcherProvider)
    }
}
