package dev.shounakmulay.devpulse.feature.settings.screens.telemetry.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.settings.telemetry.ObserveTelemetryConsentUseCase
import dev.shounakmulay.devpulse.core.domain.settings.telemetry.SetTelemetryConsentUseCase
import dev.shounakmulay.devpulse.core.logging.consent.DPAnalyticsConsent
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class TelemetrySettingsViewModel(
    private val observeTelemetryConsentUseCase: ObserveTelemetryConsentUseCase,
    private val setTelemetryConsentUseCase: SetTelemetryConsentUseCase,
) : MviViewModel<TelemetrySettingsState, Effect>(TelemetrySettingsState()),
    EventHandler<TelemetrySettingsEvent> {
    private var observation: Job? = null

    init {
        observeConsent()
    }

    override fun createStateSerializer() = TelemetrySettingsState.serializer()

    override fun onEvent(event: TelemetrySettingsEvent) {
        when (event) {
            is TelemetrySettingsEvent.SetCrashReporting -> updateConsent { copy(crashReporting = event.enabled) }
            is TelemetrySettingsEvent.SetAnalytics -> updateConsent { copy(usageAnalytics = event.enabled, performanceMetrics = event.enabled) }
            TelemetrySettingsEvent.Retry -> observeConsent()
        }
    }

    private fun observeConsent() {
        observation?.cancel()
        observation = viewModelScope.launch {
            setState { copy(error = false) }
            observeTelemetryConsentUseCase().collect { result ->
                result.onSuccess { consent ->
                    setState { copy(consent = consent ?: DPAnalyticsConsent(), loaded = true, error = false) }
                }.onFailure {
                    setState { copy(error = true) }
                }
            }
        }
    }

    private fun updateConsent(update: DPAnalyticsConsent.() -> DPAnalyticsConsent) = intent {
        if (!state.loaded || state.saving) return@intent
        setState { copy(saving = true, error = false) }
        setTelemetryConsentUseCase(state.consent.update()).onFailure {
            setState { copy(error = true) }
        }
        setState { copy(saving = false) }
    }
}
