package org.matilda.template

import org.matilda.commands.MatildaCommand
import org.matilda.commands.MatildaDynamicService

@MatildaDynamicService
interface FunctionService {
    @MatildaCommand
    fun apply(value: Int): Int
}
