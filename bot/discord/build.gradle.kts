import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass.set("io.github.sophon.botdiscord.MainKt")
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":core"))
            implementation(project(":feat:glossaryInfil"))
            implementation(project(":feat:wikiWavu"))
            implementation(project(":feat:wikiSupercombo"))

            implementation(libs.napier)
            implementation(libs.kord)
            implementation(libs.kotlin.date.time)

            api(libs.koin.core)
        }

        jvmTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.testJunit)
            implementation(libs.test.assertk)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

// Use matching instead of named
tasks.matching { it.name == "jvmRun" }.configureEach {
    (this as JavaExec).workingDir = rootProject.projectDir
}

val featureVersion = "1.0.0"
buildkonfig {
    packageName = "io.github.sophon.discord"

    defaultConfigs {
        buildConfigField(STRING, "VERSION", featureVersion)
    }
}