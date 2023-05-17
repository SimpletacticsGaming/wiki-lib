group = "de.simpletactics"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
	java
	id("org.springframework.boot")
	// id("io.spring.dependency-management") version "1.1.0"

	// genertates git.properties (git metadata)
	id("com.gorylenko.gradle-git-properties")

	// Check for dependency upgrades
	id("com.github.ben-manes.versions")
}

apply(plugin = "io.spring.dependency-management")

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Jar>("jar") {
	enabled = false
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.wrapper {
	val versionGradle: String by project
	gradleVersion = versionGradle
}

tasks.bootJar {
	mainClass.set("de.simpletactics.backend.Application.kt")

	archiveFileName.set("backend.jar")
	exclude("**/application-secrets.properties")
	exclude("**/application-secrets.yml")
	exclude("**/application-secrets.yaml")
	exclude("**/*.MD")
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
