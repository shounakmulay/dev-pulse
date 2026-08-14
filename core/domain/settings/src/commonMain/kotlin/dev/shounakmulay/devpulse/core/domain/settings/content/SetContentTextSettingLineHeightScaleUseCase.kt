package dev.shounakmulay.devpulse.core.domain.settings.content

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.ContentSettingsRepository
import org.koin.core.annotation.Factory

@Factory
class SetContentTextSettingLineHeightScaleUseCase(
    private val contentSettingsRepository: ContentSettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(scale: Float) = dispatcherProvider.runCatchingOnDefault {
        require(scale in 1f..1.5f) {
            "Scale must be between 1 and 2"
        }
        contentSettingsRepository.setContentLineHeightScale(scale)
    }
}