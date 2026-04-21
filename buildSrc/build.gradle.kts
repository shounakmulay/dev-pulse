plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization.gradle.plugin)
    implementation(libs.compose.multiplatform.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.compose.hot.reload.gradle.plugin)
    implementation(libs.koin.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kmpAndroidApplication") {
            id = "devpulse.kmp.android.application"
            implementationClass = "dev.shounakmulay.devpulse.buildsrc.plugins.KmpAndroidApplicationPlugin"
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
    }
}
