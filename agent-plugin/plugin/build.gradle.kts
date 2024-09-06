plugins {
    java
    application
    id("com.google.protobuf") version "0.9.4"
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
    annotationProcessor("org.matilda:commands-generator:0.3.0")
    annotationProcessor("com.google.dagger:dagger-compiler:2.47")
}

val pythonRootDir = rootProject.layout.projectDirectory.dir(providers.gradleProperty("PYTHON_ROOT_DIR_PATH")).get()
val pythonGeneratedPackage = providers.gradleProperty("PYTHON_GENERATED_PACKAGE").get()

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