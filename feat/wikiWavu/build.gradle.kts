import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "WikiWavu"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))

            implementation(libs.bundles.ktor)
            implementation(libs.napier)
            implementation(libs.kotlin.date.time)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive.adapters)

            api(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.driver.android)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.driver.native)
        }

        jvmMain.dependencies {
            implementation(libs.sqldelight.driver.sqlite)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.assertk)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.test.turbine)
            implementation(libs.junit)
            implementation(libs.kotlin.testJunit)
        }
    }
}

android {
    namespace = "io.github.sophon.wikiwavu"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

val featureVersion = "2.2.1"
buildkonfig {
    packageName = "io.github.sophon.wikiwavu"

    defaultConfigs {
        buildConfigField(STRING, "VERSION", featureVersion)
    }
}

sqldelight {
    databases {
        create("WavuDB") {
            packageName.set("io.github.sophon.wikiwavu.data")
            dialect(libs.sqldelight.dialect.sqlite)
        }
    }
}