package dev.shounakmulay.devpulse.buildsrc.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("devpulse.kmp.android.library")
            apply("devpulse.kmp.ios")
            apply("devpulse.kmp.jvm")
            apply("devpulse.kmp.compose")
        }
    }
}
