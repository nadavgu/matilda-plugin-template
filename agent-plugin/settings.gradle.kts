pluginManagement {
    plugins {
        val androidPluginVersion: String by settings
        val kotlinVersion: String by settings
        id("com.android.application") version androidPluginVersion
        id("com.android.library") version androidPluginVersion
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

include("plugin")
include("plugin-android")