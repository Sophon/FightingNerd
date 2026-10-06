import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
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

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))

            implementation(libs.bundles.ktor)
            implementation(libs.napier)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive.adapters)
            implementation(libs.kotlinx.serialization.json)

            api(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.assertk)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.test.turbine)
        }

        jvmMain.dependencies {
            implementation(libs.sqldelight.driver.sqlite)
        }

        jvmTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlin.testJunit)
        }
    }
}

val featureVersion = "2.0.0"

buildkonfig {
    packageName = "io.github.sophon.glossaryinfil"

    defaultConfigs {
        buildConfigField(STRING, "VERSION", featureVersion)
    }
}

sqldelight {
    databases {
        create("GlossaryDB") {
            packageName.set("io.github.sophon.glossaryinfil.data")
        }
    }
}