group = "de.simpletactics"
version = "0.0.2"
val javaVersion = "21"

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

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("javax.annotation:javax.annotation-api:1.2-b01")
	implementation("com.google.code.gson:gson:2.8.9")
	implementation("org.apache.commons:commons-collections4:4.4")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:testcontainers:1.19.1")
	testImplementation("org.testcontainers:postgresql")
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