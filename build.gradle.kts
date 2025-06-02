import com.diffplug.gradle.spotless.SpotlessExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    extensions.configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude(// Build directory
                "**/build/**/*.kt",
            )
            ktfmt("0.49").dropboxStyle()
            trimTrailingWhitespace()
            endWithNewline()
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
        }
    }
}

/**
 * Runs ./gradlew spotlessApply with kt-lint/kt-fmt() before commits
// */
//task("addCodeStyle") {
//    println("⚈ ⚈ ⚈ Running Spotless Code Style Analysis ⚈ ⚈ ⚈")
//    exec {
//        commandLine("cp", "./.scripts/pre-commit", "./.git/hooks")
//    }
//}