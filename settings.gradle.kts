rootProject.name = "wiki-lib"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    val springBootVersion: String by settings
    val gitPropertiesPluginVersion: String by settings
    val versionCheckPluginVersion: String by settings
    val kotlinAllopen: String by settings
    val kotlinVersion: String by settings

    plugins {
        // Kotlin
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion

        // Spring
        id("org.springframework.boot") version springBootVersion

        // Build
        id("com.gorylenko.gradle-git-properties") version gitPropertiesPluginVersion

        // Analytics
        id("com.github.ben-manes.versions") version versionCheckPluginVersion
        // Kotlin plugin allopen
        id("org.jetbrains.kotlin.plugin.allopen") version kotlinAllopen
    }
}