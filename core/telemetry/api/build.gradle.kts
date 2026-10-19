import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins { alias(libs.plugins.devpulse.kmp.library) }

kotlin {
    android { namespace = "dev.shounakmulay.devpulse.core.telemetry.api" }
    iosFrameworks(baseName = "telemetry_api")
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.collections.immutable)
        }
    }
}
