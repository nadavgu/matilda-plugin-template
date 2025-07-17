pluginManagement {
    plugins {
        val androidPluginVersion: String by settings
        val kspVersion: String by settings
        val kotlinVersion: String by settings
        id("com.android.application") version androidPluginVersion
        id("com.android.library") version androidPluginVersion
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