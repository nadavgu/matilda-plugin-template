plugins {
    id("com.android.application")
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonResourcesDir = pythonRootDir.dir(providers.gradleProperty("RESOURCES_SUBDIR"))

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

android {
    namespace = "org.matilda.template.android"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    packaging {
        resources {
            // no resources (such as .proto files) are needed
            excludes += "**"
        }
    }
}

androidComponents {
    beforeVariants(selector().withBuildType("release")) { variantBuilder ->
        variantBuilder.enable = false
    }
}

dependencies {
    implementation(project(":plugin"))
}

afterEvaluate {
    android.applicationVariants.forEach { variant ->
        variant.packageApplicationProvider.get().doLast {
            variant.outputs.forEach { output ->
                copy {
                    from(output.outputFile)
                    into(pythonResourcesDir)
                    rename { "android-plugin.apk" }
                }
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}