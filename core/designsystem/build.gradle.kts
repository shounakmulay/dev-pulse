import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    id("devpulse.kmp.library.compose")
}

kotlin {
    androidLibrary {
        namespace = "dev.shounakmulay.core.designsystem"

        withHostTestBuilder {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    iosFrameworks(baseName = "core:designsystemKit")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.components.resources)
        }
    }
}
