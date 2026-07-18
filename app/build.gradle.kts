import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.hilt)
}

// Load release signing config from (in order of precedence):
//   1. Environment variables (used by CI) — KEYSTORE_FILE, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD
//   2. A local, git-ignored keystore.properties file (used for local release builds)
// When neither is present (e.g. a contributor's machine), release builds simply
// fall back to unsigned/debug behaviour so the project still builds.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps =
    Properties().apply {
        if (keystorePropsFile.exists()) {
            keystorePropsFile.inputStream().use { load(it) }
        }
    }

fun signingValue(envKey: String, propKey: String): String? =
    System.getenv(envKey) ?: keystoreProps.getProperty(propKey)

val releaseStoreFile = signingValue("KEYSTORE_FILE", "storeFile")
val hasReleaseSigning = releaseStoreFile != null

android {
    namespace = "com.darshan.notificity"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.darshan.notificity"
        minSdk = 26
        targetSdk = 35
        // versionCode / versionName can be overridden at release time with
        // -PversionCode=<n> -PversionName=<x.y.z> (see .github/workflows/release.yml).
        versionCode = (project.findProperty("versionCode") as String?)?.toInt() ?: 6
        versionName = (project.findProperty("versionName") as String?) ?: "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = signingValue("KEYSTORE_PASSWORD", "storePassword")
                keyAlias = signingValue("KEY_ALIAS", "keyAlias")
                keyPassword = signingValue("KEY_PASSWORD", "keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    // Setting up Jetpack Compose
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation(libs.glide)
    implementation(libs.glide.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.lottie.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    //Firebase Dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}