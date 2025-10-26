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
tasks.register<TestReport>("testReport") {
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