pluginManagement {
    plugins {
        val androidPluginVersion: String by settings
        id("com.android.application") version androidPluginVersion
        id("com.android.library") version androidPluginVersion
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

include("plugin")
include("plugin-android")