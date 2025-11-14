plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
}

subprojects {
    tasks.withType<Test>().configureEach {
        val projectName = project.name

        testLogging {
            showExceptions = true
            showCauses = true
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }

        addTestListener(object : TestListener {
            var testClassCount = 0

            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}

            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent != null && suite.parent?.parent != null && result.testCount > 0) {
                    testClassCount++
                }

                if (suite.parent == null && result.testCount > 0) {
                    println("\n[$projectName]")
                    println("  Test Classes: $testClassCount")
                    println("  Test Functions: ${result.testCount}")
                    println("  ✅ Passed: ${result.successfulTestCount}")
                    println("  ❌ Failed: ${result.failedTestCount}")
                    println("  ⏭️ Skipped: ${result.skippedTestCount}\n")
                }
            }
        })
    }
}

// Aggregated test report task
tasks.register<TestReport>("unitTests") {
    destinationDirectory.set(file("${layout.buildDirectory.get().asFile}/reports/allTests"))

    testResults.from(subprojects.mapNotNull { subproject ->
        subproject.tasks.findByName("jvmTest")?.let { task ->
            (task as? Test)?.binaryResultsDirectory
        }
    })

    doLast {
        val reportPath = destinationDirectory.get().asFile.resolve("index.html")
        println("\n========================================")
        println("📊 Aggregated Test Report:")
        println("file://${reportPath.absolutePath}")
        println("========================================\n")
    }
}

// Verify all use cases have corresponding tests
tasks.register("testCoverage") {
    group = "verification"
    description = "Verifies that all use case files have corresponding unit tests"

    doLast {
        // Automatically discover all feat modules
        val featDir = file("feat")
        val featModules = featDir.listFiles()
            ?.filter { it.isDirectory && !it.name.startsWith(".") }
            ?.map { it.name }
            ?: emptyList()

        if (featModules.isEmpty()) {
            println("⚠️  No feature modules found in feat/ directory")
            return@doLast
        }

        println("🔍 Scanning modules: ${featModules.joinToString(", ")}\n")

        val missingTests = mutableListOf<String>()
        var totalUseCases = 0
        var testedUseCases = 0

        featModules.forEach { module ->
            val useCaseDir = file("feat/$module/src/commonMain/kotlin")

            if (!useCaseDir.exists()) {
                println("⚠️  Warning: Module $module commonMain directory not found")
                return@forEach
            }

            // Find all files in usecase directories
            useCaseDir.walk()
                .filter { it.isFile && it.path.contains("/usecase/") && it.extension == "kt" }
                .forEach { useCaseFile ->
                    totalUseCases++
                    val useCaseName = useCaseFile.nameWithoutExtension

                    // Expected test file name
                    val testFileName = useCaseName + "Test.kt"

                    // Get the package path after kotlin/ (fixed here!)
                    val relativePath = useCaseFile.relativeTo(useCaseDir)
                    val testPath = file("feat/$module/src/commonTest/kotlin/${relativePath.parent}/$testFileName")

                    if (!testPath.exists()) {
                        missingTests.add("$module: $useCaseName -> Missing")
                    } else {
                        testedUseCases++
                        println("✓ ${module}: $useCaseName")
                    }
                }
        }

        val coverage = if (totalUseCases > 0) {
            (testedUseCases.toDouble() / totalUseCases * 100).toInt()
        } else 0

        println("\n========================================")
        println("📊 Use Case Test Coverage:")
        println("  Total Use Cases: $totalUseCases")
        println("  Tested: $testedUseCases")
        println("  Missing: ${missingTests.size}")
        println("  Coverage: $coverage%")
        println("========================================\n")

        if (missingTests.isNotEmpty()) {
            println("❌ Missing tests for ${missingTests.size} use case(s):")
            missingTests.forEach { println("  - $it") }
            throw GradleException("Use case test verification failed! ${missingTests.size} use case(s) are missing tests.")
        } else {
            println("✅ All use cases have corresponding tests!")
        }
    }
}