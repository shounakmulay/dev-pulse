package dev.shounakmulay.devpulse.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.designsystem.theme.blackScheme
import dev.shounakmulay.devpulse.core.designsystem.theme.darkScheme
import dev.shounakmulay.devpulse.core.designsystem.theme.lightScheme
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.domain.settings.ObserveThemeSettingsUseCase
import dev.shounakmulay.devpulse.core.logging.DPLogger
import kotlinx.coroutines.flow.launchIn
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ThemeSettingsViewModel(
    observeThemeSettingsUseCase: ObserveThemeSettingsUseCase,
    logger: DPLogger
) : ViewModel() {
    private val logger = logger.withTag(Tag)
    private val _state = mutableStateOf(ThemeSettingsState())
    val state: State<ThemeSettingsState> = _state


    init {
        logger.d { "ThemeSettingsViewModel created" }
        observeThemeSettingsUseCase()
            .onEachSuccess { themeSettings ->
                if (themeSettings != null) {
                    _state.value = _state.value.copy(
                        themeMode = themeSettings.mode,
                        isBlackMode = themeSettings.blackMode,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSystemDarkTheme(isSystemInDarkTheme: Boolean) {
        _state.value = _state.value.copy(
           isSystemInDarkTheme = isSystemInDarkTheme
        )
    }

    fun getColorScheme(state: ThemeSettingsState, isSystemInDarkTheme: Boolean): ColorScheme {
        return when (state.themeMode) {
            ThemeMode.LIGHT -> lightScheme
            ThemeMode.DARK -> getDarkScheme(state.isBlackMode)
            ThemeMode.SYSTEM -> if (isSystemInDarkTheme) {
                getDarkScheme(state.isBlackMode)
            } else {
                lightScheme
            }
        }
    }

    private fun getDarkScheme(blackMode: Boolean): ColorScheme {
        return if (blackMode) blackScheme else darkScheme
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
            current.isBlackMode != next.isBlackMode
        ) {
            logger.d {
                "Effective theme changed mode=${next.themeMode}  blackMode=${next.isBlackMode}"
            }
        }
    }

    private companion object {
        const val Tag = "ThemeSettingsViewModel"
    }
}
