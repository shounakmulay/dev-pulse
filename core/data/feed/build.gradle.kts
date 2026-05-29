import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
    alias(libs.plugins.mokkery)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.data.feed"
    }
    iosFrameworks(baseName = "core:data:FeedKit")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.COMMON))
            implementation(project(Modules.Core.NETWORK))
            implementation(project(Modules.Core.PREFERENCES))
            implementation(project(Modules.Core.Domain.MODELS))
            implementation(project(Modules.Core.Data.DB))

            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.ktor.client.core)
            implementation(libs.ktxml.core)
            implementation(libs.androidx.paging.common)
            implementation(libs.okio)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}
