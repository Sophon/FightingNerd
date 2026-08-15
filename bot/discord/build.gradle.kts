import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
}

val botVersionName = project.properties["bot.version.name"] as String

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass.set("io.github.sophon.discord.MainKt")
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":core"))
            implementation(project(":feat:admin"))
            implementation(project(":feat:glossaryInfil"))
            implementation(project(":feat:wikiWavu"))
            implementation(project(":feat:wikiSupercombo"))
            implementation(project(":feat:xko"))
            implementation(project(":feat:wikiDreamCancel"))
            implementation(project(":feat:wikiDustLoop"))
            implementation(project(":feat:wikiMizuumi"))
            implementation(project(":feat:wikiDragDown"))
            implementation(project(":feat:ewgf"))
            implementation(project(":feat:stats"))

            implementation(libs.napier)
            implementation(libs.kord)
            implementation(libs.kotlin.date.time)
            implementation(libs.kotlin.reflect)
            implementation(libs.ktor.java)
            implementation(libs.sqldelight.driver.sqlite)

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


val fatJar = tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Creates a fat JAR with all dependencies for JVM"

    archiveBaseName.set("discord-bot")
    archiveClassifier.set("all")

    val jvmTarget = kotlin.targets.getByName("jvm")
    from(jvmTarget.compilations.getByName("main").output)

    val runtimeClasspath = configurations.getByName("jvmRuntimeClasspath")
    dependsOn(runtimeClasspath)

    from({
        runtimeClasspath.map { if (it.isDirectory) it else zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "io.github.sophon.discord.MainKt"
    }
}

tasks.matching { it.name == "jvmRun" }.configureEach {
    (this as JavaExec).workingDir = rootProject.projectDir
}

buildkonfig {
    packageName = "io.github.sophon.discord"

    defaultConfigs {
        buildConfigField(STRING, "VERSION", botVersionName)
    }
}