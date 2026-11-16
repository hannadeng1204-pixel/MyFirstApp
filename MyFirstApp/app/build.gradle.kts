// ---------------------------------------------------------------------------
// Build configuration for the ":app" module of the BingoGameApp project.
//
// This file configures how the Android application is compiled, its minimum /
// target SDK versions, and which third-party libraries it depends on.
// ---------------------------------------------------------------------------

plugins {
    // Applies the Android application plugin (builds an APK/AAB).
    alias(libs.plugins.android.application)
    // Applies the Kotlin Compose compiler plugin for Jetpack Compose support.
    alias(libs.plugins.kotlin.compose)
}

android {
    // The namespace is the package used for generated R classes and BuildConfig.
    namespace = "hk.edu.thei.mti.bingogame"

    // The SDK version used to compile the app against.
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        // The unique application id of the app on the device.
        applicationId = "hk.edu.thei.mti.bingogame"
        // Minimum Android version the app runs on (Android 7.0 / API 24).
        minSdk = 24
        // The SDK version the app is tested against.
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        // The test runner used for instrumented (on-device) tests.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                // R8/minification is disabled for this educational project so
                // the release build remains simple and easy to debug.
                enable = false
            }
        }
    }

    compileOptions {
        // Use Java 11 language features for the Java parts of the build.
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        // Enable Jetpack Compose UI support.
        compose = true
    }
}

dependencies {
    // ---- Jetpack Compose ----
    // The Compose BOM aligns the versions of all Compose libraries.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // ---- AndroidX ----
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ---- Classic Android Views (used by the XML layout activities) ----
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    // ---- Testing ----
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
