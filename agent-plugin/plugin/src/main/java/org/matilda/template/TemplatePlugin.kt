package org.matilda.template;

import org.matilda.commands.CommandRegistry;
import org.matilda.commands.PluginDependenciesModule;

public class TemplatePlugin {
    public static CommandRegistry createCommandRegistry(PluginDependenciesModule pluginDependenciesModule) {
        return DaggerTemplatePluginComponent.builder()
                .pluginDependenciesModule(pluginDependenciesModule)
                .build()
                .commandRegistry();
    }
}
