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

tasks.withType<Test>().configureEach {
    // Konsist reads project sources straight from disk, so they must be inputs for up-to-date checks to work
    inputs.files(
        rootProject.fileTree("feat") { include("*/src/**/*.kt") },
        rootProject.fileTree("composeApp/src") { include("**/*.kt") },
        rootProject.fileTree("bot/discord/src") { include("**/*.kt") },
    )
        .withPropertyName("projectSources")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

tasks.register<Test>("testArchHexagonal") {
    group = "verification"
    description = "Verifies the hexagonal architecture of migrated feature modules"

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    filter { includeTestsMatching("io.github.sophon.testsuite.hex.*") }
}

tasks.register<Test>("testCoverage") {
    group = "verification"
    description = "Verifies that all service, adapter mapper and util files have corresponding unit tests"

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    filter { includeTestsMatching("io.github.sophon.testsuite.coverage.*") }
}
