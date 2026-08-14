package dev.shounakmulay.devpulse.feature.settings.controllers.contentText

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.common.extensions.mapToSuccessNotNull
import dev.shounakmulay.devpulse.core.domain.settings.content.ObserveContentTextSettingsUseCase
import dev.shounakmulay.devpulse.core.ui.content.UIContentTextSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ContentTextSettingsViewModel(
    observeContentTextSettingsUseCase: ObserveContentTextSettingsUseCase
) : ViewModel() {

    val state = observeContentTextSettingsUseCase()
        .mapToSuccessNotNull()
        .map {
            UIContentTextSettings(
                textScale = it.textScale,
                lineHeightScale = it.lineHeightScale
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UIContentTextSettings()
        )
}