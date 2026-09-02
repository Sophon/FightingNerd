import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.files("config/detekt.yml"))
        autoCorrect = false
        source.setFrom(
            files(
                "src/commonMain/kotlin",
                "src/androidMain/kotlin",
                "src/iosMain/kotlin",
                "src/jvmMain/kotlin",
                "src/main/kotlin",
                "src/commonTest/kotlin",
                "src/androidUnitTest/kotlin",
                "src/jvmTest/kotlin",
            ).filter { it.exists() }
        )
    }

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
tasks.register<TestReport>("testUnit") {
    destinationDirectory.set(file("${layout.buildDirectory.get().asFile}/reports/allTests"))

    testResults.from(subprojects.flatMap { subproject ->
        listOf("jvmTest", "testDebugUnitTest").mapNotNull { taskName ->
            (subproject.tasks.findByName(taskName) as? Test)?.binaryResultsDirectory
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
        data class CoverageRoot(val label: String, val mainDir: File, val testDir: File)

        val roots = mutableListOf<CoverageRoot>()

        // feat/* modules — auto-discover, commonMain/commonTest, same package root
        val featDir = projectDir.dir("feat").asFile
        featDir.listFiles()
            ?.filter { it.isDirectory && it.name.startsWith(".").not() }
            ?.forEach { module ->
                roots.add(
                    CoverageRoot(
                        label = "feat/${module.name}",
                        mainDir = File(module, "src/commonMain/kotlin"),
                        testDir = File(module, "src/commonTest/kotlin"),
                    )
                )
            }

        // composeApp — commonMain/commonTest, same package root
        roots.add(
            CoverageRoot(
                label = "composeApp",
                mainDir = projectDir.dir("composeApp/src/commonMain/kotlin").asFile,
                testDir = projectDir.dir("composeApp/src/commonTest/kotlin").asFile,
            )
        )

        // bot/discord — jvmMain/jvmTest; main pkg root `discord`, test pkg root `botdiscord`
        roots.add(
            CoverageRoot(
                label = "bot/discord",
                mainDir = projectDir.dir("bot/discord/src/jvmMain/kotlin/io/github/sophon/discord").asFile,
                testDir = projectDir.dir("bot/discord/src/jvmTest/kotlin/io/github/sophon/botdiscord").asFile,
            )
        )

        if (roots.isEmpty()) {
            println("⚠️  No coverage roots configured")
            return@doLast
        }

        println("🔍 Scanning modules: ${roots.joinToString(", ") { it.label }}\n")

        val missingTests = mutableListOf<String>()
        var totalUseCases = 0
        var testedUseCases = 0
        var excludedCount = 0

        roots.forEach root@{ root ->
            if (root.mainDir.exists().not()) {
                println("⚠️  Warning: ${root.label} main dir not found at ${root.mainDir}")
                return@root
            }

            root.mainDir.walk()
                .filter { it.isFile && it.path.contains("/usecase/") && it.extension == "kt" }
                .forEach useCase@{ useCaseFile ->
                    val useCaseName = useCaseFile.nameWithoutExtension

                    val isExcluded = useCaseFile.readText().contains("@ExcludeFromCoverage")

                    if (isExcluded) {
                        excludedCount++
                        println("⊘ ${root.label}: $useCaseName (excluded)")
                        return@useCase
                    }

                    totalUseCases++

                    val testFileName = useCaseName + "Test.kt"
                    val relativePath = useCaseFile.relativeTo(root.mainDir)
                    val testPath = File(root.testDir, "${relativePath.parent}/$testFileName")

                    if (testPath.exists().not()) {
                        missingTests.add("${root.label}: $useCaseName -> Missing")
                    } else {
                        testedUseCases++
                        println("✓ ${root.label}: $useCaseName")
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

object HexagonalArchScanner {
    private val declarationRegex = Regex(
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

            val match = declarationRegex.find(stripped) ?: continue
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

                    HexagonalArchScanner.findTopLevelDeclarations(ktFile).forEach { decl ->
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
    }
}
//endregion
