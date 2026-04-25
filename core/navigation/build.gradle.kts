import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library.compose)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.navigation"

        withHostTestBuilder {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    iosFrameworks(baseName = "core:navigationKit")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.components.resources)
        }
    }
}
