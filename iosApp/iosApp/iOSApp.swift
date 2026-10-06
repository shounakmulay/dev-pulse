import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        HtmlToMarkdownConverter_appleKt.setNativeHtmlToMarkdownConverter(converter: HtmlToMarkdownConverter())
        DevPulseStartup.shared.initialize()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
