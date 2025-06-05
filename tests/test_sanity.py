from template.generated.commands.function_service import FunctionService
from template.template_plugin import TemplatePlugin


class TestSanity:
    def test_square(self, plugin: TemplatePlugin):
        assert plugin.math.square(3) == 9

    def test_sum(self, plugin: TemplatePlugin):
        assert plugin.math.sum(1, 2) == 3

    def test_div(self, plugin: TemplatePlugin):
        assert plugin.math.div(9, 3) == 3

    def test_multi_sym(self, plugin: TemplatePlugin):
        assert plugin.math.multi_sum([1, 2, 3]) == 6

    def test_factorize(self, plugin: TemplatePlugin):
        assert set(plugin.math.factorize(30)) == {2, 3, 5}

    def test_map(self, plugin: TemplatePlugin):
        class SquareFunction(FunctionService):
            def apply(self, value: int) -> int:
                return value * value

        assert plugin.math.map(SquareFunction(), [1, 2, 3]) == [1, 4, 9]

    def test_create_adder(self, plugin: TemplatePlugin):
        assert plugin.math.create_adder(3).apply(4) == 7

    def test_map_adder(self, plugin: TemplatePlugin):
        assert plugin.math.map(plugin.math.create_adder(3), [1, 2, 3]) == [4, 5, 6]