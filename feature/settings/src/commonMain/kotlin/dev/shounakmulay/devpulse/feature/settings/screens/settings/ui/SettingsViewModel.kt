package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeSettings
import dev.shounakmulay.devpulse.core.domain.settings.feed.ObserveFeedPostListItemVariantUseCase
import dev.shounakmulay.devpulse.core.domain.settings.feed.ObserveSyncInBackgroundUseCase
import dev.shounakmulay.devpulse.core.domain.settings.feed.SetFeedPostListItemVariantUseCase
import dev.shounakmulay.devpulse.core.domain.settings.feed.SetSyncInBackgroundUseCase
import dev.shounakmulay.devpulse.core.domain.settings.theme.ObserveThemeSettingsUseCase
import dev.shounakmulay.devpulse.core.domain.settings.theme.SetThemeSettingsUseCase
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
    private val observeFeedPostListItemVariantUseCase: ObserveFeedPostListItemVariantUseCase,
    private val setFeedPostListItemVariantUseCase: SetFeedPostListItemVariantUseCase,
    private val observeSyncInBackgroundUseCase: ObserveSyncInBackgroundUseCase,
    private val setSyncInBackgroundUseCase: SetSyncInBackgroundUseCase,
    logger: DPLogger
) : MviViewModel<SettingsScreenState, SettingsScreenEffect>(SettingsScreenState()),
    EventHandler<SettingsScreenEvent> {
    private val logger = logger.withTag(Tag)

    init {
        logger.d { "SettingsViewModel created" }
        observeFeedPostListItemVariantUseCase()
            .onEachSuccess { variant ->
                if (variant != null) {
                    setState { copy(feedPostListItemVariant = variant) }
                }
            }
            .launchIn(viewModelScope)

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

        observeSyncInBackgroundUseCase()
            .onEachSuccess { syncInBackground ->
                if (syncInBackground != null) {
                    setState { copy(syncInBackground = syncInBackground) }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createStateSerializer() = SettingsScreenState.serializer()

    override fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.OnFeedPostListItemVariantSelected -> updateFeedPostListItemVariant(
                event.variant
            )

            is SettingsScreenEvent.OnThemeModeSelected -> updateThemeMode(event.themeMode)
            is SettingsScreenEvent.OnBlackModeToggled -> toggleBlackMode(event.value)
            SettingsScreenEvent.OnDesignSystemBoardClicked -> navigateToDesignSystemBoard()
            SettingsScreenEvent.OnLicensesClicked -> navigateToLicenses()
            SettingsScreenEvent.OnArticleSettingsClicked -> navigateToArticleSettings()
            is SettingsScreenEvent.OnSyncInBackgroundToggled -> toggleSyncInBackground(event.enabled)
        }
    }

    private fun toggleSyncInBackground(enabled: Boolean) {
        intent {
            setSyncInBackgroundUseCase(enabled)
        }
    }

    private fun updateFeedPostListItemVariant(variant: FeedsPostListItemVariant) {
        intent {
            setFeedPostListItemVariantUseCase(variant).onFailure {
                logger.e(it) { "Feed post list item variant change failed value=$variant" }
            }
        }
    }

    private fun navigateToArticleSettings() {
        postEffect(SettingsScreenEffect.NavigateToArticleSettings)
    }

    private fun navigateToLicenses() {
        postEffect(SettingsScreenEffect.NavigateToLicenses)
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
