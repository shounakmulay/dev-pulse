package dev.shounakmulay.devpulse.core.telemetry.api

import kotlin.time.Duration

interface Analytics { fun track(event: AnalyticsEvent) }

interface AnalyticsEvent {
    val definition: EventDefinition
    val attributes: SafeAttributes
}

data class EventDefinition(
    val name: String,
    val version: Int,
    val owner: String,
    val attributes: AttributeSchema = AttributeSchema.Empty,
    val deprecated: Boolean = false,
) {
    init { requireSignalName(name); require(version > 0); require(owner.isNotBlank()) }
}

data class OperationDefinition(
    val name: String,
    val attributes: AttributeSchema = AttributeSchema.Empty,
) { init { requireSignalName(name) } }

data class CounterDefinition(
    val name: String,
    val dimensions: MetricDimensions = MetricDimensions.Empty,
) { init { requireSignalName(name) } }

enum class MetricUnit { Count, Milliseconds, Bytes, Ratio }

data class HistogramDefinition(
    val name: String,
    val unit: MetricUnit,
    val dimensions: MetricDimensions = MetricDimensions.Empty,
) { init { requireSignalName(name) } }

interface Metrics {
    fun increment(metric: CounterDefinition, amount: Long = 1, attributes: MetricAttributes = MetricAttributes.Empty)
    fun record(metric: HistogramDefinition, value: Double, attributes: MetricAttributes = MetricAttributes.Empty)
}

interface Tracer {
    suspend fun <T> trace(
        operation: OperationDefinition,
        attributes: SafeAttributes = SafeAttributes.Empty,
        block: suspend TraceScope.() -> T,
    ): T
    suspend fun <T> traceResult(
        operation: OperationDefinition,
        attributes: SafeAttributes = SafeAttributes.Empty,
        block: suspend TraceScope.() -> Result<T>,
    ): Result<T>
}

interface TraceScope { val context: TraceContext? }

data class TraceContext(
    val traceId: String,
    val spanId: String,
    val parentSpanId: String?,
    val diagnosticsGeneration: Long = 0,
) {
    init {
        require(traceId.matches(Regex("[a-f0-9]{32}")))
        require(spanId.matches(Regex("[a-f0-9]{16}")))
        require(parentSpanId == null || parentSpanId.matches(Regex("[a-f0-9]{16}")))
    }
}
enum class SpanStatus { Success, Failure, Cancelled }

interface ErrorReporter {
    fun capture(error: Throwable, context: ErrorContext)
    fun capture(failure: DiagnosticFailure)
    fun breadcrumb(breadcrumb: SafeBreadcrumb)
}

data class ErrorContext(
    val code: String,
    val attributes: SafeAttributes = SafeAttributes.Empty,
    val schema: AttributeSchema = AttributeSchema.Empty,
    val trace: TraceContext? = null,
) { init { requireSignalName(code) } }

data class DiagnosticFailure(val context: ErrorContext)

data class SafeBreadcrumb(
    val name: String,
    val attributes: SafeAttributes = SafeAttributes.Empty,
    val schema: AttributeSchema = AttributeSchema.Empty,
) { init { requireSignalName(name) } }

interface TelemetryLifecycle {
    suspend fun flush(deadline: Duration): FlushReport
    suspend fun close(deadline: Duration): CloseReport
}

data class FlushReport(val completed: Boolean, val failedProviders: Set<String>)
data class CloseReport(val completed: Boolean, val failedProviders: Set<String>)
