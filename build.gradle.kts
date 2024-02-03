group = "de.simpletactics"
version = "0.0.2"
val javaVersion = "17"

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
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17

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
			jvmTarget = javaVersion
		}
	}

	compileTestKotlin {
		kotlinOptions {
			jvmTarget = javaVersion
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
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
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


