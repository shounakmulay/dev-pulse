package dev.shounakmulay.devpulse.core.domain.settings.privacy

import dev.shounakmulay.devpulse.core.data.settings.TelemetryConsentRepository
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryConsent
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryControl
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryPurpose
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single
class TelemetryPrivacyCoordinator(
    private val repository: TelemetryConsentRepository,
    private val telemetryControl: TelemetryControl,
) {
    private val mutex = Mutex()
    private val mutableState = MutableStateFlow(TelemetryPrivacyState())
    val state: StateFlow<TelemetryPrivacyState> = mutableState.asStateFlow()
    private var initialized = false
    private var pendingConsent: TelemetryConsent? = null

    suspend fun initialize(): Result<Unit> = mutex.withLock {
        if (initialized) Result.success(Unit) else load()
    }

    suspend fun setConsent(purpose: TelemetryPurpose, enabled: Boolean): Result<Unit> =
        mutex.withLock {
            if (!state.value.canChangeConsent && (enabled || !state.value.canWithdrawConsent)) {
                return@withLock Result.failure(IllegalStateException("Privacy settings are unavailable"))
            }
            val current = telemetryControl.state.value.consent
            if (current.allows(purpose) == enabled) return@withLock Result.success(Unit)
            val desired = when (purpose) {
                TelemetryPurpose.Analytics -> current.copy(analytics = enabled)
                TelemetryPurpose.Diagnostics -> current.copy(diagnostics = enabled)
            }
            pendingConsent = desired
            publish(TelemetryPrivacyStatus.Saving)
            if (!enabled) {
                try {
                    telemetryControl.pause(purpose)
                } catch (cancellation: CancellationException) {
                    publish(TelemetryPrivacyStatus.ApplyFailed)
                    throw cancellation
                } catch (failure: Exception) {
                    publish(TelemetryPrivacyStatus.ApplyFailed)
                    return@withLock Result.failure(failure)
                }
            }
            persistAndApply(desired)
        }

    suspend fun retry(): Result<Unit> = mutex.withLock {
        val pending = pendingConsent
        when {
            pending != null -> persistAndApply(pending)
            !initialized -> load()
            else -> Result.success(Unit)
        }
    }

    private suspend fun load(): Result<Unit> {
        publish(TelemetryPrivacyStatus.Loading)
        val result = try {
            repository.load()
        } catch (cancellation: CancellationException) {
            publish(TelemetryPrivacyStatus.LoadFailed)
            throw cancellation
        }
        return result.fold(
            onSuccess = { consent ->
                initialized = true
                pendingConsent = consent
                apply(consent)
            },
            onFailure = { failure ->
                publish(TelemetryPrivacyStatus.LoadFailed)
                Result.failure(failure)
            },
        )
    }

    private suspend fun persistAndApply(consent: TelemetryConsent): Result<Unit> {
        publish(TelemetryPrivacyStatus.Saving)
        val result = try {
            repository.save(consent)
        } catch (cancellation: CancellationException) {
            publish(TelemetryPrivacyStatus.SaveFailed)
            throw cancellation
        }
        return result.fold(
            onSuccess = { apply(consent) },
            onFailure = { failure ->
                publish(TelemetryPrivacyStatus.SaveFailed)
                Result.failure(failure)
            },
        )
    }

    private suspend fun apply(consent: TelemetryConsent): Result<Unit> = try {
        val report = telemetryControl.applyConsent(consent)
        when {
            !report.applied || report.failedProviders.isNotEmpty() -> {
                publish(TelemetryPrivacyStatus.ApplyFailed)
                Result.failure(IllegalStateException("Telemetry controls could not finish updating"))
            }
            else -> {
                pendingConsent = null
                publish(TelemetryPrivacyStatus.Ready)
                Result.success(Unit)
            }
        }
    } catch (cancellation: CancellationException) {
        publish(TelemetryPrivacyStatus.ApplyFailed)
        throw cancellation
    } catch (failure: Exception) {
        publish(TelemetryPrivacyStatus.ApplyFailed)
        Result.failure(failure)
    }

    private fun publish(status: TelemetryPrivacyStatus) {
        val consent = telemetryControl.state.value.consent
        mutableState.value = TelemetryPrivacyState(
            analytics = consent.analytics,
            diagnostics = consent.diagnostics,
            status = status,
        )
    }
}
