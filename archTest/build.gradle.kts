import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    testImplementation(libs.test.konsist)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.testJunit)
}

tasks.test {
    // Konsist reads feature sources straight from disk, so they must be inputs for up-to-date checks to work
    inputs.files(
        rootProject.fileTree("feat") {
            include("*/src/**/*.kt")
        }
    )
        .withPropertyName("featureSources")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
