package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface ArticleSettingsEvent : ScreenEvent {
    data class SetContentTextScale(val scale: Float) : ArticleSettingsEvent
    data class SetContentLineHeightScale(val scale: Float) : ArticleSettingsEvent
}
