from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer
from template.generated.commands.math_service import MathService
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.plugins.plugin_entry_point import PluginEntryPoint

PLUGIN_ENTRY_POINTS = {
    MatildaPlatform.JVM: PluginEntryPoint("org.matilda.template.TemplatePlugin"),
    MatildaPlatform.ANDROID: PluginEntryPoint("org.matilda.template.TemplatePlugin"),
}


def load_plugin(dependencies_container: DependencyContainer):
    return dependencies_container.get(TemplatePlugin)


class TemplatePlugin(Dependency):
    def __init__(self, math_service: MathService):
        self.__math_service = math_service

    @property
    def math(self) -> MathService:
        return self.__math_service

    @staticmethod
    def create(dependency_container: DependencyContainer) -> 'TemplatePlugin':
        return TemplatePlugin(dependency_container.get(MathService))
