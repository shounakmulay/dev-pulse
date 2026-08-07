import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.bridge.readability"
    }
    iosFrameworks(baseName = "bridge:readability")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.COMMON))

            implementation(libs.ksoup)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
