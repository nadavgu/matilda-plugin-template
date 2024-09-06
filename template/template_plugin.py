from maddie.dependency import Dependency
from maddie.dependency_container import DependencyContainer
from template.generated.commands.math_service import MathService

PLUGIN_ENTRY_POINT_CLASS_NAME = "org.matilda.template.TemplatePlugin"


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
