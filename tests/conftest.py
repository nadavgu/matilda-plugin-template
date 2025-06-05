from typing import Generator

import pytest
from _pytest.fixtures import SubRequest
from matilda.matilda import Matilda
from matilda.matilda_process import MatildaProcess
from matilda.platform.matilda_platform import MatildaPlatform

from template.template_plugin import TemplatePlugin


@pytest.fixture(scope='session')
def matilda() -> Matilda:
    return Matilda()


@pytest.fixture(params = [
    MatildaPlatform.JVM,
    MatildaPlatform.LINUX_X64,
], scope='session')
def matilda_platform(request: SubRequest) -> MatildaPlatform:
    platform: MatildaPlatform = request.param
    if platform == MatildaPlatform.LINUX_X64:
        pytest.skip(f"platform {platform} not supported by plugin")
    return platform


@pytest.fixture(scope='session')
def matilda_process(matilda_platform: MatildaPlatform, matilda: Matilda) -> Generator[MatildaProcess, None, None]:
    if matilda_platform == MatildaPlatform.JVM:
        with matilda.run_in_java_process() as process:
            yield process
    elif matilda_platform == MatildaPlatform.LINUX_X64:
        with matilda.run_in_native_process() as process:
            yield process
    else:
        raise ValueError(matilda_platform)

@pytest.fixture(scope='session')
def plugin(matilda_process) -> TemplatePlugin:
    return matilda_process.plugins.template
