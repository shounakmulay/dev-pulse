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
        jvmTest.dependencies {
            implementation(libs.androidx.datastore.preferences)
        }

        commonMain.dependencies {
            implementation(project(Modules.Core.Data.PREFERENCES))
            implementation(project(Modules.Core.Domain.MODELS))
        }
    }
}
