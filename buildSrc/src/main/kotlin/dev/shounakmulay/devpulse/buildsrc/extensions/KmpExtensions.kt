package dev.shounakmulay.devpulse.buildsrc.extensions

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework

fun KotlinMultiplatformExtension.iosFrameworks(
    baseName: String,
    isStatic: Boolean = false,
    configure: Framework.() -> Unit = {}
) {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            this.baseName = baseName
            this.isStatic = isStatic
            configure()
        }
    }
}
