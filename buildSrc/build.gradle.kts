plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.multiplatform.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.compose.hot.reload.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kmpAndroidLibrary") {
            id = "devpulse.kmp.android.library"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpAndroidLibraryPlugin"
        }
        register("kmpAndroidApplication") {
            id = "devpulse.kmp.android.application"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpAndroidApplicationPlugin"
        }
        register("kmpIos") {
            id = "devpulse.kmp.ios"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpIosTargetsPlugin"
        }
        register("kmpJvm") {
            id = "devpulse.kmp.jvm"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpJvmTargetPlugin"
        }
        register("kmpCompose") {
            id = "devpulse.kmp.compose"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpComposePlugin"
        }
        register("kmpLibrary") {
            id = "devpulse.kmp.library"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpLibraryPlugin"
        }
        register("kmpLibraryCompose") {
            id = "devpulse.kmp.library.compose"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpLibraryComposePlugin"
        }
        register("kmpApplication") {
            id = "devpulse.kmp.application"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpApplicationPlugin"
        }
    }
}
