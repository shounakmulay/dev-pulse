package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import dev.shounakmulay.devpulse.core.ui.effect.Effect

sealed interface ArticleSettingsEffect : Effect {
    data object BrowserOpeningModeError : ArticleSettingsEffect
}
