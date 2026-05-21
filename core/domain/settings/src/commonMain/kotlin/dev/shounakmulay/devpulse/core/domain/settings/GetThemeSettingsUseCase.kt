package dev.shounakmulay.devpulse.core.domain.settings

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.ThemeRepository
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeSettings
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

