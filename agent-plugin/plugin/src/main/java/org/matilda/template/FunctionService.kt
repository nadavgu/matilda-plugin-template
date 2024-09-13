package org.matilda.template;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaDynamicService;

@MatildaDynamicService
public interface FunctionService {
    @MatildaCommand
    int apply(int value);
}
