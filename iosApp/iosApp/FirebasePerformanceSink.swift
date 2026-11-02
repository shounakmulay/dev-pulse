import ComposeApp
import Foundation
import FirebasePerformance

func createFirebasePerformanceSinks() -> [PerformanceSink] {
    [FirebasePerformanceSink()]
}

private final class FirebasePerformanceSink: NSObject, PerformanceSink {
    func isTraceable(entry: TraceEntry) -> Bool {
        !entry.name.wireName.isEmpty && entry.name.wireName.utf16.count <= 100 &&
            entry.name.wireName == entry.name.wireName.trimmingCharacters(in: .whitespacesAndNewlines) &&
            !entry.name.wireName.hasPrefix("_")
    }

    func startTrace(entry: TraceEntry) -> PerformanceTrace {
        let trace = Performance.sharedInstance().trace(name: entry.name.wireName)
        entry.data.forEach { key, value in trace?.setValue(value, forAttribute: key.wireName) }
        trace?.start()
        return FirebasePerformanceTrace(trace: trace)
    }
}

private final class FirebasePerformanceTrace: NSObject, PerformanceTrace {
    private let trace: Trace?

    init(trace: Trace?) {
        self.trace = trace
    }

    func stop() {
        trace?.stop()
    }
}
