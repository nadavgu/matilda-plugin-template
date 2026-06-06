# matilda-plugin-template

A **starter template for writing [Matilda](https://github.com/nadavgu/matilda) plugins.**
Fork it and replace the example `MathService`/`FunctionService` with your own. It ships a
worked example: a Python package + an `agent-plugin/` Gradle build + `setup.sh`, wired so
the Python and agent sides already talk over Matilda's RPC.

## Branches — pick your starting point

This repo has several branches, each a different flavor of the same template. Real
plugins (e.g. `matilda-java-plugin`, `matilda-native-plugin`) are forks of one of these.

The branches form a **linear stack** — each is built on (a strict superset of) the one
before it, so each adds capability on top of its base:

```
dev  →  kotlin  →  native
```

| Branch | Built on | Adds | Language / DI | Targets | Modules |
|--------|----------|------|---------------|---------|---------|
| `dev` | — | the example plugin (JVM + Android) | **Java**, Dagger | JVM, Android | `plugin` + `plugin-android` |
| `kotlin` | `dev` | sources converted to Kotlin + KSP | **Kotlin**, Dagger | JVM, Android | `plugin` + `plugin-android` |
| `native` | `kotlin` | Kotlin Multiplatform restructure adding native (pbandk, kotlin-inject) | **Kotlin** (`commonMain`), kotlin-inject | JVM, Android, `LINUX_X64`, `ANDROID_NATIVE_ARM32/64` | single `plugin` (KMP) |

`dev` is the default branch; `native` is the fullest (all platforms). Note `native` is
*not* native-only — it supports JVM and Android too, through KMP. (This file lives on all
three branches; check out the one whose flavor you want.)

**Because the branches are stacked, a change to a base branch must be propagated upward by
rebasing each descendant onto its (rewritten) base** — i.e. after changing `dev`, rebase
`kotlin` onto `dev`, then `native` onto `kotlin`. This keeps every branch a clean superset
of its base. Fix a thing on the lowest branch where it applies, then rebase the rest of
the stack; don't cherry-pick the same fix into each branch independently.

When something in this CLAUDE.md says "per branch", consult the actual files on the
branch you're on — module layout (`plugin` vs `plugin`+`plugin-android`), source language,
and DI framework differ as in the table above. The build flow, codegen, and Python entry
point conventions below are the same on all branches.

## Layout (this branch)

- `template/` — the Python package (import package `template`; registered under the
  `matilda.plugins` entry point group as `template = 'template.template_plugin'`).
  - `template_plugin.py` — `PLUGIN_ENTRY_POINTS` (one `PluginEntryPoint` per supported
    platform), `load_plugin(DependencyContainer)`, and the `TemplatePlugin` API. **This
    is the main thing you customize.**
  - `generated/`, `protos/*_pb2.py`, `resources/` — **build output, gitignored. Do not
    hand-edit.**
- `agent-plugin/` — the Gradle agent build.
  - `plugin/` — the plugin module: `TemplatePlugin`, `TemplatePluginComponent`,
    `MathService` (`@MatildaService`), `FunctionService` (`@MatildaDynamicService`),
    `NativeEntryPoint.kt` (the native `createCommandRegistry`), and
    `src/main/proto/template/protos/exercise.proto`.

## How a plugin works

A plugin has two halves. The **agent** declares services with `@MatildaService` /
`@MatildaCommand` (callable from Python) and `@MatildaDynamicService` (interfaces that
carry callbacks both directions). At **agent build time**, Matilda's KSP code generator
writes matching Python classes into `template/generated/` plus protobuf `*_pb2.py`.

The **Python** `template_plugin.py` must define:
1. `PLUGIN_ENTRY_POINTS: dict[Platform, PluginEntryPoint]` — for JVM platforms the value
   is the fully-qualified class implementing `createCommandRegistry`; for native it is the
   entry-point function name. Optional `binary_path` overrides the default resource.
2. `load_plugin(DependencyContainer)` — returns the object exported as
   `process.plugins.<name>`.

Agent DI is **kotlin-inject** (native/Kotlin branches) or **Dagger** (the Java branch);
Python DI is **maddie** (`DependencyContainer.get(...)`). Protobuf is `pbandk` (Kotlin) /
Google protobuf (Python). See the core's `docs/matilda_rpc.md`.

**Regenerate after changing any service, command, or `.proto`** — the `generated/` tree
and `*_pb2.py` are produced by the build, not committed.

## Build / test

- Requires the **core installed first**: `cd ../matilda && ./setup.sh install` (publishes
  `org.matilda:*` to mavenLocal; this build consumes `org.matilda:commands-generator*`
  from there — see `matildaVersion` in `agent-plugin/gradle.properties`).
- `./setup.sh install` — `gradlew assemble` then `pip install .`.
- `./setup.sh clean` — Gradle clean + wipe generated code, `*_pb2.py`, build output, and
  non-committed resources.
- Agent only: `cd agent-plugin && ./gradlew assemble`.

To verify a change, build then run the end-to-end tests: `./setup.sh install` (rebuilds
the agent plugin, regenerates Python, reinstalls), then `pytest` from the repo root. The
tests load the plugin into a real agent process and exercise it over the RPC, parametrized
across platforms. Android/native targets are skipped unless you pass
`--test-on-connected-android-device` (needs a device on adb). (There are no Kotlin unit
tests in this template — the plugin is verified through these Python tests.)

Commit/push only when asked. Don't commit generated code or `resources/` binaries — both
gitignored.
