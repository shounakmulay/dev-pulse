package dev.shounakmulay.devpulse.core.telemetry.runtime

import dev.shounakmulay.devpulse.core.telemetry.api.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

class RecordingTelemetryProvider(
    override val descriptor: ProviderDescriptor = ProviderDescriptor(
        id = "recording",
        capabilities = TelemetryCapability.entries.toSet(),
        supportedPlatforms = setOf("android", "ios", "jvm"),
        adapterVersion = "1",
    ),
    private val capacity: Int = 256,
) : TelemetryProvider {
    private val lock = TelemetryLock()
    private val records = ArrayDeque<CanonicalRecord>()
    private var policy = ProviderPolicy(TelemetryConsent(), 0, 0)
    private var closed = false
    private var dropped = 0L
    init { require(capacity > 0) }

    fun records(): PersistentList<CanonicalRecord> = lock.withLock { records.toPersistentList() }
    fun dropped(): Long = lock.withLock { dropped }

    override suspend fun initialize(release: AppReleaseContext, policy: ProviderPolicy) {
        lock.withLock { this.policy = policy; closed = false }
    }
    override suspend fun updatePolicy(policy: ProviderPolicy) { lock.withLock { this.policy = policy } }
    override suspend fun deliver(record: CanonicalRecord): DeliveryOutcome = lock.withLock {
        val generation = when (record.context.purpose) {
            TelemetryPurpose.Analytics -> policy.analyticsGeneration
            TelemetryPurpose.Diagnostics -> policy.diagnosticsGeneration
        }
        if (closed || !policy.consent.allows(record.context.purpose) || record.context.generation != generation) {
            return@withLock DeliveryOutcome.RejectedByPolicy
        }
        if (records.size == capacity) { records.removeFirst(); dropped++ }
        records.addLast(record)
        DeliveryOutcome.AcceptedBySdk
    }
    override suspend fun purge(purpose: TelemetryPurpose) {
        lock.withLock {
            val retained = records.filter { it.context.purpose != purpose }
            records.clear()
            records.addAll(retained)
        }
    }
    override suspend fun flush(): DeliveryOutcome = DeliveryOutcome.AcceptedBySdk
    override suspend fun close() { lock.withLock { closed = true } }
}
