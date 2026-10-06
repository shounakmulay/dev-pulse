import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.devpulse.kmp.library.compose)
    alias(libs.plugins.aboutLibraries)
}

aboutLibraries {
    export {
        outputFile =
            file("${rootDir}/core/resources/src/commonMain/composeResources/files/aboutlibraries.json")
    }
    library {
        duplicationMode = DuplicateMode.MERGE
        duplicationRule = DuplicateRule.SIMPLE
    }
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse"
    }
    iosFrameworks(baseName = "ComposeApp", isStatic = true) {
        export(project(Modules.Bridge.MARKDOWN_CONVERTER))
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.UI))
            implementation(project(Modules.Core.WEBVIEW))
            implementation(project(Modules.Core.NAVIGATION))
            implementation(project(Modules.Core.RESOURCES))
            implementation(project(Modules.Core.Data.PREFERENCES))
            implementation(project(Modules.Core.COMMON))
            implementation(project(Modules.Core.NETWORK))
            implementation(project(Modules.Core.SYNC))
            implementation(project(Modules.Core.Domain.MODELS))
            implementation(project(Modules.Core.Domain.SETTINGS))
            implementation(project(Modules.Core.Domain.FEED))
            implementation(project(Modules.Core.Data.SETTINGS))
            implementation(project(Modules.Core.Data.DB))
            implementation(project(Modules.Core.Data.FEED))
            implementation(project(Modules.Core.NOTIFICATIONS))
            implementation(project(Modules.Core.PERMISSIONS))

            implementation(project(Modules.Feature.HOME))
            implementation(project(Modules.Feature.FEED))
            implementation(project(Modules.Feature.DEVTOOLS))
            implementation(project(Modules.Feature.SETTINGS))

            api(project(Modules.Bridge.MARKDOWN_CONVERTER))
            api(project(Modules.Bridge.READABILITY))

            implementation(libs.navigation3.ui)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktxml.core)
            implementation(libs.multiplatform.markdown.renderer.m3)
            implementation(libs.multiplatform.markdown.renderer)
            implementation(libs.multiplatform.markdown.renderer.coil3)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.koin.androidx.workmanager)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-annotations-common"))
        }
        jvmTest.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.shounakmulay.devpulse.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.shounakmulay.devpulse"
            packageVersion = buildConfig.desktop.packageVersion
        }
    }
}
