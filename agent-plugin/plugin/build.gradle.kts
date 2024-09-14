plugins {
    java
    application
    id("com.google.protobuf") version "0.9.4"
}

group = "org.matilda"
version = providers.gradleProperty("VERSION").get()

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonGeneratedPackage = providers.gradleProperty("PYTHON_GENERATED_PACKAGE").get()
val protobufVersion: String by project
val matildaVersion: String by project

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.matilda:commands-generator-api:$matildaVersion")
    compileOnly("org.matilda:commands-generator-protos:$matildaVersion")
    annotationProcessor("org.matilda:commands-generator:$matildaVersion")
    annotationProcessor("com.google.dagger:dagger-compiler:2.47")
    implementation("com.google.dagger:dagger:2.47")
}

tasks.compileJava {
    options.compilerArgs.add("-ApythonRootDir=${pythonRootDir.asFile.absolutePath}")
    options.compilerArgs.add("-ApythonGeneratedPackage=$pythonGeneratedPackage")
    options.compilerArgs.add("-AprotobufDirs=${File(layout.buildDirectory.asFile.get(), "extracted-include-protos/main/").absolutePath}" +
            ":${File(projectDir, "src/main/proto/").absolutePath}")
    options.compilerArgs.add("-AjavaMainPackage=org.matilda.template")
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

application {
    mainClass.set("org.matilda.template.TemplatePlugin")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:$protobufVersion"
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