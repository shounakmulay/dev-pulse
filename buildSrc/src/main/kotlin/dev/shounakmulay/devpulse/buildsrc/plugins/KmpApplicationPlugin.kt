package dev.shounakmulay.devpulse.buildsrc.plugins

import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import dev.shounakmulay.devpulse.buildsrc.plugins.PluginExtensions.applyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.applyPlugin(libs.findPlugin("kotlinMultiplatform"))
            with(pluginManager) {
                apply("devpulse.kmp.android.library")
                apply("devpulse.kmp.ios")
                apply("devpulse.kmp.jvm")
                apply("devpulse.kmp.compose")
            }
        }
    }
}
