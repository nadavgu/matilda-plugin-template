pluginManagement {
    plugins {
        val kspVersion: String by settings
        val kotlinVersion: String by settings
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

include("plugin")
