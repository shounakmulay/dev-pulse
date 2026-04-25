import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}
compose.resources {
    publicResClass = true
}
kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.resources"
        androidResources.enable = true
    }

    iosFrameworks(baseName = "core:resourcesKit")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(libs.compose.components.resources)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
        }
    }
}