package dev.shounakmulay.devpulse.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.settings.ObserveThemeSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ThemeSettingsViewModel(
    observeThemeSettingsUseCase: ObserveThemeSettingsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ThemeSettingsState())
    val state: StateFlow<ThemeSettingsState> = _state.asStateFlow()

    private val systemDarkTheme = MutableStateFlow(false)

    init {
        observeThemeSettingsUseCase()
            .onEachSuccess { themeSettings ->
                if (themeSettings != null) {
                    _state.update { current ->
                        current.copy(
                            themeMode = themeSettings.mode,
                            isBlackMode = themeSettings.blackMode,
                            darkTheme = themeSettings.mode.resolveDarkTheme(systemDarkTheme.value)
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSystemDarkTheme(isSystemInDarkTheme: Boolean) {
        systemDarkTheme.value = isSystemInDarkTheme
        _state.update { current ->
            current.copy(darkTheme = current.themeMode.resolveDarkTheme(isSystemInDarkTheme))
        }
    }
}
