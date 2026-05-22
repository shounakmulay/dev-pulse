package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeSettings
import dev.shounakmulay.devpulse.core.domain.settings.ObserveThemeSettingsUseCase
import dev.shounakmulay.devpulse.core.domain.settings.SetThemeSettingsUseCase
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val observeThemeSettingsUseCase: ObserveThemeSettingsUseCase,
    private val setThemeSettingsUseCase: SetThemeSettingsUseCase,
    logger: DPLogger
) : MviViewModel<SettingsScreenState, SettingsScreenEffect>(),
    EventHandler<SettingsScreenEvent> {
    private val logger = logger.withTag(Tag)

    init {
        logger.d { "SettingsViewModel created" }
        observeThemeSettingsUseCase()
            .onEachSuccess { themeSettings ->
                if (themeSettings != null) {
                    setState {
                        copy(
                            themeMode = themeSettings.mode,
                            isBlackMode = themeSettings.blackMode
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState() = SettingsScreenState()

    override fun createStateSerializer() = SettingsScreenState.serializer()

    override fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.OnThemeModeSelected -> updateThemeMode(event.themeMode)
            is SettingsScreenEvent.OnBlackModeToggled -> toggleBlackMode(event.value)
            SettingsScreenEvent.OnDesignSystemBoardClicked -> navigateToDesignSystemBoard()
        }
    }

    private fun toggleBlackMode(value: Boolean) {
        val currentState = state.value
        if (currentState.themeMode == ThemeMode.LIGHT) {
            logger.w {
                "Black mode change rejected reason=themeModeDoesNotSupportBlackMode requested=$value currentMode=${currentState.themeMode}"
            }
            return
        }
        logger.d {
            "Black mode change requested value=$value currentMode=${currentState.themeMode}"
        }
        viewModelScope.launch {
            setThemeSettingsUseCase(
                ThemeSettings(
                    mode = currentState.themeMode,
                    blackMode = value
                )
            ).onSuccess {
                logger.d {
                    "Black mode change persisted value=$value currentMode=${currentState.themeMode}"
                }
            }.onFailure {
                logger.e(it) {
                    "Black mode change failed value=$value currentMode=${currentState.themeMode}; rolling back"
                }
            }
        }
    }

    private fun updateThemeMode(themeMode: ThemeMode) {
        val currentState = state.value
        logger.d {
            "Theme mode change requested value=$themeMode currentMode=${currentState.themeMode} blackMode=${currentState.isBlackMode}"
        }
        viewModelScope.launch {
            setThemeSettingsUseCase(
                ThemeSettings(
                    mode = themeMode,
                    blackMode = currentState.isBlackMode
                )
            ).onSuccess {
                logger.d {
                    "Theme mode change persisted value=$themeMode blackMode=${currentState.isBlackMode}"
                }
            }.onFailure {
                logger.e(it) {
                    "Theme mode change failed value=$themeMode blackMode=${currentState.isBlackMode}; rolling back"
                }
            }
        }
    }

    private fun navigateToDesignSystemBoard() =
        postEffect(SettingsScreenEffect.NavigateToDesignSystemBoard)

    override fun onCleared() {
        logger.d { "SettingsViewModel cleared" }
        super.onCleared()
    }

    private companion object {
        const val Tag = "SettingsViewModel"
    }
}
