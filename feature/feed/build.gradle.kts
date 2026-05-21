import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library.compose)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.feature.feed"

        withHostTestBuilder {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    iosFrameworks(baseName = "core:feedKit")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.NAVIGATION))
            implementation(project(Modules.Core.UI))
            implementation(project(Modules.Core.Domain.MODELS))
            implementation(project(Modules.Core.Domain.FEED))
            implementation(libs.compose.components.resources)
        }
    }
}
