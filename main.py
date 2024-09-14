from matilda.matilda import Matilda

from template.generated.commands.function_service import FunctionService
from template.generated.commands.math_service import MathService


class SquareFunction(FunctionService):
    def apply(self, value: int) -> int:
        return value * value


if __name__ == '__main__':
    with Matilda().run_in_java_process() as matilda_process:
        math_service: MathService = matilda_process.plugins.template.math
        print(math_service.sum(3, 4))
        print(math_service.map(SquareFunction(), [1, 2, 3, 4]))
        adder = math_service.create_adder(5)
        print(adder.apply(3))
        print(math_service.map(adder, [1, 2, 3, 4]))
