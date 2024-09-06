package org.matilda.template;

import dagger.Component;
import org.matilda.commands.CommandRegistry;
import org.matilda.commands.PluginDependenciesModule;
import org.matilda.template.generated.commands.CommandRegistryModule;

import javax.inject.Singleton;

@Component(modules = {CommandRegistryModule.class, PluginDependenciesModule.class})
@Singleton
public interface TemplatePluginComponent {
    CommandRegistry commandRegistry();
}
