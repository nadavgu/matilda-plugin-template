import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    application
    id("com.google.protobuf") version "0.9.4"
    kotlin("jvm") version "2.0.20"
    kotlin("kapt")
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.matilda:commands-generator-api:0.3.0")
    kapt("org.matilda:commands-generator:0.3.0")
    kapt("com.google.dagger:dagger-compiler:2.47")
    implementation(kotlin("stdlib-jdk8"))
}

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonGeneratedPackage = providers.gradleProperty("PYTHON_GENERATED_PACKAGE").get()

kapt {
    arguments {
        arg("pythonRootDir", pythonRootDir.asFile.absolutePath)
        arg("pythonGeneratedPackage", pythonGeneratedPackage)
        arg("protobufDirs",
            File(layout.buildDirectory.asFile.get(), "extracted-include-protos/main/").absolutePath + ":"
                    + File(projectDir, "src/main/proto/").absolutePath
        )
        arg("javaMainPackage", "org.matilda.template")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    doLast {
        outputs.files.forEach { outputFile ->
            copy {
                from(outputFile)
                into(pythonRootDir.dir(providers.gradleProperty("RESOURCES_SUBDIR")))
                rename {"plugin.jar"}
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

sourceSets {
    main {
        java.srcDir("build/generated/source/proto/kapt/main")
        kotlin.srcDir("build/generated/source/proto/kapt/main")
    }
}

application {
    mainClass.set("org.matilda.template.TemplatePlugin")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.23.0"
    }

    generateProtoTasks {
        all().configureEach {
            builtins {
                create("python") {
                    doLast {
                        copy {
                            from(getOutputDir(this@create))
                            into(pythonRootDir)
                        }
                    }
                }
            }
        }
    }
}