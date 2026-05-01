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

    val projectDir = layout.projectDirectory

    doLast {
        // Automatically discover all feat modules
        val featDir = projectDir.dir("feat").asFile
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
        var excludedCount = 0

        featModules.forEach { module ->
            val useCaseDir = projectDir.dir("feat/$module/src/commonMain/kotlin").asFile

            if (!useCaseDir.exists()) {
                println("⚠️  Warning: Module $module commonMain directory not found")
                return@forEach
            }

            // Find all files in usecase directories
            useCaseDir.walk()
                .filter { it.isFile && it.path.contains("/usecase/") && it.extension == "kt" }
                .forEach { useCaseFile ->
                    val useCaseName = useCaseFile.nameWithoutExtension

                    // Check if file has @ExcludeFromCoverage annotation
                    val isExcluded = useCaseFile.readText().contains("@ExcludeFromCoverage")

                    if (isExcluded) {
                        excludedCount++
                        println("⊘ ${module}: $useCaseName (excluded)")
                        return@forEach
                    }

                    totalUseCases++

                    // Expected test file name
                    val testFileName = useCaseName + "Test.kt"

                    // Get the package path after kotlin/
                    val relativePath = useCaseFile.relativeTo(useCaseDir)
                    val testPath = projectDir.dir("feat/$module/src/commonTest/kotlin/${relativePath.parent}").file(testFileName).asFile

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
        if (excludedCount > 0) {
            println("  Excluded: $excludedCount")
        }
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

//region HEXAGONAL ARCH TEST
data class TopLevelDeclaration(
    val kind: String,
    val name: String,
    val visibility: String?,
    val lineNumber: Int,
)

val hexagonalDeclarationRegex = Regex(
    """^(?:(public|internal|private)\s+)?""" +
            """(?:(?:abstract|open|final|sealed|data|enum|value|inline|external|expect|actual|""" +
            """companion|inner|annotation|const|lateinit|override|operator|infix|suspend|tailrec|""" +
            """fun(?=\s+interface))\s+)*""" +
            """(class|interface|object|fun|val|var|typealias)\s+([A-Za-z_][\w.]*)"""
)

fun stripBlockComments(source: String): String {
    val result = StringBuilder()
    var i = 0
    while (i < source.length) {
        if (i + 1 < source.length && source[i] == '/' && source[i + 1] == '*') {
            i += 2
            while (i + 1 < source.length) {
                if (source[i] == '*' && source[i + 1] == '/') break
                if (source[i] == '\n') result.append('\n')
                i++
            }
            i = (i + 2).coerceAtMost(source.length)
        } else {
            result.append(source[i])
            i++
        }
    }
    return result.toString()
}

fun stripRawStringLiterals(source: String): String {
    val result = StringBuilder()
    var i = 0
    while (i < source.length) {
        if (i + 2 < source.length && source[i] == '"' && source[i + 1] == '"' && source[i + 2] == '"') {
            i += 3
            while (i + 2 < source.length) {
                if (source[i] == '"' && source[i + 1] == '"' && source[i + 2] == '"') break
                if (source[i] == '\n') result.append('\n')
                i++
            }
            i = (i + 3).coerceAtMost(source.length)
        } else {
            result.append(source[i])
            i++
        }
    }
    return result.toString()
}

fun stripLeadingAnnotations(line: String): String {
    var s = line.trimStart()
    while (s.startsWith("@")) {
        var i = 1
        while (i < s.length && (s[i].isLetterOrDigit() || s[i] == '_' || s[i] == '.' || s[i] == ':')) {
            i++
        }
        if (i < s.length && s[i] == '(') {
            var depth = 1
            i++
            while (i < s.length && depth > 0) {
                when (s[i]) {
                    '(' -> depth++
                    ')' -> depth--
                }
                i++
            }
        }
        s = s.substring(i).trimStart()
    }
    return s
}

fun findTopLevelDeclarations(file: File): List<TopLevelDeclaration> {
    val source = stripRawStringLiterals(stripBlockComments(file.readText()))
    val lines = source.lines()
    val declarations = mutableListOf<TopLevelDeclaration>()

    for (index in lines.indices) {
        val withoutComment = lines[index].substringBefore("//")
        if (withoutComment.isBlank() || withoutComment[0].isWhitespace()) continue

        val stripped = stripLeadingAnnotations(withoutComment)
        if (stripped.isBlank()) continue

        val match = hexagonalDeclarationRegex.find(stripped) ?: continue
        val visibility = match.groupValues[1].takeIf { it.isNotEmpty() }
        val kind = match.groupValues[2]
        val name = match.groupValues[3]

        declarations.add(
            TopLevelDeclaration(
                kind = kind,
                name = name,
                visibility = visibility,
                lineNumber = index + 1,
            )
        )
    }

    return declarations
}

tasks.register("testArchHexagonal") {
    group = "verification"
    description = "Verifies hexagonal architecture: integration package members must be public; everything else must be internal or private"

    val projectDir = layout.projectDirectory
    val testSourceSetRegex = Regex("/src/[^/]*[Tt]est[^/]*/")

    doLast {
        val modulesToScan = mutableListOf<File>()

        // bot submodules (e.g. bot/discord)
        val botDir = projectDir.dir("bot").asFile
        if (botDir.exists()) {
            botDir.listFiles()
                ?.filter { it.isDirectory && it.name.startsWith(".").not() }
                ?.forEach { modulesToScan.add(it) }
        }

        // composeApp (single module, no submodules)
        val composeApp = projectDir.dir("composeApp").asFile
        if (composeApp.exists()) modulesToScan.add(composeApp)

        // feat submodules
        val featDir = projectDir.dir("feat").asFile
        if (featDir.exists()) {
            featDir.listFiles()
                ?.filter { it.isDirectory && it.name.startsWith(".").not() }
                ?.forEach { modulesToScan.add(it) }
        }

        if (modulesToScan.isEmpty()) {
            println("⚠️  No modules to scan")
            return@doLast
        }

        println("🔍 Scanning modules: ${modulesToScan.joinToString(", ") { it.name }}\n")

        val violations = mutableListOf<String>()
        var totalDeclarations = 0
        var validDeclarations = 0

        modulesToScan.forEach module@{ module ->
            val srcDir = File(module, "src")
            if (srcDir.exists().not()) return@module

            srcDir.walk()
                .filter { it.isFile && it.extension == "kt" }
                .filter { testSourceSetRegex.containsMatchIn(it.invariantSeparatorsPath).not() }
                .forEach { ktFile ->
                    val isIntegration = ktFile.invariantSeparatorsPath.contains("/integration/")
                    val relativePath = ktFile.relativeTo(projectDir.asFile).invariantSeparatorsPath

                    findTopLevelDeclarations(ktFile).forEach { decl ->
                        totalDeclarations++
                        val isPublic = decl.visibility == null || decl.visibility == "public"

                        if (isIntegration) {
                            if (isPublic) {
                                validDeclarations++
                            } else {
                                violations.add(
                                    "${module.name}: ${decl.kind} ${decl.name} at $relativePath:${decl.lineNumber} " +
                                            "→ integration members must be public, found '${decl.visibility}'"
                                )
                            }
                        } else {
                            if (isPublic) {
                                val found = decl.visibility ?: "no modifier (public by default)"
                                violations.add(
                                    "${module.name}: ${decl.kind} ${decl.name} at $relativePath:${decl.lineNumber} " +
                                            "→ adapters must be internal or private, found $found"
                                )
                            } else {
                                validDeclarations++
                            }
                        }
                    }
                }
        }

        println("\n========================================")
        println("📊 Hexagonal Architecture Check:")
        println("  Total Top-Level Declarations: $totalDeclarations")
        println("  Valid: $validDeclarations")
        println("  Violations: ${violations.size}")
        println("========================================\n")

        if (violations.isNotEmpty()) {
            println("❌ Hexagonal architecture violations:")
            violations.forEach { println("  - $it") }
            throw GradleException("Hexagonal architecture check failed! ${violations.size} violation(s) found.")
        } else {
            println("✅ All declarations follow hexagonal architecture rules!")
        }
    }
}
//endregion
