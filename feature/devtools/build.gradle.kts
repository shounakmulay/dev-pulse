import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    id("devpulse.kmp.library.compose")
}

kotlin {
    android {
        namespace = "dev.shounakmulay.feature.devtools"

        withHostTestBuilder {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    iosFrameworks(baseName = "core:devtoolsKit")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.NAVIGATION))
            implementation(project(Modules.Core.DESIGN_SYSTEM))
            implementation(libs.compose.components.resources)
        }
    }
}