package dev.shounakmulay.devpulse.core.telemetry.api

object NoOpTelemetry : Analytics, ErrorReporter, Tracer, Metrics {
    override fun track(event: AnalyticsEvent) = Unit
    override fun capture(error: Throwable, context: ErrorContext) = Unit
    override fun capture(failure: DiagnosticFailure) = Unit
    override fun breadcrumb(breadcrumb: SafeBreadcrumb) = Unit
    override fun increment(metric: CounterDefinition, amount: Long, attributes: MetricAttributes) = Unit
    override fun record(metric: HistogramDefinition, value: Double, attributes: MetricAttributes) = Unit
    override suspend fun <T> trace(operation: OperationDefinition, attributes: SafeAttributes, block: suspend TraceScope.() -> T): T = scope.block()
    override suspend fun <T> traceResult(operation: OperationDefinition, attributes: SafeAttributes, block: suspend TraceScope.() -> Result<T>): Result<T> = scope.block()
    private val scope = object : TraceScope { override val context: TraceContext? = null }
}
