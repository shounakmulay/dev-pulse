import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        HtmlToMarkdownConverter_appleKt.setNativeHtmlToMarkdownConverter(converter: HtmlToMarkdownConverter())
        DevPulseStartup.shared.initialize(
            logSinks: createFirebaseLogSinks(),
            performanceSinks: createFirebasePerformanceSinks()
        )
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
