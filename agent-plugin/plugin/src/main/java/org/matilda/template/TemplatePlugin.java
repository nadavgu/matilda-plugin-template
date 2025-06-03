package org.matilda.template;

import org.matilda.commands.CommandRegistry;
import org.matilda.commands.PluginDependencies;
import org.matilda.commands.PluginDependenciesModule;

public class TemplatePlugin {
    public static CommandRegistry createCommandRegistry(PluginDependencies pluginDependencies) {
        return DaggerTemplatePluginComponent.builder()
                .pluginDependenciesModule(new PluginDependenciesModule(pluginDependencies))
                .build()
                .commandRegistry();
    }
}
