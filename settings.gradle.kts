rootProject.name = "wiki-lib"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    val springBootVersion: String by settings
    val gitPropertiesPluginVersion: String by settings
    val versionCheckPluginVersion: String by settings
    plugins {
        // Spring
        id("org.springframework.boot") version springBootVersion

        // Build
        id("com.gorylenko.gradle-git-properties") version gitPropertiesPluginVersion

        // Analytics
        id("com.github.ben-manes.versions") version versionCheckPluginVersion

    }
}