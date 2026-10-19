import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.data.settings"
    }
    iosFrameworks(baseName = "core:data:SettingsKit")

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }

        commonMain.dependencies {
            implementation(project(Modules.Core.Telemetry.API))
            implementation(libs.kotlinx.serialization.json)
            implementation(project(Modules.Core.Data.PREFERENCES))
            implementation(project(Modules.Core.Domain.MODELS))
        }
    }
}
