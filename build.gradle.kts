import com.diffplug.gradle.spotless.SpotlessExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
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
            licenseHeaderFile(rootProject.file("$rootDir/spotless/copyright.kt"))
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            // Look for the first line that doesn't have a block comment (assumed to be the license)
            licenseHeaderFile(
                rootProject.file("spotless/copyright.kts"),
                "(^(?![\\/ ]\\*).*$)"
            )
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            // Look for the first XML tag that isn't a comment (<!--) or the xml declaration (<?xml)
            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
        }
    }
}

/**
 * Runs ./gradlew spotlessApply with kt-lint/kt-fmt() before commits
 */
task("addPreCommitCodeStyleHook") {
    println("⚈ ⚈ ⚈ Running Spotless Code Style Analysis ⚈ ⚈ ⚈")
    exec {
        commandLine("cp", "./.scripts/pre-commit", "./.git/hooks")
    }
}

/**
 * No commits directly on main branch git hook
 */
task("lockCommitToMainHook") {
    exec {
        commandLine("cp", "./.scripts/commit-rule", "./.git/hooks")
    }
}