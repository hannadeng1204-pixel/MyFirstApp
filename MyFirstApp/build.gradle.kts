// ---------------------------------------------------------------------------
// Top-level Gradle build file for the "BingoGameApp" project.
//
// This file only declares the build plugins that every sub-project may use.
// The plugins are marked with "apply false" so they are added to the build
// classpath here, but are only actually applied inside a module (for example
// in app/build.gradle.kts). This is the standard Android Studio convention.
// ---------------------------------------------------------------------------

plugins {
    // The Android Gradle Plugin (AGP) — used by the :app module.
    alias(libs.plugins.android.application) apply false
    // The Kotlin Compose compiler plugin — needed for Jetpack Compose support.
    alias(libs.plugins.kotlin.compose) apply false
}
