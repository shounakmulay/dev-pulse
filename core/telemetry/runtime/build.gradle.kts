import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins { alias(libs.plugins.devpulse.kmp.library) }

kotlin {
    android { namespace = "dev.shounakmulay.devpulse.core.telemetry.runtime" }
    iosFrameworks(baseName = "telemetry_runtime")
    sourceSets {
        commonMain.dependencies { api(project(Modules.Core.Telemetry.API)) }
        commonTest.dependencies { implementation(libs.kotlinx.coroutines.test) }
    }
}
