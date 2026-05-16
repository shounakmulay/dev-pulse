package dev.shounakmulay.devpulse.core.domain.settings

import dev.shounakmulay.devpulse.core.common.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.flowCachingOnDefault
import dev.shounakmulay.devpulse.core.data.settings.ThemeRepository
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeSettings
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveThemeSettingsUseCase(
    private val themeRepository: ThemeRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    operator fun invoke(): Flow<Result<ThemeSettings>> {
        return themeRepository.observeThemeSettings(
            fallbackThemeMode = ThemeMode.DEFAULT,
            fallbackBlackMode = false
        ).flowCachingOnDefault(dispatcherProvider)
    }
}