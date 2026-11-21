import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
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

    android {
        namespace = "io.github.sophon.wiki"
        compileSdk = 37
        minSdk = 30

        // wiki compiles against 37 but uses no API newer than the app's compileSdk
        aarMetadata {
            minCompileSdk = libs.versions.android.compileSdk.get().toInt()
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }

        withHostTest {
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "wiki"
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

val featureVersion = "3.0.0"
buildkonfig {
    packageName = "io.github.sophon.wiki"

    defaultConfigs {
        buildConfigField(STRING, "VERSION", featureVersion)
    }
}

sqldelight {
    databases {
        create("WikiDB") {
            packageName.set("io.github.sophon.wiki.data")
            dialect(libs.sqldelight.dialect.sqlite)
        }
    }
}

// Force JVM target to use the JVM-specific artifact (same as `core`)
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
