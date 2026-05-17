import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.data.feed"
    }
    iosFrameworks(baseName = "core:data:FeedKit")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.NETWORK))
            implementation(project(Modules.Core.Domain.MODELS))
            implementation(project(Modules.Core.Data.DB))

            implementation(libs.rssparser)
            implementation(libs.androidx.paging.common)
        }
    }
}
