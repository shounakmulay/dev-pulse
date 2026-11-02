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
        DPLogKey.platform.wireName: "ios",
        DPLogKey.appVersion.wireName: Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "",
        DPLogKey.appBuild.wireName: Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as? String ?? ""
    ]

    func isLoggable(entry: LogEntry) -> Bool {
        entry.level == .error || entry.level == .assert
    }

    func log(entry: LogEntry) {
        guard isLoggable(entry: entry) else { return }
        let crashlytics = Crashlytics.crashlytics()
        var data = defaultData
        data[DPLogKey.loggerName.wireName] = entry.tag
        data[DPLogKey.logMessage.wireName] = entry.message
        data[DPLogKey.logLevel.wireName] = entry.level.name
        data[NSLocalizedDescriptionKey] = entry.message
        data[DPLogKey.kotlinStacktrace.wireName] = entry.stackTrace ?? ""
        entry.data.forEach { key, value in data[key.wireName] = value }
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
