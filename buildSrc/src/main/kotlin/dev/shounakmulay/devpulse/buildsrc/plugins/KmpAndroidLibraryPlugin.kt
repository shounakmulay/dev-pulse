package dev.shounakmulay.devpulse.buildsrc.plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("com.android.lint")
            }

            plugins.withId("com.android.kotlin.multiplatform.library") {
                extensions.configure<KotlinMultiplatformExtension> {
                    targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java)
                        .configureEach {
                            compileSdk =
                                libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
                            minSdk =
                                libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                        }
                }
            }

            // Device test deps are deferred — only added when a module opts in via withDeviceTestBuilder {}
            val project = this
            configurations.configureEach {
                if (name == "androidDeviceTestImplementation") {
                    project.dependencies.add(name, libs.findLibrary("androidx-runner").get())
                    project.dependencies.add(name, libs.findLibrary("androidx-core").get())
                    project.dependencies.add(name, libs.findLibrary("androidx-testExt-junit").get())
                }
            }
        }
    }
}
