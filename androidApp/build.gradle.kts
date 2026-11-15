import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
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
}

dependencies {
    implementation(project(":composeApp"))

    implementation(libs.androidx.activity.compose)
    implementation(compose.preview)

    debugImplementation(compose.uiTooling)
}
