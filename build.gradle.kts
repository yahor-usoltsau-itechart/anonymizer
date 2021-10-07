import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    application
    idea
}

group = "com.company"
version = "0.0.1-SNAPSHOT"

sourceSets {
    create("testIntegration") {
        kotlin {
            compileClasspath += main.get().output
            runtimeClasspath += main.get().output
        }
    }
}

val testIntegrationApi: Configuration by configurations.getting {
    extendsFrom(configurations.testApi.get())
}

val testIntegrationImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val testIntegrationCompileOnly: Configuration by configurations.getting {
    extendsFrom(configurations.testCompileOnly.get())
}

val testIntegrationRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("com.google.guava:guava:31.0.1-jre")
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testIntegrationImplementation("org.testcontainers:postgresql:1.16.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

val testIntegration by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    shouldRunAfter(tasks.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(testIntegration)
}

application {
    mainClass.set("com.company.anonymizer.ApplicationKt")
}

idea {
    module {
        sourceDirs = emptySet()
        testSourceDirs = sourceSets["testIntegration"].allSource.srcDirs
    }
}
