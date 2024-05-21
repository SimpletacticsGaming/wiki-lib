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
apply(plugin = "org.springframework.boot")

buildscript {
	repositories {
		maven {
			url = uri("https://plugins.gradle.org/m2/")
		}
	}
	val springBootVersion: String by project
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
	}
}


repositories {
	mavenCentral()
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21

	withSourcesJar()
	withJavadocJar()
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("com.google.code.gson:gson:2.11.0")
	implementation("org.apache.commons:commons-collections4:4.4")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks {
	creating(Jar::class) {
		manifest {
			attributes["Main-Class"] = "de.simpletactics.wiki.lib.Main.kt"
		}
		from(sourceSets.main.get().output)
		archiveFileName.set("sita-backend.jar")
	}

	compileKotlin {
		kotlinOptions {
			jvmTarget = JavaVersion.VERSION_21.toString()
		}
	}

	compileTestKotlin {
		kotlinOptions {
			jvmTarget = JavaVersion.VERSION_21.toString()
		}
	}
}

tasks.wrapper {
	val versionGradle: String by project
	gradleVersion = versionGradle
}

tasks.bootJar {
	enabled = false
	mainClass.set("de.simpletactics.Application")
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.register("bootRunLocal") {
	group = "application"
	description = "Runs the Spring Boot application with the local profile"
	doFirst {
		tasks.bootRun.configure {
			systemProperty("spring.profiles.active", "local,secrets")
		}
	}
	finalizedBy("bootRun")
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = "de.simpletactics"
			artifactId = "wiki-lib"
			version = version
			from(components["java"])
		}
	}
}


