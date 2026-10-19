package dev.shounakmulay.devpulse.core.telemetry.api

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentSet


enum class TelemetryCapability { Analytics, HandledErrors, Breadcrumbs, ManualSpans, Counters, Histograms }
enum class DeliveryOwnership { ProviderSdk, BestEffortMemory }
enum class DeliveryOutcome { AcceptedBySdk, AcknowledgedByServer, RetryableFailure, PermanentFailure, RejectedByPolicy }

class ProviderDescriptor(
    val id: String,
    capabilities: Set<TelemetryCapability>,
    supportedPlatforms: Set<String>,
    val adapterVersion: String,
    val deliveryOwnership: DeliveryOwnership = DeliveryOwnership.BestEffortMemory,
) {
    val capabilities: PersistentSet<TelemetryCapability> = capabilities.toPersistentSet()
    val supportedPlatforms: PersistentSet<String> = supportedPlatforms.toPersistentSet()
    init { requireSignalName(id); require(adapterVersion.isNotBlank()); require(supportedPlatforms.isNotEmpty()) }
}

data class ProviderPolicy(
    val consent: TelemetryConsent,
    val analyticsGeneration: Long,
    val diagnosticsGeneration: Long,
)

interface TelemetryProvider {
    val descriptor: ProviderDescriptor
    suspend fun initialize(release: AppReleaseContext, policy: ProviderPolicy)
    suspend fun updatePolicy(policy: ProviderPolicy)
    suspend fun deliver(record: CanonicalRecord): DeliveryOutcome
    suspend fun purge(purpose: TelemetryPurpose)
    suspend fun flush(): DeliveryOutcome
    suspend fun close()
}

data class RecordContext(
    val release: AppReleaseContext,
    val purpose: TelemetryPurpose,
    val generation: Long,
    val sequence: Long,
    val occurredAtEpochMillis: Long,
)

sealed interface CanonicalRecord {
    val context: RecordContext
    val capability: TelemetryCapability

    data class Event(
        override val context: RecordContext,
        val name: String,
        val version: Int,
        val attributes: SafeAttributes,
    ) : CanonicalRecord { override val capability = TelemetryCapability.Analytics }

    data class Error(
        override val context: RecordContext,
        val code: String,
        val attributes: SafeAttributes,
        val exception: SafeExceptionSnapshot?,
        val trace: TraceContext?,
        val breadcrumbs: PersistentList<Breadcrumb>,
    ) : CanonicalRecord { override val capability = TelemetryCapability.HandledErrors }

    data class Breadcrumb(
        override val context: RecordContext,
        val name: String,
        val attributes: SafeAttributes,
    ) : CanonicalRecord { override val capability = TelemetryCapability.Breadcrumbs }

    data class Span(
        override val context: RecordContext,
        val name: String,
        val attributes: SafeAttributes,
        val trace: TraceContext,
        val elapsedNanos: Long,
        val status: SpanStatus,
    ) : CanonicalRecord { override val capability = TelemetryCapability.ManualSpans }

    data class Counter(
        override val context: RecordContext,
        val name: String,
        val amount: Long,
        val attributes: MetricAttributes,
    ) : CanonicalRecord { override val capability = TelemetryCapability.Counters }

    data class Histogram(
        override val context: RecordContext,
        val name: String,
        val unit: MetricUnit,
        val value: Double,
        val attributes: MetricAttributes,
    ) : CanonicalRecord { override val capability = TelemetryCapability.Histograms }
}

data class SafeStackFrame(val owner: String, val method: String, val file: String, val line: Int)

data class SafeExceptionSnapshot(
    val type: String,
    val frames: PersistentList<SafeStackFrame> = persistentListOf(),
    val cause: SafeExceptionSnapshot? = null,
)
