import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("devpulse.kmp.library.compose")
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse"
        androidResources.enable = true
    }
    iosFrameworks(baseName = "ComposeApp", isStatic = true)
    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.DESIGN_SYSTEM))
            implementation(project(Modules.Core.NAVIGATION))
            implementation(libs.navigation3.ui)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}

compose.desktop {
    application {
        mainClass = "dev.shounakmulay.devpulse.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.shounakmulay.devpulse"
            packageVersion = "1.0.0"
        }
    }
}
