import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library.compose)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.webview"
    }
    iosFrameworks(baseName = "core:webviewKit")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.UI))
            implementation(project(Modules.Core.NAVIGATION))
            implementation(libs.compose.webview.multiplatform)
        }
    }
}
