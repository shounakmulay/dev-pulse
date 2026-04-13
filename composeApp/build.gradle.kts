import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("devpulse.kmp.application")
}

kotlin {
    iosFrameworks(baseName = "ComposeApp", isStatic = true)
    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.designsystem))
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "dev.shounakmulay.devpulse"
    defaultConfig {
        applicationId = "dev.shounakmulay.devpulse"
        versionCode = 1
        versionName = "1.0"
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
