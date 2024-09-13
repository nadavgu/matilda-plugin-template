package org.matilda.template

import org.matilda.commands.CommandRegistry
import org.matilda.commands.PluginDependencies
import org.matilda.commands.PluginDependenciesModule

object TemplatePlugin {
    @JvmStatic
    fun createCommandRegistry(pluginDependencies: PluginDependencies): CommandRegistry {
        return DaggerTemplatePluginComponent.builder()
            .pluginDependenciesModule(PluginDependenciesModule(pluginDependencies))
            .build()
            .commandRegistry()
    }
}
