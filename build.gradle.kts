import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    application
}

group = "com.company"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("com.google.guava:guava:31.0.1-jre")
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.mockito:mockito-core:3.12.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("com.company.anonymizer.ApplicationKt")
}
