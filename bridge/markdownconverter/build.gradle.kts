import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.bridge.markdownconverter"
    }
    iosFrameworks(baseName = "bridge:markdownconverter")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.COMMON))
        }
        androidMain.dependencies {
            implementation(libs.html.to.markdown.android)
        }
        jvmMain.dependencies {
            implementation(libs.html.to.markdown)
        }
    }
}
