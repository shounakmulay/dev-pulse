package dev.shounakmulay.devpulse.buildsrc.plugins

import org.gradle.api.GradleException
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

object PluginExtensions {
    fun PluginContainer.applyPlugin(
        alias: String,
        pluginOptional: Optional<Provider<PluginDependency>>,
    ) {
        val plugin = pluginOptional.getOrNull()?.orNull
            ?: throw GradleException(
                "Plugin alias '$alias' not found in libs.versions.toml [plugins]"
            )
        apply(plugin.pluginId)
    }
}
