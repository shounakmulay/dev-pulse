import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.domain.settings"
    }
    iosFrameworks(baseName = "core:domain:SettingsKit")

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }

        commonMain.dependencies {
            api(project(Modules.Core.Telemetry.API))
            implementation(libs.kotlinx.serialization.json)
            api(project(Modules.Core.Domain.MODELS))
            implementation(project(Modules.Core.COMMON))
            implementation(project(Modules.Core.Data.SETTINGS))
        }
    }
}
