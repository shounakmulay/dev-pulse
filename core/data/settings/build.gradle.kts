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
        commonMain.dependencies {
            implementation(project(Modules.Core.PREFERENCES))
            implementation(project(Modules.Core.Domain.MODELS))
        }
    }
}
