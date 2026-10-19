package dev.shounakmulay.devpulse.core.telemetry.runtime

import dev.shounakmulay.devpulse.core.logging.DPLogLevel
import dev.shounakmulay.devpulse.core.logging.LogEntry
import dev.shounakmulay.devpulse.core.telemetry.api.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TelemetryRuntimeTest {
    private val release = AppReleaseContext(
        appId = "devpulse", platform = "jvm", appVersion = "1", buildNumber = "7", environment = "test", channel = "test",
    )
    private val eventDefinition = EventDefinition("feed.added", 1, "feed")
    private val event = object : AnalyticsEvent {
        override val definition = eventDefinition
        override val attributes = SafeAttributes.Empty
    }

    @Test
    fun `Given changing event getters When tracking Then validation and delivery share one snapshot`() = runTest {
        val provider = RecordingTelemetryProvider()
        val telemetry = runtime(listOf(provider))
        telemetry.applyConsent(TelemetryConsent(analytics = true))
        var reads = 0
        val changing = object : AnalyticsEvent {
            override val definition = EventDefinition("safe_event", version = 1, owner = "test")
            override val attributes: SafeAttributes get() = if (reads++ == 0) SafeAttributes.Empty
                else SafeAttributes.of("url" to AttributeValue.Text("https://private.example/search"))
        }
        telemetry.track(changing)
        assertTrue(telemetry.flush(1.seconds).completed)
        assertEquals(1, reads)
        assertTrue(assertIs<CanonicalRecord.Event>(provider.records().single()).attributes.values.isEmpty())
        telemetry.close(1.seconds)
    }

    @Test
    fun `Given provider throws synthetic cancellation When another event arrives Then its worker remains alive`() = runTest {
        val recording = RecordingTelemetryProvider()
        var first = true
        val provider = object : TelemetryProvider by recording {
            override suspend fun deliver(record: CanonicalRecord): DeliveryOutcome {
                if (first) {
                    first = false
                    throw CancellationException("SDK cancelled its request")
                }
                return recording.deliver(record)
            }
        }
        val telemetry = runtime(listOf(provider))
        telemetry.applyConsent(TelemetryConsent(analytics = true))
        telemetry.track(event)
        runCurrent()
        telemetry.track(event)
        assertTrue(telemetry.flush(1.seconds).completed)
        assertEquals(1, recording.records().size)
        assertEquals(1L, telemetry.snapshot().providers.single().failures)
        telemetry.close(1.seconds)
    }

    private fun TestScope.runtime(
        providers: List<TelemetryProvider>,
        config: TelemetryRuntimeConfig = TelemetryRuntimeConfig(),
    ) = TelemetryRuntime(release, providers, StandardTestDispatcher(testScheduler), config)

    @Test
    fun `Given unknown and denied consent When tracking Then no records are buffered for a later grant`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.track(event)
        runtime.capture(IllegalStateException("private message"), ErrorContext("feed.failed"))
        runtime.applyConsent(TelemetryConsent())
        runtime.track(event)
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        assertTrue(runtime.flush(1.seconds).completed)
        assertEquals(1, provider.records().size)
        assertEquals(3L, runtime.snapshot().denied)
        assertEquals("feed.added", assertIs<CanonicalRecord.Event>(provider.records().single()).name)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given diagnostics only When recording all purposes Then analytics stays absent`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(diagnostics = true))
        runtime.track(event)
        runtime.capture(DiagnosticFailure(ErrorContext("feed.failed")))
        runtime.increment(CounterDefinition("feed.failures"))
        runtime.record(HistogramDefinition("feed.duration", MetricUnit.Milliseconds), 7.0)
        runtime.flush(1.seconds)
        assertEquals(listOf(TelemetryCapability.HandledErrors, TelemetryCapability.Counters, TelemetryCapability.Histograms), provider.records().map { it.capability })
        runtime.close(1.seconds)
    }

    @Test
    fun `Given one throwing provider When tracking Then another provider receives the same immutable record`() = runTest {
        val healthy = RecordingTelemetryProvider()
        val failing = object : TelemetryProvider by RecordingTelemetryProvider() {
            override val descriptor = ProviderDescriptor("broken", setOf(TelemetryCapability.Analytics), setOf("jvm"), "1")
            override suspend fun deliver(record: CanonicalRecord): DeliveryOutcome = error("SDK failure")
        }
        val runtime = runtime(listOf(failing, healthy))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        runCurrent()
        assertEquals(1, healthy.records().size)
        assertEquals(1L, runtime.snapshot().providers.first().failures)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given a blocked provider and bounded queue When full Then other providers proceed and excess records drop`() = runTest {
        val releaseDelivery = CompletableDeferred<Unit>()
        val slow = object : TelemetryProvider by RecordingTelemetryProvider() {
            override val descriptor = ProviderDescriptor("slow", setOf(TelemetryCapability.Analytics), setOf("jvm"), "1")
            override suspend fun deliver(record: CanonicalRecord): DeliveryOutcome {
                releaseDelivery.await()
                return DeliveryOutcome.AcceptedBySdk
            }
        }
        val healthy = RecordingTelemetryProvider()
        val runtime = runtime(listOf(slow, healthy), TelemetryRuntimeConfig(queueCapacity = 2))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        runCurrent()
        repeat(3) { runtime.track(event); runCurrent() }
        assertEquals(4, healthy.records().size)
        assertEquals(2, runtime.snapshot().providers.first().queued)
        assertEquals(1L, runtime.snapshot().providers.first().dropped)
        releaseDelivery.complete(Unit)
        assertTrue(runtime.flush(1.seconds).completed)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given in flight and queued records When consent is withdrawn Then purge finishes before return and old work never returns`() = runTest {
        val entered = CompletableDeferred<Unit>()
        val releaseDelivery = CompletableDeferred<Unit>()
        val recording = RecordingTelemetryProvider()
        var blocked = true
        val provider = object : TelemetryProvider by recording {
            override suspend fun deliver(record: CanonicalRecord): DeliveryOutcome {
                if (blocked) { entered.complete(Unit); releaseDelivery.await() }
                return recording.deliver(record)
            }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        runCurrent()
        assertTrue(entered.isCompleted)
        runtime.track(event)
        val withdrawal = async { runtime.pause(TelemetryPurpose.Analytics) }
        runCurrent()
        assertFalse(runtime.state.value.consent.analytics)
        assertFalse(withdrawal.isCompleted)
        runtime.track(event)
        releaseDelivery.complete(Unit)
        assertTrue(withdrawal.await().applied)
        assertTrue(recording.records().isEmpty())
        blocked = false
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        runtime.flush(1.seconds)
        assertEquals(1, recording.records().size)
        assertEquals(3L, recording.records().single().context.generation)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given a trace awaiting completion When consent is withdrawn and granted Then its old span is discarded`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(diagnostics = true))
        val complete = CompletableDeferred<Unit>()
        val task = launch { runtime.trace(OperationDefinition("feed.sync")) { complete.await() } }
        runCurrent()
        runtime.pause(TelemetryPurpose.Diagnostics)
        runtime.applyConsent(TelemetryConsent(diagnostics = true))
        complete.complete(Unit)
        task.join()
        runtime.flush(1.seconds)
        assertTrue(provider.records().isEmpty())
        assertEquals(1L, runtime.snapshot().stale)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given nested suspend traces When completed Then parent context survives suspension and results remain unchanged`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(diagnostics = true))
        val value = runtime.trace(OperationDefinition("feed.sync")) {
            yield()
            runtime.trace(OperationDefinition("feed.parse")) { 42 }
        }
        runtime.flush(1.seconds)
        assertEquals(42, value)
        val spans = provider.records().map { assertIs<CanonicalRecord.Span>(it) }
        assertEquals(listOf("feed.parse", "feed.sync"), spans.map { it.name })
        assertEquals(spans[1].trace.traceId, spans[0].trace.traceId)
        assertEquals(spans[1].trace.spanId, spans[0].trace.parentSpanId)
        assertNotEquals(spans[1].trace.spanId, spans[0].trace.spanId)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given Result failure and thrown cancellation When tracing Then business failures are preserved with correct status`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(diagnostics = true))
        val failure = IllegalArgumentException("private value")
        val result = runtime.traceResult(OperationDefinition("feed.parse")) { Result.failure<Int>(failure) }
        assertSame(failure, result.exceptionOrNull())
        val cancellation = CancellationException("cancelled")
        try {
            runtime.trace(OperationDefinition("feed.cancel")) { throw cancellation }
        } catch (caught: CancellationException) { assertSame(cancellation, caught) }
        runtime.capture(cancellation, ErrorContext("feed.cancelled"))
        runtime.flush(1.seconds)
        assertEquals(listOf(SpanStatus.Failure, SpanStatus.Cancelled), provider.records().map { assertIs<CanonicalRecord.Span>(it).status })
        runtime.close(1.seconds)
    }

    @Test
    fun `Given zero trace sampling When recording a metric Then metrics remain independent`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider), TelemetryRuntimeConfig(traceSampleRate = 0.0))
        runtime.applyConsent(TelemetryConsent(diagnostics = true))
        runtime.updatePolicy(TelemetryCollectionPolicy(traceSampleRate = 1.0))
        runtime.trace(OperationDefinition("feed.sync")) { runtime.increment(CounterDefinition("feed.completed")) }
        runtime.record(HistogramDefinition("feed.duration", MetricUnit.Milliseconds), 23.5)
        runtime.flush(1.seconds)
        assertEquals(listOf(TelemetryCapability.Counters, TelemetryCapability.Histograms), provider.records().map { it.capability })
        assertEquals(0.0, runtime.state.value.collectionPolicy.traceSampleRate)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given unsafe fields and exception messages When captured Then invalid fields and raw text never reach a provider`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true, diagnostics = true))
        val invalidEvent = object : AnalyticsEvent {
            override val definition = eventDefinition
            override val attributes = SafeAttributes.of("url" to AttributeValue.Text("https://private.example"))
        }
        runtime.track(invalidEvent)
        runtime.record(HistogramDefinition("feed.duration", MetricUnit.Milliseconds), Double.NaN)
        runtime.capture(IllegalStateException("https://private.example token secret", IllegalArgumentException("private search")), ErrorContext("feed.failed"))
        runtime.flush(1.seconds)
        assertEquals(2L, runtime.snapshot().invalid)
        val captured = assertIs<CanonicalRecord.Error>(provider.records().single())
        assertEquals("IllegalStateException", captured.exception?.type)
        assertEquals("IllegalArgumentException", captured.exception?.cause?.type)
        assertFalse(captured.toString().contains("private"))
        runtime.close(1.seconds)
    }

    @Test
    fun `Given raw logs When bridge has no mapping Then nothing is exported and mapped logs use only fixed safe data`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(diagnostics = true))
        val raw = LogEntry(DPLogLevel.Error, "Queue", "private search query", IllegalStateException("private URL"))
        val closed = TelemetryLogSink(runtime)
        assertFalse(closed.isLoggable(raw))
        closed.log(raw)
        val allowed = TelemetryLogSink(runtime, listOf(SafeLogMapping("Queue", DPLogLevel.Warn, SafeBreadcrumb("queue.warning"))))
        allowed.log(raw)
        runtime.flush(1.seconds)
        val crumb = assertIs<CanonicalRecord.Breadcrumb>(provider.records().single())
        assertEquals("queue.warning", crumb.name)
        assertTrue(crumb.attributes.values.isEmpty())
        runtime.close(1.seconds)
    }

    @Test
    fun `Given enabled consent When remote collection is disabled Then consent remains saved while records are denied`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true, diagnostics = true))
        runtime.track(event)
        runtime.flush(1.seconds)
        runtime.updatePolicy(TelemetryCollectionPolicy(enabled = false))
        runtime.track(event)
        runtime.capture(DiagnosticFailure(ErrorContext("feed.failed")))
        assertTrue(runtime.state.value.consent.analytics)
        assertTrue(provider.records().isEmpty())
        runtime.updatePolicy(TelemetryCollectionPolicy())
        runtime.track(event)
        runtime.flush(1.seconds)
        assertEquals(1, provider.records().size)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given no providers When tracing and closing twice Then app behavior is unchanged`() = runTest {
        val runtime = runtime(emptyList())
        runtime.applyConsent(TelemetryConsent(analytics = true, diagnostics = true))
        runtime.track(event)
        assertEquals(11, runtime.trace(OperationDefinition("feed.sync")) { 11 })
        assertTrue(runtime.flush(1.seconds).completed)
        assertTrue(runtime.close(1.seconds).completed)
        assertTrue(runtime.close(1.seconds).completed)
        assertFalse(runtime.applyConsent(TelemetryConsent()).applied)
        assertEquals(RuntimeStatus.Closed, runtime.state.value.status)
    }

    @Test
    fun `Given bounded recording provider When capacity is exceeded Then only newest records remain`() = runTest {
        val provider = RecordingTelemetryProvider(capacity = 2)
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        repeat(4) { runtime.track(event) }
        runtime.flush(1.seconds)
        assertEquals(listOf(3L, 4L), provider.records().map { it.context.sequence })
        assertEquals(2L, provider.dropped())
        runtime.close(1.seconds)
    }

    @Test
    fun `Given a provider timeout When another provider is healthy Then timeout is local and bounded`() = runTest {
        val healthy = RecordingTelemetryProvider()
        val never = CompletableDeferred<Unit>()
        val timedOut = object : TelemetryProvider by RecordingTelemetryProvider() {
            override val descriptor = ProviderDescriptor("timeout", setOf(TelemetryCapability.Analytics), setOf("jvm"), "1")
            override suspend fun deliver(record: CanonicalRecord): DeliveryOutcome {
                never.await()
                return DeliveryOutcome.AcceptedBySdk
            }
        }
        val runtime = runtime(listOf(timedOut, healthy), TelemetryRuntimeConfig(providerTimeout = 1.seconds))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        assertTrue(runtime.flush(2.seconds).completed)
        assertEquals(1, healthy.records().size)
        assertEquals(1L, runtime.snapshot().providers.first().failures)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given failed provider purge When regranting Then provider remains blocked until purge succeeds`() = runTest {
        val recording = RecordingTelemetryProvider()
        var failPurge = false
        val provider = object : TelemetryProvider by recording {
            override suspend fun purge(purpose: TelemetryPurpose) {
                if (failPurge) error("cannot purge")
                recording.purge(purpose)
            }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        runtime.flush(1.seconds)
        failPurge = true
        val paused = runtime.pause(TelemetryPurpose.Analytics)
        assertFalse(paused.applied)
        assertEquals(setOf("recording"), paused.failedProviders)
        assertFalse(runtime.applyConsent(TelemetryConsent(analytics = true)).applied)
        runtime.track(event)
        runCurrent()
        assertEquals(1, recording.records().size)
        assertEquals(ProviderStatus.Degraded, runtime.snapshot().providers.single().status)
        failPurge = false
        assertTrue(runtime.applyConsent(TelemetryConsent(analytics = true)).applied)
        runtime.track(event)
        runtime.flush(1.seconds)
        assertEquals(1, recording.records().size)
        assertEquals(3L, recording.records().single().context.generation)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given a small byte budget When record exceeds admission size Then it drops without blocking subsequent events`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider), TelemetryRuntimeConfig(maxRecordBytes = 1300))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        val definition = EventDefinition(
            name = "feed.result", version = 1, owner = "feed",
            attributes = AttributeSchema(mapOf("source" to AttributeRule.Choice(setOf("a".repeat(80))))),
        )
        runtime.track(object : AnalyticsEvent {
            override val definition = definition
            override val attributes = SafeAttributes.of("source" to AttributeValue.Text("a".repeat(80)))
        })
        runtime.track(event)
        runtime.flush(1.seconds)
        assertEquals(1L, runtime.snapshot().queueDrops)
        assertEquals("feed.added", assertIs<CanonicalRecord.Event>(provider.records().single()).name)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given mutable event holder When changed after tracking Then canonical payload preserves capture time attributes`() = runTest {
        val provider = RecordingTelemetryProvider()
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        val tracked = object : AnalyticsEvent {
            override val definition = EventDefinition(
                name = "feed.source", version = 1, owner = "feed",
                attributes = AttributeSchema(mapOf("source" to AttributeRule.Choice(setOf("manual", "discovery")))),
            )
            override var attributes = SafeAttributes.of("source" to AttributeValue.Text("manual"))
        }
        runtime.track(tracked)
        tracked.attributes = SafeAttributes.of("source" to AttributeValue.Text("discovery"))
        runtime.flush(1.seconds)
        assertEquals(AttributeValue.Text("manual"), assertIs<CanonicalRecord.Event>(provider.records().single()).attributes.values["source"])
        runtime.close(1.seconds)
    }

    @Test
    fun `Given disabled or closed runtime When flushing Then provider upload is never invoked`() = runTest {
        val recording = RecordingTelemetryProvider()
        var flushes = 0
        val provider = object : TelemetryProvider by recording {
            override suspend fun flush(): DeliveryOutcome { flushes++; return DeliveryOutcome.AcceptedBySdk }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        assertTrue(runtime.flush(1.seconds).completed)
        assertEquals(1, flushes)
        assertEquals(1, recording.records().size)
        runtime.pause(TelemetryPurpose.Analytics)
        assertTrue(runtime.flush(1.seconds).completed)
        assertEquals(1, flushes)
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        assertTrue(runtime.flush(1.seconds).completed)
        assertEquals(2, flushes)
        runtime.close(1.seconds)
        assertTrue(runtime.flush(1.seconds).completed)
        assertEquals(2, flushes)
    }

    @Test
    fun `Given failed withdrawal cleanup When flushing Then SDK upload stays blocked and failure is reported`() = runTest {
        val recording = RecordingTelemetryProvider()
        var failPurge = false
        var flushes = 0
        val provider = object : TelemetryProvider by recording {
            override suspend fun purge(purpose: TelemetryPurpose) {
                if (failPurge) error("purge unavailable")
                recording.purge(purpose)
            }
            override suspend fun flush(): DeliveryOutcome { flushes++; return DeliveryOutcome.AcceptedBySdk }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        runtime.flush(1.seconds)
        assertEquals(1, recording.records().size)
        failPurge = true
        assertFalse(runtime.pause(TelemetryPurpose.Analytics).applied)
        val flushed = runtime.flush(1.seconds)
        assertFalse(flushed.completed)
        assertEquals(setOf("recording"), flushed.failedProviders)
        assertEquals(1, flushes)
        failPurge = false
        assertTrue(runtime.pause(TelemetryPurpose.Analytics).applied)
        assertTrue(recording.records().isEmpty())
        runtime.close(1.seconds)
    }

    @Test
    fun `Given partial initialization failure When withdrawing Then cleanup is attempted and failures cannot masquerade as success`() = runTest {
        val recording = RecordingTelemetryProvider()
        var resourceExists = false
        var cleanupAllowed = false
        var closes = 0
        val provider = object : TelemetryProvider by recording {
            override suspend fun initialize(release: AppReleaseContext, policy: ProviderPolicy) {
                resourceExists = true
                error("SDK failed after creating a client")
            }
            override suspend fun updatePolicy(policy: ProviderPolicy) {
                if (!cleanupAllowed) error("SDK controls unavailable")
                assertFalse(policy.consent.analytics)
            }
            override suspend fun purge(purpose: TelemetryPurpose) {
                if (!cleanupAllowed) error("SDK purge unavailable")
                resourceExists = false
            }
            override suspend fun close() { closes++; resourceExists = false }
        }
        val runtime = runtime(listOf(provider))
        assertFalse(runtime.applyConsent(TelemetryConsent(analytics = true)).applied)
        assertTrue(resourceExists)
        assertFalse(runtime.pause(TelemetryPurpose.Analytics).applied)
        assertTrue(resourceExists)
        cleanupAllowed = true
        assertTrue(runtime.pause(TelemetryPurpose.Analytics).applied)
        assertFalse(resourceExists)
        assertTrue(runtime.close(1.seconds).completed)
        assertEquals(1, closes)
    }

    @Test
    fun `Given one provider blocks policy reconciliation When pausing Then admission closes before lifecycle lock and latest denial wins`() = runTest {
        val first = RecordingTelemetryProvider()
        val second = RecordingTelemetryProvider(descriptor = ProviderDescriptor("second", TelemetryCapability.entries.toSet(), setOf("jvm"), "1"))
        val releasePolicy = CompletableDeferred<Unit>()
        var blocked = false
        var firstPolicy: ProviderPolicy? = null
        var secondPolicy: ProviderPolicy? = null
        val providerA = object : TelemetryProvider by first {
            override suspend fun updatePolicy(policy: ProviderPolicy) { firstPolicy = policy; first.updatePolicy(policy) }
        }
        val providerB = object : TelemetryProvider by second {
            override suspend fun updatePolicy(policy: ProviderPolicy) {
                if (blocked) releasePolicy.await()
                secondPolicy = policy
                second.updatePolicy(policy)
            }
        }
        val runtime = runtime(listOf(providerA, providerB))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        blocked = true
        val older = async { runtime.updatePolicy(TelemetryCollectionPolicy(traceSampleRate = 0.5)) }
        runCurrent()
        assertEquals(ProviderStatus.Ready, runtime.snapshot().providers.first().status)
        val withdrawal = async { runtime.pause(TelemetryPurpose.Analytics) }
        runCurrent()
        assertFalse(runtime.state.value.consent.analytics)
        assertEquals(2L, runtime.state.value.analyticsGeneration)
        runtime.track(event)
        runCurrent()
        assertTrue(first.records().isEmpty())
        assertEquals(1L, runtime.snapshot().denied)
        releasePolicy.complete(Unit)
        assertTrue(older.await().applied)
        assertTrue(withdrawal.await().applied)
        assertFalse(firstPolicy!!.consent.analytics)
        assertFalse(secondPolicy!!.consent.analytics)
        assertEquals(listOf(ProviderStatus.Disabled, ProviderStatus.Disabled), runtime.snapshot().providers.map { it.status })
        runtime.close(1.seconds)
    }

    @Test
    fun `Given cancelled withdrawal waits behind policy work When old work finishes Then cleanup converges and caller remains cancelled`() = runTest {
        val recording = RecordingTelemetryProvider()
        val releasePolicy = CompletableDeferred<Unit>()
        var blocked = false
        var lastPolicy: ProviderPolicy? = null
        var returnedNormally = false
        val provider = object : TelemetryProvider by recording {
            override suspend fun updatePolicy(policy: ProviderPolicy) {
                if (blocked) releasePolicy.await()
                lastPolicy = policy
                recording.updatePolicy(policy)
            }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        val older = launch { blocked = true; runtime.updatePolicy(TelemetryCollectionPolicy()) }
        runCurrent()
        val withdrawal = launch { runtime.pause(TelemetryPurpose.Analytics); returnedNormally = true }
        runCurrent()
        withdrawal.cancel()
        runtime.track(event)
        assertFalse(runtime.state.value.consent.analytics)
        releasePolicy.complete(Unit)
        older.join()
        withdrawal.join()
        assertTrue(withdrawal.isCancelled)
        assertFalse(returnedNormally)
        assertFalse(lastPolicy!!.consent.analytics)
        assertTrue(recording.records().isEmpty())
        assertEquals(ProviderStatus.Disabled, runtime.snapshot().providers.single().status)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given SDK throws AssertionError When another event arrives Then only the failed delivery is lost`() = runTest {
        val recording = RecordingTelemetryProvider()
        var fail = true
        val provider = object : TelemetryProvider by recording {
            override suspend fun deliver(record: CanonicalRecord): DeliveryOutcome {
                if (fail) { fail = false; throw AssertionError("SDK invariant failed") }
                return recording.deliver(record)
            }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        runtime.track(event)
        runCurrent()
        runtime.track(event)
        runtime.flush(1.seconds)
        assertEquals(1L, runtime.snapshot().providers.single().failures)
        assertEquals(2L, recording.records().single().context.sequence)
        runtime.close(1.seconds)
    }

    @Test
    fun `Given concurrent close calls When SDK shutdown blocks Then exactly one cleanup owner closes the provider`() = runTest {
        val recording = RecordingTelemetryProvider()
        val releaseClose = CompletableDeferred<Unit>()
        var closes = 0
        var updates = 0
        val provider = object : TelemetryProvider by recording {
            override suspend fun updatePolicy(policy: ProviderPolicy) { updates++; recording.updatePolicy(policy) }
            override suspend fun close() { closes++; releaseClose.await(); recording.close() }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        val first = async { runtime.close(1.seconds) }
        runCurrent()
        val second = async { runtime.close(1.seconds) }
        runCurrent()
        assertEquals(1, closes)
        assertEquals(3, updates)
        assertFalse(second.isCompleted)
        releaseClose.complete(Unit)
        val report = first.await()
        assertTrue(report.completed)
        assertEquals(report, second.await())
        assertEquals(1, closes)
        assertEquals(3, updates)
        assertEquals(RuntimeStatus.Closed, runtime.state.value.status)
    }

    @Test
    fun `Given a close waiter times out When owner is still cleaning Then waiter cannot finalize or cancel the owner`() = runTest {
        val releaseClose = CompletableDeferred<Unit>()
        var closes = 0
        val provider = object : TelemetryProvider by RecordingTelemetryProvider() {
            override suspend fun close() { closes++; releaseClose.await() }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        val owner = async { runtime.close(3.seconds) }
        runCurrent()
        val waiter = runtime.close(kotlin.time.Duration.ZERO)
        assertFalse(waiter.completed)
        assertEquals(RuntimeStatus.Closing, runtime.state.value.status)
        assertFalse(owner.isCompleted)
        releaseClose.complete(Unit)
        assertTrue(owner.await().completed)
        assertEquals(1, closes)
    }

    @Test
    fun `Given SDK close failed When close is repeated Then the original failure stays visible without repeating SDK calls`() = runTest {
        var closes = 0
        val provider = object : TelemetryProvider by RecordingTelemetryProvider() {
            override suspend fun close() { closes++; error("SDK close failed") }
        }
        val runtime = runtime(listOf(provider))
        runtime.applyConsent(TelemetryConsent(analytics = true))
        val first = runtime.close(1.seconds)
        assertFalse(first.completed)
        assertEquals(setOf("recording"), first.failedProviders)
        assertEquals(first, runtime.close(1.seconds))
        assertEquals(1, closes)
    }

    @Test
    fun `Given SDK initialization times out When consent is retried Then failed controls degrade safely and remain retryable`() = runTest {
        val recording = RecordingTelemetryProvider()
        val releaseInitialization = CompletableDeferred<Unit>()
        val provider = object : TelemetryProvider by recording {
            override suspend fun initialize(release: AppReleaseContext, policy: ProviderPolicy) {
                releaseInitialization.await()
                recording.initialize(release, policy)
            }
        }
        val runtime = runtime(listOf(provider), TelemetryRuntimeConfig(providerTimeout = 1.seconds))
        assertFalse(runtime.applyConsent(TelemetryConsent(analytics = true)).applied)
        assertEquals(ProviderStatus.Degraded, runtime.snapshot().providers.single().status)
        runtime.track(event)
        assertTrue(recording.records().isEmpty())
        releaseInitialization.complete(Unit)
        assertTrue(runtime.applyConsent(TelemetryConsent(analytics = true)).applied)
        runtime.track(event)
        runtime.flush(1.seconds)
        assertEquals(1, recording.records().size)
        runtime.close(1.seconds)
    }

}
