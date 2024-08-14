
buildscript {
    repositories {
        google()
    }
  /*  dependencies {
        classpath("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
    }*/
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    // id("org.jlleitschuh.gradle.ktlint") version "11.5.1" apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.spotless)
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            licenseHeaderFile(
                rootProject.file("${project.rootDir}/spotless/copyright.kt"),
                "^(package|object|import|interface)"
            )
            trimTrailingWhitespace()
            endWithNewline()
        }
        format("kts") {
            target("**/*.kts")
            targetExclude(layout.buildDirectory.dir("**/*.kts").get().asFile)
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\*).*$)")
        }
        format("misc") {
            target("**/*.md", "**/.gitignore")
            trimTrailingWhitespace()
            indentWithTabs()
            endWithNewline()
        }
    }
}
