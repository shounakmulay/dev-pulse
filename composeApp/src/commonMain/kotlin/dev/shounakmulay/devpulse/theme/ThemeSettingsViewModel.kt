package dev.shounakmulay.devpulse.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.settings.ObserveThemeSettingsUseCase
import dev.shounakmulay.devpulse.core.logging.DPLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ThemeSettingsViewModel(
    observeThemeSettingsUseCase: ObserveThemeSettingsUseCase,
    logger: DPLogger
) : ViewModel() {
    private val logger = logger.withTag(Tag)
    private val _state = MutableStateFlow(ThemeSettingsState())
    val state: StateFlow<ThemeSettingsState> = _state.asStateFlow()

    private val systemDarkTheme = MutableStateFlow(false)

    init {
        logger.d { "ThemeSettingsViewModel created" }
        observeThemeSettingsUseCase()
            .onEachSuccess { themeSettings ->
                if (themeSettings != null) {
                    _state.update { current ->
                        val next = current.copy(
                            themeMode = themeSettings.mode,
                            isBlackMode = themeSettings.blackMode,
                            darkTheme = themeSettings.mode.resolveDarkTheme(systemDarkTheme.value)
                        )
                        logIfEffectiveThemeChanged(current, next)
                        next
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSystemDarkTheme(isSystemInDarkTheme: Boolean) {
        systemDarkTheme.value = isSystemInDarkTheme
        _state.update { current ->
            val next = current.copy(darkTheme = current.themeMode.resolveDarkTheme(isSystemInDarkTheme))
            logIfEffectiveThemeChanged(current, next)
            next
        }
    }

    override fun onCleared() {
        logger.d { "ThemeSettingsViewModel cleared" }
        super.onCleared()
    }

    private fun logIfEffectiveThemeChanged(
        current: ThemeSettingsState,
        next: ThemeSettingsState
    ) {
        if (
            current.themeMode != next.themeMode ||
            current.isBlackMode != next.isBlackMode ||
            current.darkTheme != next.darkTheme
        ) {
            logger.d {
                "Effective theme changed mode=${next.themeMode} darkTheme=${next.darkTheme} blackMode=${next.isBlackMode}"
            }
        }
    }

    private companion object {
        const val Tag = "ThemeSettingsViewModel"
    }
}
