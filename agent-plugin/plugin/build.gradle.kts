plugins {
    java
    application
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
    implementation("org.matilda:commands-generator-api:0.1.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClass.set("org.matilda.template.TemplatePlugin")
}