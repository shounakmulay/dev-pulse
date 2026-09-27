package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import dev.shounakmulay.devpulse.core.domain.settings.content.SetContentTextSettingFontScaleUseCase
import dev.shounakmulay.devpulse.core.domain.settings.content.SetContentTextSettingLineHeightScaleUseCase
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ArticleSettingsViewModel(
    private val setContentTextSettingFontScaleUseCase: SetContentTextSettingFontScaleUseCase,
    private val setContentLineHeightSettingUseCase: SetContentTextSettingLineHeightScaleUseCase
) : MviViewModel<ArticleSettingsState, ArticleSettingsEffect>(
    initialState = ArticleSettingsState()
), EventHandler<ArticleSettingsEvent> {
    override fun createStateSerializer() = ArticleSettingsState.serializer()

    override fun onEvent(event: ArticleSettingsEvent) {
        when (event) {
            is ArticleSettingsEvent.SetContentLineHeightScale -> setContentLineHeightScale(event.scale)
            is ArticleSettingsEvent.SetContentTextScale -> setContentTextScale(event.scale)
        }
    }

    private fun setContentTextScale(scale: Float) = intent {
        setContentTextSettingFontScaleUseCase(scale)
    }

    private fun setContentLineHeightScale(scale: Float) = intent {
        setContentLineHeightSettingUseCase(scale)
    }
}