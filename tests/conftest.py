from typing import Generator

import pytest
from _pytest.config import Config
from _pytest.fixtures import SubRequest
from matilda.exceptions.architecture_not_supported_by_device_exception import ArchitectureNotSupportedByDeviceException
from matilda.matilda import Matilda
from matilda.matilda_process import MatildaProcess
from matilda.platform.architecture import Architecture
from matilda.platform.matilda_platform import MatildaPlatform
from matilda.platform.supported_platforms import JVM, LINUX_X64, ANDROID, ANDROID_NATIVE_ARM64, ANDROID_NATIVE_ARM32

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
    JVM,
    LINUX_X64,
    ANDROID,
    ANDROID_NATIVE_ARM64,
    ANDROID_NATIVE_ARM32,
], ids=[
    "JVM",
    "LINUX_X64",
    "ANDROID",
    "ANDROID_NATIVE_ARM64",
    "ANDROID_NATIVE_ARM32",
], scope='session')
def matilda_platform(request: SubRequest, run_on_connected_android_device: bool) -> MatildaPlatform:
    platform: MatildaPlatform = request.param
    if platform.is_android() and not run_on_connected_android_device:
        pytest.skip("Not running tests on android in this run - to run pass the option --test-on-connected-android-device")
    return platform


@pytest.fixture(scope='session')
def matilda_process(matilda_platform: MatildaPlatform, matilda: Matilda) -> Generator[MatildaProcess, None, None]:
    if matilda_platform == JVM:
        process = matilda.run_in_java_process()
    elif matilda_platform == ANDROID:
        process = matilda.run_in_android_java_process()
    elif matilda_platform == LINUX_X64:
        process = matilda.run_in_native_process()
    elif matilda_platform == ANDROID_NATIVE_ARM64:
        process = __run_in_android_native_process(matilda, Architecture.ARM64)
    elif matilda_platform == ANDROID_NATIVE_ARM32:
        process = __run_in_android_native_process(matilda, Architecture.ARM32)
    else:
        raise ValueError(matilda_platform)

    with process:
        yield process


def __run_in_android_native_process(matilda: Matilda, architecture: Architecture) -> MatildaProcess:
    try:
        return matilda.run_in_android_native_process(architecture=architecture)
    except ArchitectureNotSupportedByDeviceException as e:
        return pytest.skip(str(e))

@pytest.fixture(scope='session')
def plugin(matilda_process) -> TemplatePlugin:
    return matilda_process.plugins.template
