from typing import Generator

import pytest
from _pytest.config import Config
from _pytest.fixtures import SubRequest
from matilda.matilda import Matilda
from matilda.matilda_process import MatildaProcess
from matilda.platform.matilda_platform import MatildaPlatform

from template.template_plugin import TemplatePlugin


def pytest_addoption(parser):
    parser.addoption("--test-on-connected-android-device", action="store_true", default=False)


@pytest.fixture(scope='session')
def run_on_connected_android_device(pytestconfig: Config) -> bool:
    return pytestconfig.getoption("--test-on-connected-android-device")


@pytest.fixture(scope='session')
def matilda() -> Matilda:
    return Matilda()


@pytest.fixture(params = [
    MatildaPlatform.JVM,
    MatildaPlatform.LINUX_X64,
    MatildaPlatform.ANDROID,
], scope='session')
def matilda_platform(request: SubRequest, run_on_connected_android_device: bool) -> MatildaPlatform:
    platform: MatildaPlatform = request.param
    if platform == MatildaPlatform.LINUX_X64:
        pytest.skip(f"platform {platform} not supported by plugin")
    if platform == MatildaPlatform.ANDROID and not run_on_connected_android_device:
        pytest.skip("Not running tests on android in this run - to run pass the option --test-on-connected-android-device")
    return platform


@pytest.fixture(scope='session')
def matilda_process(matilda_platform: MatildaPlatform, matilda: Matilda) -> Generator[MatildaProcess, None, None]:
    if matilda_platform == MatildaPlatform.JVM:
        with matilda.run_in_java_process() as process:
            yield process
    elif matilda_platform == MatildaPlatform.LINUX_X64:
        with matilda.run_in_native_process() as process:
            yield process
    elif matilda_platform == MatildaPlatform.ANDROID:
        with matilda.run_in_android_java_process() as process:
            yield process
    else:
        raise ValueError(matilda_platform)

@pytest.fixture(scope='session')
def plugin(matilda_process) -> TemplatePlugin:
    return matilda_process.plugins.template
