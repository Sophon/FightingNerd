plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass.set("com.example.botdiscord.MainKt")
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":core"))
            implementation(project(":feat:glossaryInfil"))
            implementation(project(":feat:wikiWavu"))

            implementation(libs.napier)
            implementation(libs.kord)

            api(libs.koin.core)
        }
    }
}

// Use matching instead of named
tasks.matching { it.name == "jvmRun" }.configureEach {
    (this as JavaExec).workingDir = rootProject.projectDir
}