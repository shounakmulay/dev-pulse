package dev.shounakmulay.devpulse.buildsrc.plugins

import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.compose.hot-reload")
            }

            plugins.withId("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension> {
                    sourceSets.getByName("commonMain") {
                        dependencies {
                            implementation(libs.findLibrary("compose-runtime").get())
                            implementation(libs.findLibrary("compose-foundation").get())
                            implementation(libs.findLibrary("compose-foundation-layout").get())
                            implementation(libs.findLibrary("compose-material-v3").get())
                            implementation(libs.findLibrary("compose-material3-window-size").get())
                            implementation(libs.findLibrary("compose-material3-adaptive-navigation-suite").get())
                            implementation(libs.findLibrary("compose-material3-adaptive").get())
                            implementation(libs.findLibrary("compose-material3-adaptive-layout").get())
                            implementation(libs.findLibrary("compose-material3-adaptive-navigation").get())
                            implementation(libs.findLibrary("compose-material-icons-core").get())
                            implementation(libs.findLibrary("compose-material-icons-extended").get())
                            implementation(libs.findLibrary("compose-animation").get())
                            implementation(libs.findLibrary("compose-animation-graphics").get())
                            implementation(libs.findLibrary("navigation-compose").get())
                            implementation(libs.findLibrary("navigation3-ui").get())
                            implementation(libs.findLibrary("compose-ui").get())
                            implementation(libs.findLibrary("compose-ui-graphics").get())
                            implementation(libs.findLibrary("compose-ui-text").get())
                            implementation(libs.findLibrary("compose-ui-unit").get())
                            implementation(libs.findLibrary("compose-ui-util").get())
                            implementation(libs.findLibrary("compose-components-resources").get())
                            implementation(libs.findLibrary("compose-uiToolingPreview").get())
                            implementation(libs.findLibrary("androidx-lifecycle-viewmodelCompose").get())
                            implementation(libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())
                            implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
                        }
                    }
                }
            }

            val project = this
            configurations.configureEach {
                if (name == "debugImplementation") {
                    project.dependencies.add(name, libs.findLibrary("compose-uiTooling").get())
                }
            }
        }
    }
}
