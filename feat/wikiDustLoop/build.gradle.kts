import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
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
            baseName = "dustloop"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))

            implementation(libs.bundles.ktor)
            implementation(libs.napier)
            implementation(libs.kotlin.date.time)

            api(libs.koin.core)
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
    namespace = "io.github.sophon.dustloop"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

val featureVersion = "1.0.5"
buildkonfig {
    packageName = "io.github.sophon.dustloop"

    defaultConfigs {
        buildConfigField(STRING, "VERSION", featureVersion)
    }
}