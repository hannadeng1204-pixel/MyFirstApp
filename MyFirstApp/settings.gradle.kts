// ---------------------------------------------------------------------------
// Gradle settings file for the "BingoGameApp" project.
//
// This file is read by Gradle before any build script. It configures:
//   1. Where Gradle should download build plugins from (pluginManagement).
//   2. Which toolchain resolver to use for locating a JDK automatically.
//   3. Where project dependencies are resolved from.
//   4. The root project name and which modules belong to the build.
// ---------------------------------------------------------------------------

// pluginManagement controls how Gradle resolves the build plugins declared
// in build.gradle.kts / app/build.gradle.kts.
pluginManagement {
    repositories {
        // Google's Maven repository hosts the Android Gradle Plugin (AGP).
        google {
            content {
                // Only allow Google/Android artifacts to come from this repo,
                // which speeds up resolution and avoids wrong matches.
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // Maven Central hosts most third-party Gradle plugins.
        mavenCentral()
        // The Gradle plugin portal hosts community plugins.
        gradlePluginPortal()
    }
}

// Declare the plugins used by this build (without applying them yet).
plugins {
    // The foojay resolver convention lets Gradle auto-download a suitable JDK
    // when the one required by the project is not already installed.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// dependencyResolutionManagement controls where libraries are downloaded from.
dependencyResolutionManagement {
    // Fail the build if a sub-project tries to declare its own repositories,
    // forcing every dependency to come from the single source of truth below.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()      // AndroidX and Google libraries.
        mavenCentral() // Most other open-source libraries.
    }
}

// The name of the root project.
rootProject.name = "MyFirstApp"

// The ":app" module is the only module in this project.
include(":app")
