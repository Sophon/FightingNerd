import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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

val appVersionName = project.properties["app.version.name"] as String
val appVersionCode = (project.properties["app.version.code"] as String).toInt()

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

            binaryOption("bundleVersion", appVersionCode.toString())
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

            implementation(project(":core"))
            implementation(project(":feat:wikiWavu"))
            implementation(project(":feat:wikiSupercombo"))
            implementation(project(":feat:xko"))
            implementation(project(":feat:wikiDreamCancel"))
            implementation(project(":feat:wikiDustLoop"))
            implementation(project(":feat:wikiMizuumi"))
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
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
        buildConfigField(INT, "VERSION_CODE", appVersionCode.toString())
    }
}

sqldelight {
    databases {
        create("CharacterDatabase") {
            packageName.set("io.github.sophon.fightingnerd.db.character")
            srcDirs.setFrom("src/commonMain/sqldelight")
        }
//        create("MoveDatabase") {
//            packageName.set("io.github.sophon.fightingnerd.db.move")
//            srcDirs.setFrom("src/commonMain/sqldelight/move")
//        }
    }
}
