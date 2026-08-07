import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        HtmlToMarkdownConverter_appleKt.setNativeHtmlToMarkdownConverter(converter: HtmlToMarkdownConverter())
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
