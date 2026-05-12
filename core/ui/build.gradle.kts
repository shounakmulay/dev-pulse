import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library.compose)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.ui"

        withHostTestBuilder {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    iosFrameworks(baseName = "core:coreUIKit")

    sourceSets {
        commonMain.dependencies {
            api(project(Modules.Core.DESIGN_SYSTEM))
            implementation(project(Modules.Core.PREFERENCES))
            implementation(libs.compose.components.resources)
        }
    }
}
