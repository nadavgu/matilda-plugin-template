package org.matilda.template

import org.matilda.commands.CommandRegistry
import org.matilda.commands.PluginDependenciesModule

object TemplatePlugin {
    @JvmStatic
    fun createCommandRegistry(pluginDependenciesModule: PluginDependenciesModule): CommandRegistry {
        return DaggerTemplatePluginComponent.builder()
            .pluginDependenciesModule(pluginDependenciesModule)
            .build()
            .commandRegistry()
    }
}
