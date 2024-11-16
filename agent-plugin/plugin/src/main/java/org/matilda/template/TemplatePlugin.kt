package org.matilda.template

import org.matilda.commands.CommandRegistry
import org.matilda.commands.PluginDependencies
import org.matilda.commands.PluginDependenciesComponent
import org.matilda.commands.create

object TemplatePlugin {
    @JvmStatic
    fun createCommandRegistry(pluginDependencies: PluginDependencies): CommandRegistry {
        return TemplatePluginComponent::class.create(PluginDependenciesComponent::class.create(pluginDependencies))
            .commandRegistry()
    }
}
