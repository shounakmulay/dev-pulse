import ComposeApp
import Foundation
import FirebaseCore
import FirebaseCrashlytics

func createFirebaseLogSinks() -> [LogSink] {
    FirebaseApp.configure()
    return [FirebaseLogSink()]
}

private final class FirebaseLogSink: NSObject, LogSink {
    private let defaultData = [
        "platform": "ios",
        "app.version": Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "",
        "app.build": Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as? String ?? ""
    ]

    func isLoggable(entry: LogEntry) -> Bool {
        entry.level == .error || entry.level == .assert
    }

    func log(entry: LogEntry) {
        guard isLoggable(entry: entry) else { return }
        let crashlytics = Crashlytics.crashlytics()
        var data = defaultData
        data["logger.name"] = entry.tag
        data["log.message"] = entry.message
        data["log.level"] = entry.level.name
        data[NSLocalizedDescriptionKey] = entry.message
        data["kotlin_stacktrace"] = entry.stackTrace ?? ""
        data.merge(entry.data) { _, callerValue in callerValue }
        guard entry.throwable != nil else {
            let context = data.map { key, value in "\(key)=\(value)" }.sorted().joined(separator: ", ")
            crashlytics.log("\(entry.tag): \(entry.message) {\(context)}")
            return
        }
        let error = NSError(
            domain: "dev.shounakmulay.devpulse.\(entry.tag)",
            code: 1,
            userInfo: data
        )
        crashlytics.record(error: error)
    }
}
