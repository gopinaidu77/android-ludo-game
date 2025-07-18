// Ludo/build.gradle.kts

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1") // Match your Gradle version
        classpath(kotlin("gradle-plugin", version = "1.9.23")) // Or your current Kotlin version
    }

    repositories {
        google()
        mavenCentral()
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Kotlin and Android tools
    id("com.android.application") version "8.4.0" apply false
    id("com.android.library") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}

