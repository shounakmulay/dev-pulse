package dev.shounakmulay.devpulse.core.domain.settings

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.ThemeRepository
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeSettings
import org.koin.core.annotation.Factory

@Factory
class SetThemeSettingsUseCase(
    private val themeRepository: ThemeRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(themeSettings: ThemeSettings) =
        dispatcherProvider.runCatchingOnDefault {
            themeRepository.setThemeMode(themeSettings.mode)
            themeRepository.setBlackMode(themeSettings.blackMode)
        }
}
