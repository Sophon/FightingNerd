import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val appVersionName: String = (project.properties["app.version.name"] as? String) ?: "1.0.0-dev"
val appVersionCode: Int = System.getenv("APP_VERSION_CODE")?.toIntOrNull() ?: 1

val releaseKeystorePath: String = System.getenv("KEYSTORE_PATH").orEmpty()
val releaseKeystorePassword: String = System.getenv("KEYSTORE_PASSWORD").orEmpty()
val releaseKeyAlias: String = System.getenv("KEY_ALIAS").orEmpty()
val releaseKeyPassword: String = System.getenv("KEY_PASSWORD").orEmpty()

val hasReleaseSigning: Boolean = listOf(
    releaseKeystorePath,
    releaseKeystorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { it.isNotEmpty() }

val revenueCatApiKey: String = run {
    val fromLocal = rootProject.file("local.properties")
        .takeIf { it.exists() }
        ?.inputStream()
        ?.use { Properties().apply { load(it) } }
        ?.getProperty("REVENUECAT_API_KEY")
    val resolved = fromLocal ?: System.getenv("REVENUECAT_API_KEY").orEmpty()
    resolved
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
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

            implementation(project(":core"))
            implementation(project(":feat:wikiWavu"))
            implementation(project(":feat:wikiSupercombo"))
            implementation(project(":feat:xko"))
            implementation(project(":feat:wikiDreamCancel"))
            implementation(project(":feat:wikiDustLoop"))
            implementation(project(":feat:wikiMizuumi"))
            implementation(project(":feat:wikiDragDown"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.assertk)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.test.turbine)
        }
    }
}

android {
    namespace = "io.github.sophon.fightingnerd"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.sophon.fightingnerd"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseKeystorePath)
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = if (hasReleaseSigning) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }
    lint {
        abortOnError = true
        warningsAsErrors = false
        checkDependencies = true
        checkReleaseBuilds = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                output.outputFileName = "FightingNerd-$appVersionName.apk"
            }
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
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
        buildConfigField(STRING, "REVENUECAT_API_KEY", revenueCatApiKey)
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
