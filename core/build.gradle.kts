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

                implementation(libs.napier)

                api(libs.koin.core)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.android)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.ios)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.cio)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.assertk)
            implementation(libs.kotlinx.coroutines.test)
        }

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

// Force JVM target to use the JVM-specific artifact
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "io.github.aakira" && requested.name == "napier") {
            // For JVM configurations, explicitly use the JVM artifact
            if (name.contains("jvm", ignoreCase = true) &&
                !name.contains("android", ignoreCase = true)) {
                useTarget("io.github.aakira:napier-jvm:${requested.version}")
                because("Force JVM artifact to avoid ambiguity with Android variants")
            }
        }
    }
}