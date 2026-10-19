package dev.shounakmulay.devpulse.core.telemetry.runtime

import dev.shounakmulay.devpulse.core.telemetry.api.*
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlin.uuid.Uuid

class TelemetryRuntimeConfig(
    val queueCapacity: Int = 128,
    val maxQueueBytes: Int = 256 * 1024,
    val maxRecordBytes: Int = 16 * 1024,
    val queueTtl: Duration = 60.seconds,
    val providerTimeout: Duration = 2.seconds,
    val breadcrumbCapacity: Int = 24,
    val traceSampleRate: Double = 1.0,
) {
    init {
        require(queueCapacity > 0 && maxQueueBytes > 0 && maxRecordBytes > 0)
        require(queueTtl.isPositive() && queueTtl.isFinite())
        require(providerTimeout.isPositive() && providerTimeout.isFinite())
        require(breadcrumbCapacity in 0..128)
        require(traceSampleRate.isFinite() && traceSampleRate in 0.0..1.0)
    }
}

class TelemetryRuntime(
    private val release: AppReleaseContext,
    providers: List<TelemetryProvider> = emptyList(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val config: TelemetryRuntimeConfig = TelemetryRuntimeConfig(),
) : Analytics, ErrorReporter, Tracer, Metrics, TelemetryControl, TelemetryInspector, TelemetryLifecycle {
    private val lock = TelemetryLock()
    private val coordinator = Mutex()
    private val closeCompletion = CompletableDeferred<CloseReport>()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val mutableState = MutableStateFlow(TelemetryState(collectionPolicy = TelemetryCollectionPolicy(traceSampleRate = config.traceSampleRate)))
    override val state: StateFlow<TelemetryState> = mutableState.asStateFlow()
    private var policyRevision = 0L
    private var sequence = 0L
    private var denied = 0L
    private var invalid = 0L
    private var queueDrops = 0L
    private var expired = 0L
    private var stale = 0L
    private val breadcrumbs = ArrayDeque<CanonicalRecord.Breadcrumb>()
    private val routes: List<Route>

    init {
        routes = providers.map { Route(it, it.descriptor) }
        require(routes.map { it.descriptor.id }.distinct().size == routes.size)
        require(routes.all { release.platform in it.descriptor.supportedPlatforms })
        routes.forEach { route -> scope.launch { runRoute(route) } }
    }

    private class Queued(val record: CanonicalRecord, val queuedAt: TimeSource.Monotonic.ValueTimeMark, val bytes: Int)
    private class Route(val provider: TelemetryProvider, val descriptor: ProviderDescriptor) {
        val barrier = Mutex()
        val signal = Channel<Unit>(Channel.CONFLATED)
        val queue = ArrayDeque<Queued>()
        var queuedBytes = 0
        var status = ProviderStatus.Disabled
        var initialized = false
        var initializationAttempted = false
        var appliedRevision = -1L
        var active = false
        var accepted = 0L
        var failures = 0L
        var dropped = 0L
        val pendingPurges = TelemetryPurpose.entries.associateWith { 0L }.toMutableMap()
    }

    override fun track(event: AnalyticsEvent) {
        lock.withLock {
            val context = admit(TelemetryPurpose.Analytics) ?: return@withLock
            val definition = event.definition
            val attributes = event.attributes
            if (!definition.attributes.accepts(attributes)) {
                invalid++
                return@withLock
            }
            enqueue(CanonicalRecord.Event(context, definition.name, definition.version, attributes))
        }
    }

    override fun capture(error: Throwable, context: ErrorContext) {
        if (error is CancellationException) return
        lock.withLock {
            val recordContext = admit(TelemetryPurpose.Diagnostics) ?: return@withLock
            if (!context.schema.accepts(context.attributes)) {
                invalid++
                return@withLock
            }
            enqueue(CanonicalRecord.Error(
                context = recordContext,
                code = context.code,
                attributes = context.attributes,
                exception = safeException(error),
                trace = context.trace?.takeIf { it.diagnosticsGeneration == recordContext.generation },
                breadcrumbs = breadcrumbs.toPersistentList(),
            ))
        }
    }

    override fun capture(failure: DiagnosticFailure) {
        lock.withLock {
            val context = admit(TelemetryPurpose.Diagnostics) ?: return@withLock
            if (!failure.context.schema.accepts(failure.context.attributes)) {
                invalid++
                return@withLock
            }
            enqueue(CanonicalRecord.Error(
                context = context,
                code = failure.context.code,
                attributes = failure.context.attributes,
                exception = null,
                trace = failure.context.trace?.takeIf { it.diagnosticsGeneration == context.generation },
                breadcrumbs = breadcrumbs.toPersistentList(),
            ))
        }
    }

    override fun breadcrumb(breadcrumb: SafeBreadcrumb) {
        lock.withLock {
            val context = admit(TelemetryPurpose.Diagnostics) ?: return@withLock
            if (!breadcrumb.schema.accepts(breadcrumb.attributes)) {
                invalid++
                return@withLock
            }
            val record = CanonicalRecord.Breadcrumb(context, breadcrumb.name, breadcrumb.attributes)
            if (config.breadcrumbCapacity > 0) {
                if (breadcrumbs.size == config.breadcrumbCapacity) breadcrumbs.removeFirst()
                breadcrumbs.addLast(record)
            }
            enqueue(record)
        }
    }

    override fun increment(metric: CounterDefinition, amount: Long, attributes: MetricAttributes) {
        lock.withLock {
            val context = admit(TelemetryPurpose.Diagnostics) ?: return@withLock
            if (amount <= 0 || !metric.dimensions.accepts(attributes)) {
                invalid++
                return@withLock
            }
            enqueue(CanonicalRecord.Counter(context, metric.name, amount, attributes))
        }
    }

    override fun record(metric: HistogramDefinition, value: Double, attributes: MetricAttributes) {
        lock.withLock {
            val context = admit(TelemetryPurpose.Diagnostics) ?: return@withLock
            if (!value.isFinite() || value < 0 || !metric.dimensions.accepts(attributes)) {
                invalid++
                return@withLock
            }
            enqueue(CanonicalRecord.Histogram(context, metric.name, metric.unit, value, attributes))
        }
    }

    override suspend fun <T> trace(
        operation: OperationDefinition,
        attributes: SafeAttributes,
        block: suspend TraceScope.() -> T,
    ): T = traceInternal(operation, attributes, { SpanStatus.Success }, block)

    override suspend fun <T> traceResult(
        operation: OperationDefinition,
        attributes: SafeAttributes,
        block: suspend TraceScope.() -> Result<T>,
    ): Result<T> = traceInternal(operation, attributes, { if (it.isFailure) SpanStatus.Failure else SpanStatus.Success }, block)

    private suspend fun <T> traceInternal(
        operation: OperationDefinition,
        attributes: SafeAttributes,
        outcome: (T) -> SpanStatus,
        block: suspend TraceScope.() -> T,
    ): T {
        val parent = currentCoroutineContext()[ActiveTrace]
        val context = lock.withLock {
            val admitted = admit(TelemetryPurpose.Diagnostics) ?: return@withLock null
            if (!operation.attributes.accepts(attributes)) {
                invalid++
                return@withLock null
            }
            admitted
        }
        val parentPermitted = parent?.generation == context?.generation && context != null
        val sampled = context != null && if (parentPermitted) parent!!.sampled else kotlin.random.Random.nextDouble() < state.value.collectionPolicy.traceSampleRate
        val trace = if (context == null) null else TraceContext(
            traceId = if (parentPermitted) parent!!.trace.traceId else newId(),
            spanId = newId().take(16),
            parentSpanId = if (parentPermitted) parent!!.trace.spanId else null,
            diagnosticsGeneration = context.generation,
        )
        val traceScope = object : TraceScope { override val context = trace }
        val started = TimeSource.Monotonic.markNow()
        var status = SpanStatus.Success
        var blockFailure: Throwable? = null
        try {
            val value = if (trace != null) {
                withContext(ActiveTrace(trace, context!!.generation, sampled)) {
                    try { traceScope.block() } catch (failure: Throwable) { blockFailure = failure; throw failure }
                }
            } else {
                traceScope.block()
            }
            status = outcome(value)
            return value
        } catch (cancelled: CancellationException) {
            status = SpanStatus.Cancelled
            throw (blockFailure ?: cancelled)
        } catch (failure: Throwable) {
            status = SpanStatus.Failure
            throw (blockFailure ?: failure)
        } finally {
            if (context != null && trace != null && sampled) {
                lock.withLock {
                    if (isCurrent(context)) enqueue(CanonicalRecord.Span(
                        context = context,
                        name = operation.name,
                        attributes = attributes,
                        trace = trace,
                        elapsedNanos = started.elapsedNow().inWholeNanoseconds.coerceAtLeast(0),
                        status = status,
                    )) else stale++
                }
            }
        }
    }

    override suspend fun applyConsent(consent: TelemetryConsent): ConsentApplyReport {
        if (!changeDesired { it.copy(consent = consent) }) return ConsentApplyReport(false, emptySet())
        return reconcile()
    }

    override suspend fun pause(purpose: TelemetryPurpose): ConsentApplyReport {
        if (!changeDesired { current -> current.copy(consent = when (purpose) {
            TelemetryPurpose.Analytics -> current.consent.copy(analytics = false)
            TelemetryPurpose.Diagnostics -> current.consent.copy(diagnostics = false)
        }) }) return ConsentApplyReport(false, emptySet())
        return reconcile()
    }

    override suspend fun updatePolicy(policy: TelemetryCollectionPolicy): ConsentApplyReport {
        if (!changeDesired { it.copy(collectionPolicy = policy.copy(traceSampleRate = minOf(policy.traceSampleRate, config.traceSampleRate))) }) {
            return ConsentApplyReport(false, emptySet())
        }
        return reconcile()
    }

    private fun changeDesired(transform: (TelemetryState) -> TelemetryState): Boolean = lock.withLock {
        val previous = state.value
        if (previous.status in setOf(RuntimeStatus.Closing, RuntimeStatus.Closed)) return@withLock false
        val requested = transform(previous)
        val collectionChanged = previous.collectionPolicy.enabled != requested.collectionPolicy.enabled
        val next = requested.copy(
            status = RuntimeStatus.Ready,
            analyticsGeneration = previous.analyticsGeneration + if (collectionChanged || previous.consent.analytics != requested.consent.analytics) 1 else 0,
            diagnosticsGeneration = previous.diagnosticsGeneration + if (collectionChanged || previous.consent.diagnostics != requested.consent.diagnostics) 1 else 0,
        )
        mutableState.value = next
        policyRevision++
        val effective = next.toPolicy().consent
        if (!effective.diagnostics) breadcrumbs.clear()
        routes.forEach { route ->
            route.status = ProviderStatus.Starting
            TelemetryPurpose.entries.filter { !effective.allows(it) }.forEach { route.pendingPurges[it] = next.generation(it) }
            val retained = route.queue.filter { isCurrent(it.record.context) }
            route.dropped += route.queue.size - retained.size
            route.queue.clear()
            route.queue.addAll(retained)
            route.queuedBytes = retained.sumOf { it.bytes }
        }
        true
    }

    private data class PolicySnapshot(val revision: Long, val policy: ProviderPolicy)

    private suspend fun reconcile(): ConsentApplyReport {
        val caller = currentCoroutineContext()
        val report = withContext(NonCancellable) {
            try {
                withTimeout(config.providerTimeout * (routes.size * 8 + 2)) {
                    coordinator.withLock {
                        while (true) {
                            val revision = lock.withLock { policyRevision }
                            for (route in routes) reconcileRoute(route)
                            val result = lock.withLock {
                                if (revision != policyRevision) null else {
                                    val failed = routes.filter {
                                        it.status == ProviderStatus.Degraded || it.appliedRevision != policyRevision
                                    }.map { it.descriptor.id }.toPersistentSet()
                                    ConsentApplyReport(
                                        applied = failed.isEmpty() && state.value.status == RuntimeStatus.Ready,
                                        failedProviders = failed,
                                    )
                                }
                            }
                            if (result != null) return@withLock result
                        }
                        @Suppress("UNREACHABLE_CODE")
                        ConsentApplyReport(false, emptySet())
                    }
                }
            } catch (_: TimeoutCancellationException) {
                lock.withLock {
                    val failed = routes.filter { it.status == ProviderStatus.Starting || it.appliedRevision != policyRevision }
                    failed.forEach { it.status = ProviderStatus.Degraded; it.failures++ }
                    ConsentApplyReport(false, failed.map { it.descriptor.id }.toPersistentSet())
                }
            }
        }
        caller.ensureActive()
        return report
    }

    private suspend fun reconcileRoute(route: Route) = route.barrier.withLock {
        while (true) {
            val snapshot = lock.withLock { PolicySnapshot(policyRevision, state.value.toPolicy()) }
            val shouldInitialize = snapshot.policy.consent.analytics || snapshot.policy.consent.diagnostics
            var successful = true
            if (!lock.withLock { route.initialized } && shouldInitialize) {
                lock.withLock { route.initializationAttempted = true }
                successful = providerCall(route) {
                    route.provider.initialize(release, snapshot.policy.denied())
                    lock.withLock { route.initialized = true }
                }
            }
            if (lock.withLock { route.initializationAttempted }) {
                val disabled = providerCall(route) { route.provider.updatePolicy(snapshot.policy.denied()) }
                successful = disabled && successful
                val purges = lock.withLock { route.pendingPurges.toMap() }
                for ((purpose, generation) in purges) {
                    val purged = providerCall(route) { route.provider.purge(purpose) }
                    if (purged) lock.withLock {
                        if (route.pendingPurges[purpose] == generation) route.pendingPurges.remove(purpose)
                    }
                    successful = purged && successful
                }
            }
            if (lock.withLock { policyRevision != snapshot.revision }) continue
            if (successful && shouldInitialize) {
                successful = providerCall(route) { route.provider.updatePolicy(snapshot.policy) }
            }
            val finished = lock.withLock {
                if (policyRevision != snapshot.revision) false else {
                    route.status = when {
                        !successful -> ProviderStatus.Degraded
                        shouldInitialize -> ProviderStatus.Ready
                        else -> ProviderStatus.Disabled
                    }
                    if (successful) route.appliedRevision = snapshot.revision
                    true
                }
            }
            if (finished) {
                route.signal.trySend(Unit)
                break
            }
        }
    }

    override fun snapshot(): TelemetryHealth = lock.withLock {
        TelemetryHealth(
            providers = routes.map { route -> ProviderHealth(
                id = route.descriptor.id,
                status = route.status,
                queued = route.queue.size,
                accepted = route.accepted,
                failures = route.failures,
                dropped = route.dropped,
                capabilities = route.descriptor.capabilities.toPersistentList(),
            ) }.toPersistentList(),
            denied = denied,
            invalid = invalid,
            queueDrops = queueDrops,
            expired = expired,
            stale = stale,
        )
    }

    override suspend fun flush(deadline: Duration): FlushReport {
        if (!deadline.isPositive() || !deadline.isFinite()) return FlushReport(false, routes.map { it.descriptor.id }.toPersistentSet())
        val failed = mutableSetOf<String>()
        try {
            withTimeout(deadline) {
                while (lock.withLock { routes.any { it.queue.isNotEmpty() || it.active } }) delay(1)
                for (route in routes) {
                    route.barrier.withLock {
                        val disposition = lock.withLock {
                            when {
                                state.value.status in setOf(RuntimeStatus.Closing, RuntimeStatus.Closed) -> 0
                                route.status in setOf(ProviderStatus.Starting, ProviderStatus.Degraded) -> -1
                                route.status != ProviderStatus.Ready || !route.initialized -> 0
                                route.appliedRevision != policyRevision || route.pendingPurges.isNotEmpty() -> -1
                                !state.value.toPolicy().consent.let { it.analytics || it.diagnostics } -> 0
                                else -> 1
                            }
                        }
                        when (disposition) {
                            -1 -> failed.add(route.descriptor.id)
                            1 -> if (!providerCall(route) {
                                when (route.provider.flush()) {
                                    DeliveryOutcome.AcceptedBySdk, DeliveryOutcome.AcknowledgedByServer -> Unit
                                    else -> error("Provider flush failed")
                                }
                            }) failed.add(route.descriptor.id)
                        }
                    }
                }
            }
        } catch (_: TimeoutCancellationException) {
            currentCoroutineContext().ensureActive()
            failed.addAll(routes.map { it.descriptor.id })
        }
        return FlushReport(failed.isEmpty(), failed.toPersistentSet())
    }

    override suspend fun close(deadline: Duration): CloseReport {
        val ownsClose = lock.withLock {
            if (state.value.status in setOf(RuntimeStatus.Closing, RuntimeStatus.Closed)) false else {
                mutableState.value = state.value.copy(
                    status = RuntimeStatus.Closing,
                    analyticsGeneration = state.value.analyticsGeneration + 1,
                    diagnosticsGeneration = state.value.diagnosticsGeneration + 1,
                )
                policyRevision++
                routes.forEach { route ->
                    route.status = ProviderStatus.Starting
                    route.queue.clear()
                    route.queuedBytes = 0
                    TelemetryPurpose.entries.forEach { route.pendingPurges[it] = state.value.generation(it) }
                }
                breadcrumbs.clear()
                true
            }
        }
        val caller = currentCoroutineContext()
        if (!ownsClose) {
            val report = withContext(NonCancellable) {
                try { withTimeout(deadline) { closeCompletion.await() } }
                catch (_: TimeoutCancellationException) { CloseReport(false, routes.map { it.descriptor.id }.toPersistentSet()) }
            }
            caller.ensureActive()
            return report
        }
        val failed = mutableSetOf<String>()
        withContext(NonCancellable) {
            try {
                withTimeout(deadline) {
                    coordinator.withLock {
                        for (route in routes) {
                            route.barrier.withLock {
                                if (!lock.withLock { route.initializationAttempted }) continue
                                val denied = lock.withLock { state.value.toPolicy().denied() }
                                if (!providerCall(route) { route.provider.updatePolicy(denied) }) failed.add(route.descriptor.id)
                                for (purpose in TelemetryPurpose.entries) {
                                    if (!providerCall(route) { route.provider.purge(purpose) }) failed.add(route.descriptor.id)
                                }
                                if (!providerCall(route) { route.provider.close() }) failed.add(route.descriptor.id)
                            }
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                failed.addAll(routes.map { it.descriptor.id })
            } finally {
                scope.cancel()
                lock.withLock {
                    mutableState.value = state.value.copy(status = RuntimeStatus.Closed)
                    routes.forEach { it.status = ProviderStatus.Closed; it.signal.close() }
                    closeCompletion.complete(CloseReport(failed.isEmpty(), failed.toPersistentSet()))
                }
            }
        }
        caller.ensureActive()
        return closeCompletion.await()
    }

    private fun admit(purpose: TelemetryPurpose): RecordContext? {
        val current = state.value
        if (current.status != RuntimeStatus.Ready || !current.collectionPolicy.enabled || !current.consent.allows(purpose)) {
            denied++
            return null
        }
        return RecordContext(
            release = release,
            purpose = purpose,
            generation = current.generation(purpose),
            sequence = ++sequence,
            occurredAtEpochMillis = Clock.System.now().toEpochMilliseconds(),
        )
    }

    private fun isCurrent(context: RecordContext): Boolean = state.value.let {
        it.status == RuntimeStatus.Ready && it.collectionPolicy.enabled && it.consent.allows(context.purpose) && it.generation(context.purpose) == context.generation
    }

    private fun enqueue(original: CanonicalRecord) {
        var record = original
        if (record is CanonicalRecord.Error) {
            while (record is CanonicalRecord.Error && record.breadcrumbs.isNotEmpty() && estimateBytes(record) > config.maxRecordBytes) {
                record = record.copy(breadcrumbs = record.breadcrumbs.removingAt(0))
            }
        }
        val bytes = estimateBytes(record)
        for (route in routes) {
            if (record.capability !in route.descriptor.capabilities) continue
            while (route.queue.firstOrNull()?.queuedAt?.elapsedNow()?.let { it > config.queueTtl } == true) {
                route.queuedBytes -= route.queue.removeFirst().bytes
                route.dropped++
                expired++
            }
            if (route.status != ProviderStatus.Ready || bytes > config.maxRecordBytes ||
                route.queue.size >= config.queueCapacity || route.queuedBytes + bytes > config.maxQueueBytes
            ) {
                queueDrops++
                route.dropped++
                continue
            }
            route.queue.addLast(Queued(record, TimeSource.Monotonic.markNow(), bytes))
            route.queuedBytes += bytes
            route.signal.trySend(Unit)
        }
    }

    private suspend fun runRoute(route: Route) {
        for (ignored in route.signal) {
            while (true) {
                val queued = lock.withLock {
                    route.queue.removeFirstOrNull()?.also {
                        route.queuedBytes -= it.bytes
                        route.active = true
                    }
                } ?: break
                try {
                    route.barrier.withLock {
                        val permitted = lock.withLock {
                            when {
                                !isCurrent(queued.record.context) -> { stale++; false }
                                queued.queuedAt.elapsedNow() > config.queueTtl -> { expired++; false }
                                route.status != ProviderStatus.Ready -> false
                                else -> true
                            }
                        }
                        if (permitted) providerCall(route) {
                            when (route.provider.deliver(queued.record)) {
                                DeliveryOutcome.AcceptedBySdk, DeliveryOutcome.AcknowledgedByServer -> lock.withLock { route.accepted++ }
                                else -> lock.withLock { route.failures++ }
                            }
                        }
                    }
                } finally {
                    lock.withLock { route.active = false }
                }
            }
        }
    }

    private suspend fun providerCall(route: Route, block: suspend () -> Unit): Boolean = try {
        withTimeout(config.providerTimeout) { block() }
        true
    } catch (_: TimeoutCancellationException) {
        currentCoroutineContext().ensureActive()
        lock.withLock { route.failures++ }
        false
    } catch (_: CancellationException) {
        currentCoroutineContext().ensureActive()
        lock.withLock { route.failures++ }
        false
    } catch (failure: Throwable) {
        if (isProcessFatal(failure)) throw failure
        lock.withLock { route.failures++ }
        false
    }
}

private fun TelemetryState.generation(purpose: TelemetryPurpose): Long = when (purpose) {
    TelemetryPurpose.Analytics -> analyticsGeneration
    TelemetryPurpose.Diagnostics -> diagnosticsGeneration
}

private fun TelemetryState.toPolicy() = ProviderPolicy(
    consent = if (status == RuntimeStatus.Ready && collectionPolicy.enabled) consent else TelemetryConsent(policyVersion = consent.policyVersion),
    analyticsGeneration = analyticsGeneration,
    diagnosticsGeneration = diagnosticsGeneration,
)

private fun ProviderPolicy.denied() = copy(consent = TelemetryConsent(policyVersion = consent.policyVersion))

private class ActiveTrace(val trace: TraceContext, val generation: Long, val sampled: Boolean) : AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<ActiveTrace>
}

private fun newId(): String = Uuid.random().toString().replace("-", "")

private fun safeException(error: Throwable, depth: Int = 0): SafeExceptionSnapshot = SafeExceptionSnapshot(
    type = (error::class.simpleName ?: "Throwable").filter { it.isLetterOrDigit() || it == '_' }.take(80),
    frames = safeStackFrames(error, (8 shr depth).coerceAtLeast(1)),
    cause = if (depth < 3 && error.cause != null && error.cause !== error) safeException(error.cause!!, depth + 1) else null,
)

private fun SafeAttributes.byteEstimate(): Int = values.entries.sumOf { (key, value) ->
    key.length * 4 + when (value) {
        is AttributeValue.Text -> value.value.length * 4
        else -> 16
    }
}

private fun SafeExceptionSnapshot.byteEstimate(): Int = type.length * 4 + frames.sumOf {
    (it.owner.length + it.method.length + it.file.length) * 4 + 32
} + (cause?.byteEstimate() ?: 0)

private fun estimateBytes(record: CanonicalRecord): Int = 1024 + record.context.release.let {
    (it.appId.length + it.platform.length + it.appVersion.length + it.buildNumber.length +
        it.environment.length + it.channel.length + (it.commitId?.length ?: 0)) * 4
} + when (record) {
    is CanonicalRecord.Event -> record.attributes.byteEstimate()
    is CanonicalRecord.Breadcrumb -> record.attributes.byteEstimate()
    is CanonicalRecord.Error -> record.attributes.byteEstimate() + record.breadcrumbs.sumOf { 256 + it.attributes.byteEstimate() } + (record.exception?.byteEstimate() ?: 0)
    is CanonicalRecord.Span -> record.attributes.byteEstimate()
    is CanonicalRecord.Counter -> record.attributes.values.entries.sumOf { (key, value) -> (key.length + value.length) * 4 }
    is CanonicalRecord.Histogram -> record.attributes.values.entries.sumOf { (key, value) -> (key.length + value.length) * 4 }
}
