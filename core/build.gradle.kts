plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    androidLibrary {
        namespace = "io.github.sophon.core"
        compileSdk = 36
        minSdk = 30

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    val xcfName = "coreKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.ktor)
                implementation(libs.ktor.cio)
                implementation(libs.ktor.slf)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.date.time)

                api(libs.koin.core)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.android)
                implementation(libs.napier)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.ios)
                implementation(libs.napier)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.cio)
                // Use explicit JVM artifact to avoid variant ambiguity
                implementation("io.github.aakira:napier-jvm:2.7.1")
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.assertk)
            implementation(libs.kotlinx.coroutines.test)
        }

        // Add jvmTest source set
        jvmTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlin.testJunit)
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
                implementation(libs.junit)
                implementation(libs.kotlin.testJunit)
            }
        }
    }
}