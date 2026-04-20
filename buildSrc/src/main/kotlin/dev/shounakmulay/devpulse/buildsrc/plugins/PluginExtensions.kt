package dev.shounakmulay.devpulse.buildsrc.plugins

import org.gradle.api.GradleException
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

object PluginExtensions {
    fun PluginContainer.applyPlugin(pluginOptional: Optional<Provider<PluginDependency>>) {
        val plugin = pluginOptional.getOrNull()?.orNull
        if (plugin != null) {
            apply(plugin.pluginId)
        } else {
            throw GradleException("Plugin not found in version catalog")
        }
    }
}
