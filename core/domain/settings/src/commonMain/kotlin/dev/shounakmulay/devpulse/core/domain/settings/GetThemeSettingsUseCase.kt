package dev.shounakmulay.devpulse.core.domain.settings

import dev.shounakmulay.devpulse.core.common.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.ThemeRepository
import dev.shounakmulay.devpulse.core.domain.models.ThemeMode
import dev.shounakmulay.devpulse.core.domain.models.ThemeSettings
import org.koin.core.annotation.Factory

@Factory
class GetThemeSettingsUseCase(
    private val themeRepository: ThemeRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke() = dispatcherProvider.runCatchingOnDefault {
        val themeMode = themeRepository.getThemeMode() ?: ThemeMode.DEFAULT
        val blackMode = themeRepository.getBlackMode() ?: false

        ThemeSettings(
            mode = themeMode,
            blackMode = blackMode
        )
    }
}

