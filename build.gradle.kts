group = "de.simpletactics"
version = "0.0.2-SNAPSHOT"

plugins {
	java
	`maven-publish`

	id("org.springframework.boot")
	id("com.gorylenko.gradle-git-properties")
	id("com.github.ben-manes.versions")

}

apply(plugin = "io.spring.dependency-management")

repositories {
	mavenCentral()
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11

	withSourcesJar()
	withJavadocJar()
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("javax.annotation:javax.annotation-api:1.2-b01")
	implementation("com.google.code.gson:gson:2.8.9")
	implementation("org.apache.commons:commons-collections4:4.4")

}

tasks.getByName<Jar>("jar") {
	enabled = false
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jar {
	enabled = true
	archiveClassifier.set("")
	manifest.attributes["Main-Class"] = "de.simpletactics.wiki.lib.Application"
}

tasks.wrapper {
	val versionGradle: String by project
	gradleVersion = versionGradle
}

tasks.bootJar {
	enabled = false
	mainClass.set("de.simpletactics.Application")
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