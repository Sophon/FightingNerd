import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val appVersionName: String = (project.properties["app.version.name"] as? String) ?: "1.0.0-dev"
val localProperties: Properties = rootProject.file("local.properties")
    .takeIf { it.exists() }
    ?.inputStream()
    ?.use { Properties().apply { load(it) } }
    ?: Properties()

fun readSecret(name: String): String {
    val fromLocal = localProperties.getProperty(name)
    val resolved = fromLocal ?: System.getenv(name).orEmpty()
    return resolved
}

val revenueCatApiKeyAndroid: String = readSecret("REVENUECAT_API_KEY_ANDROID")
val revenueCatApiKeyIos: String = readSecret("REVENUECAT_API_KEY_IOS")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.sqldelight)
}

kotlin {
    android {
        namespace = "io.github.sophon.fightingnerd.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }

        withHostTest {
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "io.github.sophon.fightingnerd")
            binaryOption("bundleShortVersionString", appVersionName)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.android)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.napier)

            implementation(libs.sqldelight.driver.android)

            implementation(libs.androidx.work.runtime)

            implementation(libs.play.review.ktx)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.driver.native)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.coil.compose.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.core)
            implementation(libs.coil.svg)
            implementation(libs.coil.network.ktor)

            implementation(libs.napier)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlin.date.time)

            implementation(libs.datastore)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive.adapters)

            implementation(libs.composemediaplayer)

            implementation(libs.kotlinx.collections.immutable)

            implementation(libs.revenuecat.purchases.core)

            implementation(libs.okio)

            implementation(project(":core"))
            implementation(project(":feat:wiki"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.assertk)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.test.turbine)
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.sophon.fightingnerd.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.sophon.fightingnerd"
            packageVersion = "1.0.0"
        }
    }
}

buildkonfig {
    packageName = "io.github.sophon.fightingnerd"
    defaultConfigs {
        buildConfigField(STRING, "VERSION", appVersionName)
        buildConfigField(STRING, "REVENUECAT_API_KEY", "")
    }
    targetConfigs {
        create("android") {
            buildConfigField(STRING, "REVENUECAT_API_KEY", revenueCatApiKeyAndroid)
        }
        listOf("iosArm64", "iosSimulatorArm64").forEach { iosTarget ->
            create(iosTarget) {
                buildConfigField(STRING, "REVENUECAT_API_KEY", revenueCatApiKeyIos)
            }
        }
    }
}

sqldelight {
    databases {
        create("CharacterDatabase") {
            packageName.set("io.github.sophon.fightingnerd.db.character")
            srcDirs.setFrom("src/commonMain/sqldelight/character")
            verifyMigrations.set(false)
        }
        create("MoveDatabase") {
            packageName.set("io.github.sophon.fightingnerd.db.move")
            srcDirs.setFrom("src/commonMain/sqldelight/move")
            verifyMigrations.set(false)
        }
    }
}
