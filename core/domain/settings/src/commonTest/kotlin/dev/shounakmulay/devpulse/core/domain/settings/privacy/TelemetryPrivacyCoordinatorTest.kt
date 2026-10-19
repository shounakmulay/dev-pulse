package dev.shounakmulay.devpulse.core.domain.settings.privacy

import dev.shounakmulay.devpulse.core.data.settings.TelemetryConsentRepository
import dev.shounakmulay.devpulse.core.telemetry.api.ConsentApplyReport
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryCollectionPolicy
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryConsent
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryControl
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryPurpose
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TelemetryPrivacyCoordinatorTest {
    @Test
    fun `Given a saved grant When initializing Then only that purpose is enabled`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent(analytics = true))
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)
        assertEquals(TelemetryPrivacyState(), coordinator.state.value)

        coordinator.initialize().getOrThrow()

        assertEquals(
            TelemetryPrivacyState(analytics = true, diagnostics = false, status = TelemetryPrivacyStatus.Ready),
            coordinator.state.value,
        )
        assertEquals(TelemetryConsent(analytics = true), control.state.value.consent)
    }

    @Test
    fun `Given failed revocation persistence When retrying Then old saved grants cannot overwrite the pause`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent(analytics = true, diagnostics = true))
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)
        coordinator.initialize().getOrThrow()
        repository.beforeSave = {
            assertEquals(TelemetryConsent(diagnostics = true), control.state.value.consent)
        }
        repository.saveFailure = IllegalStateException("disk full")

        assertTrue(coordinator.setConsent(TelemetryPurpose.Analytics, false).isFailure)
        assertEquals(TelemetryPrivacyStatus.SaveFailed, coordinator.state.value.status)
        assertEquals(TelemetryConsent(analytics = true, diagnostics = true), repository.saved)
        assertEquals(TelemetryConsent(diagnostics = true), control.state.value.consent)
        coordinator.initialize().getOrThrow()
        assertEquals(TelemetryConsent(diagnostics = true), control.state.value.consent)

        val restartedControl = RecordingControl()
        TelemetryPrivacyCoordinator(repository, restartedControl).initialize().getOrThrow()
        assertEquals(
            TelemetryConsent(analytics = true, diagnostics = true),
            restartedControl.state.value.consent,
        )
        repository.saveFailure = null
        coordinator.retry().getOrThrow()
        assertEquals(TelemetryConsent(diagnostics = true), repository.saved)
        assertEquals(
            TelemetryPrivacyState(analytics = false, diagnostics = true, status = TelemetryPrivacyStatus.Ready),
            coordinator.state.value,
        )
    }

    @Test
    fun `Given a failed grant write When saving Then collection waits for a successful retry`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent())
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)
        coordinator.initialize().getOrThrow()
        repository.saveFailure = IllegalStateException("disk full")

        assertTrue(coordinator.setConsent(TelemetryPurpose.Diagnostics, true).isFailure)
        assertEquals(TelemetryConsent(), control.state.value.consent)
        assertEquals(TelemetryPrivacyStatus.SaveFailed, coordinator.state.value.status)

        repository.saveFailure = null
        coordinator.retry().getOrThrow()
        assertEquals(TelemetryConsent(diagnostics = true), control.state.value.consent)
        assertEquals(TelemetryPrivacyStatus.Ready, coordinator.state.value.status)
    }

    @Test
    fun `Given storage load failure When retrying Then sharing stays closed until the saved choice loads`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent(analytics = true))
        repository.loadFailure = IllegalStateException("unreadable")
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)

        assertTrue(coordinator.initialize().isFailure)
        assertEquals(TelemetryConsent(), control.state.value.consent)
        assertEquals(TelemetryPrivacyStatus.LoadFailed, coordinator.state.value.status)

        repository.loadFailure = null
        coordinator.retry().getOrThrow()
        assertEquals(TelemetryConsent(analytics = true), control.state.value.consent)
        assertEquals(TelemetryPrivacyStatus.Ready, coordinator.state.value.status)
    }

    @Test
    fun `Given simultaneous purpose changes When writes serialize Then neither grant is lost`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent())
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)
        coordinator.initialize().getOrThrow()
        val saving = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        repository.beforeSave = {
            saving.complete(Unit)
            release.await()
        }

        val analytics = async { coordinator.setConsent(TelemetryPurpose.Analytics, true) }
        saving.await()
        val diagnostics = async { coordinator.setConsent(TelemetryPurpose.Diagnostics, true) }
        release.complete(Unit)
        analytics.await().getOrThrow()
        diagnostics.await().getOrThrow()

        assertEquals(TelemetryConsent(analytics = true, diagnostics = true), repository.saved)
        assertEquals(TelemetryConsent(analytics = true, diagnostics = true), control.state.value.consent)
    }

    @Test
    fun `Given cancellation during revocation save When retrying Then cancellation propagates and pause survives`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent(analytics = true))
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)
        coordinator.initialize().getOrThrow()
        repository.saveCancellation = true

        assertFailsWith<CancellationException> {
            coordinator.setConsent(TelemetryPurpose.Analytics, false)
        }
        assertEquals(TelemetryConsent(), control.state.value.consent)
        assertEquals(TelemetryPrivacyStatus.SaveFailed, coordinator.state.value.status)

        repository.saveCancellation = false
        coordinator.retry().getOrThrow()
        assertEquals(TelemetryConsent(), repository.saved)
        assertEquals(TelemetryPrivacyStatus.Ready, coordinator.state.value.status)
    }

    @Test
    fun `Given provider control failure When retrying Then saved choice is reapplied without reloading`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent())
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)
        coordinator.initialize().getOrThrow()
        control.applyFails = true

        assertTrue(coordinator.setConsent(TelemetryPurpose.Analytics, true).isFailure)
        assertEquals(TelemetryConsent(analytics = true), repository.saved)
        assertEquals(TelemetryPrivacyStatus.ApplyFailed, coordinator.state.value.status)

        control.applyFails = false
        coordinator.retry().getOrThrow()
        assertEquals(TelemetryConsent(analytics = true), control.state.value.consent)
        assertEquals(TelemetryPrivacyStatus.Ready, coordinator.state.value.status)
    }

    @Test
    fun `Given provider controls failed When withdrawing Then saved consent and admission close`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent())
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)
        coordinator.initialize().getOrThrow()
        control.applyFails = true
        assertTrue(coordinator.setConsent(TelemetryPurpose.Analytics, true).isFailure)

        assertTrue(coordinator.setConsent(TelemetryPurpose.Analytics, false).isFailure)

        assertEquals(TelemetryConsent(), repository.saved)
        assertEquals(TelemetryConsent(), control.state.value.consent)
        assertEquals(TelemetryPrivacyStatus.ApplyFailed, coordinator.state.value.status)
    }

    @Test
    fun `Given failed pending grant When withdrawing another purpose Then the pending grant stays off`() = runTest {
        val repository = MemoryConsentRepository(TelemetryConsent(diagnostics = true))
        val control = RecordingControl()
        val coordinator = TelemetryPrivacyCoordinator(repository, control)
        coordinator.initialize().getOrThrow()
        repository.saveFailure = IllegalStateException("disk full")
        assertTrue(coordinator.setConsent(TelemetryPurpose.Analytics, true).isFailure)

        assertTrue(coordinator.setConsent(TelemetryPurpose.Diagnostics, false).isFailure)
        assertEquals(TelemetryConsent(), control.state.value.consent)
        repository.saveFailure = null
        coordinator.retry().getOrThrow()

        assertEquals(TelemetryConsent(), repository.saved)
        assertEquals(TelemetryConsent(), control.state.value.consent)
        assertEquals(TelemetryPrivacyStatus.Ready, coordinator.state.value.status)
    }

    private class MemoryConsentRepository(initial: TelemetryConsent) : TelemetryConsentRepository {
        var saved = initial
        var saveFailure: Exception? = null
        var loadFailure: Exception? = null
        var saveCancellation = false
        var beforeSave: suspend () -> Unit = {}

        override suspend fun load(): Result<TelemetryConsent> =
            loadFailure?.let { Result.failure(it) } ?: Result.success(saved)

        override suspend fun save(consent: TelemetryConsent): Result<Unit> {
            beforeSave()
            if (saveCancellation) throw CancellationException("canceled")
            saveFailure?.let { return Result.failure(it) }
            saved = consent
            return Result.success(Unit)
        }
    }

    private class RecordingControl : TelemetryControl {
        private val mutableState = MutableStateFlow(TelemetryState())
        override val state: StateFlow<TelemetryState> = mutableState
        var applyFails = false

        override suspend fun applyConsent(consent: TelemetryConsent): ConsentApplyReport {
            mutableState.value = mutableState.value.copy(consent = consent)
            return ConsentApplyReport(
                applied = !applyFails,
                failedProviders = if (applyFails) setOf("test-provider") else emptySet(),
            )
        }

        override suspend fun updatePolicy(policy: TelemetryCollectionPolicy): ConsentApplyReport {
            mutableState.value = mutableState.value.copy(collectionPolicy = policy)
            return ConsentApplyReport(applied = true, failedProviders = emptySet())
        }

        override suspend fun pause(purpose: TelemetryPurpose): ConsentApplyReport {
            val consent = mutableState.value.consent
            mutableState.value = mutableState.value.copy(
                consent = when (purpose) {
                    TelemetryPurpose.Analytics -> consent.copy(analytics = false)
                    TelemetryPurpose.Diagnostics -> consent.copy(diagnostics = false)
                },
            )
            return ConsentApplyReport(applied = true, failedProviders = emptySet())
        }
    }
}
