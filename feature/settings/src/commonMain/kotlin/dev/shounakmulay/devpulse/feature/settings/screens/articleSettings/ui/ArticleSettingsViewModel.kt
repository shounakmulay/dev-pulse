package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import dev.shounakmulay.devpulse.core.domain.settings.content.ObserveBrowserOpeningModeUseCase
import dev.shounakmulay.devpulse.core.domain.settings.content.SetBrowserOpeningModeUseCase
import dev.shounakmulay.devpulse.core.domain.settings.content.SetContentTextSettingFontScaleUseCase
import dev.shounakmulay.devpulse.core.domain.settings.content.SetContentTextSettingLineHeightScaleUseCase
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ArticleSettingsViewModel(
    private val setContentTextSettingFontScaleUseCase: SetContentTextSettingFontScaleUseCase,
    private val setContentLineHeightSettingUseCase: SetContentTextSettingLineHeightScaleUseCase,
    private val observeBrowserOpeningModeUseCase: ObserveBrowserOpeningModeUseCase,
    private val setBrowserOpeningModeUseCase: SetBrowserOpeningModeUseCase,
) : MviViewModel<ArticleSettingsState, ArticleSettingsEffect>(
    initialState = ArticleSettingsState(),
),
    EventHandler<ArticleSettingsEvent> {
    init {
        viewModelScope.launch {
            observeBrowserOpeningModeUseCase().collect { result ->
                setState {
                    copy(
                        browserOpeningMode = result.getOrNull() ?: BrowserOpeningMode.EXTERNAL_BROWSER,
                        isLoading = false,
                    )
                }
                result.onFailure {
                    postEffect(ArticleSettingsEffect.BrowserOpeningModeError)
                }
            }
        }
    }

    override fun createStateSerializer() = ArticleSettingsState.serializer()

    override fun onEvent(event: ArticleSettingsEvent) {
        when (event) {
            is ArticleSettingsEvent.SetBrowserOpeningMode -> setBrowserOpeningMode(event.mode)
            is ArticleSettingsEvent.SetContentLineHeightScale -> setContentLineHeightScale(event.scale)
            is ArticleSettingsEvent.SetContentTextScale -> setContentTextScale(event.scale)
        }
    }

    private fun setBrowserOpeningMode(mode: BrowserOpeningMode) = intent {
        if (state.isSavingBrowserOpeningMode || state.isLoading || state.browserOpeningMode == mode) return@intent
        setState { copy(isSavingBrowserOpeningMode = true) }
        setBrowserOpeningModeUseCase(mode).onFailure {
            postEffect(ArticleSettingsEffect.BrowserOpeningModeError)
        }
        setState { copy(isSavingBrowserOpeningMode = false) }
    }

    private fun setContentTextScale(scale: Float) = intent {
        setContentTextSettingFontScaleUseCase(scale)
    }

    private fun setContentLineHeightScale(scale: Float) = intent {
        setContentLineHeightSettingUseCase(scale)
    }
}
