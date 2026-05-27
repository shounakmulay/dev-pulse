import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.domain.feed"
    }
    iosFrameworks(baseName = "core:domain:FeedKit")

    sourceSets {
        commonMain.dependencies {
            api(project(Modules.Core.Domain.MODELS))
            implementation(project(Modules.Core.COMMON))
            implementation(project(Modules.Core.Data.FEED))

            implementation(libs.androidx.paging.common)
        }
    }
}
