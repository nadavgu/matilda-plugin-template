package org.matilda.template

import me.tatarka.inject.annotations.Component
import org.matilda.commands.CommandRegistry
import org.matilda.commands.MatildaScope
import org.matilda.commands.PluginDependenciesComponent
import org.matilda.template.generated.commands.CommandRegistryModule

@Component
@MatildaScope
abstract class TemplatePluginComponent(@Component val pluginDependencies: PluginDependenciesComponent) : CommandRegistryModule {
    abstract fun commandRegistry(): CommandRegistry
}
