package dev.shounakmulay.devpulse.buildsrc.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpIosTargetsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            target.extensions.configure<KotlinMultiplatformExtension> {
                iosX64()
                iosArm64()
                iosSimulatorArm64()
            }
        }
    }
}
