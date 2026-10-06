import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library.compose)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.permissions"
    }
    iosFrameworks(baseName = "core:permissions")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.calf.permissions.notifications)
        }
    }
}
