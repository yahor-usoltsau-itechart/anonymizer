import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.springframework.boot") version "2.5.5"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    idea
}

group = "com.company"
version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11

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

extra["testcontainersVersion"] = "1.16.0"
extra["javafakerVersion"] = "1.0.2"
extra["guavaVersion"] = "31.0.1-jre"

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.javafaker:javafaker:${property("javafakerVersion")}")
    implementation("com.google.guava:guava:${property("guavaVersion")}")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testIntegrationImplementation("org.testcontainers:junit-jupiter")
    testIntegrationImplementation("org.testcontainers:postgresql")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

val testIntegration by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["testIntegration"].output.classesDirs
    classpath = sourceSets["testIntegration"].runtimeClasspath

    shouldRunAfter(tasks.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(testIntegration)
}

idea {
    module {
        sourceDirs = emptySet()
        testSourceDirs = sourceSets["testIntegration"].allSource.srcDirs
    }
}
