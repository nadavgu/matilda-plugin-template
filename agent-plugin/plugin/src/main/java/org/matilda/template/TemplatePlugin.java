package org.matilda.template;

import org.matilda.commands.CommandRegistry;

public class TemplatePlugin {
    public static CommandRegistry createCommandRegistry() {
        return new CommandRegistry() {{
            addCommand(3, parameter -> new byte[] {1, 2, 3});
        }};
    }
}
