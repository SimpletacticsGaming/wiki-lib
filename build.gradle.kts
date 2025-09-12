group = "de.simpletactics"
version = "1.1.0"

plugins {
    java
    `maven-publish`

    // Kotlin
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.noarg")

    id("org.jetbrains.kotlin.plugin.allopen")
    id("org.springframework.boot")
    id("com.gorylenko.gradle-git-properties")
    id("com.github.ben-manes.versions")
}

apply(plugin = "io.spring.dependency-management")

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
    //withJavadocJar()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.3.2")
    implementation("javax.annotation:javax.annotation-api:1.2-b01")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.postgresql:postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:1.20.0")
    testImplementation("org.testcontainers:postgresql")

    val kotestVersion: String by project
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("io.mockk:mockk:1.13.12")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
    exclude("**/application-secrets.*")
    manifest.attributes["Main-Class"] = "de.simpletactics.wiki.lib.Main.kt"
}

tasks.wrapper {
    val versionGradle: String by project
    gradleVersion = versionGradle
}

val nexusSnapshotUrl: String by project
val nexusUrl: String by project
val nexusUser: String by project
val nexusPassword: String by project

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.simpletactics"
            artifactId = "wiki-lib"
            version = version
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "nexus"
            url = if (version.toString().contains("SNAPSHOT", true)) {
                uri(nexusSnapshotUrl)
            } else {
                uri(nexusUrl)
            }
            credentials {
                username = nexusUser
                password = nexusPassword
            }
        }
    }
}