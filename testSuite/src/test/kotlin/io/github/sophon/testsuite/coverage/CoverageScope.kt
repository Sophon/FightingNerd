package io.github.sophon.testsuite.coverage

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

/**
 * Production files that must have a unit test, and the test files that can satisfy them.
 * Scans every source set of the [modules]; files containing `@ExcludeFromCoverage` are left out.
 */
internal object CoverageScope {
    /**
     * Modules held to the coverage rules - add a module here once its tests are written.
     */
    val modules = listOf(
        "composeApp",
        "feat/admin",
        "feat/ewgf",
        "feat/glossaryInfil",
        "feat/stats",
        "feat/wiki",
        BOT_MODULE,
    )

    val files: List<KoFileDeclaration> by lazy {
        val files = modules
            .flatMap { module -> Konsist.scopeFromProduction(moduleName = module).files }
            .filterNot { it.text.contains(EXCLUDE_ANNOTATION) }
        files
    }

    val services: List<KoFileDeclaration> by lazy {
        val services = files.filter { SERVICE_PACKAGE in it.packageSegments }
        services
    }

    val adapterMappers: List<KoFileDeclaration> by lazy {
        val mappers = files.filter { file ->
            val isMapper = (file.name.endsWith(MAPPER) || file.name.endsWith(MAPPERS))
            (ADAPTER_PACKAGE in file.packageSegments && isMapper)
        }
        mappers
    }

    val utils: List<KoFileDeclaration> by lazy {
        val utils = files.filter { UTIL_PACKAGE in it.packageSegments }
        utils
    }

    /**
     * `module:package.Name` of every test file
     */
    val testFileKeys: Set<String> by lazy {
        val keys = modules
            .flatMap { module -> Konsist.scopeFromTest(moduleName = module).files }
            .map { "${it.moduleName}:${it.packagee?.name.orEmpty()}.${it.name}" }
            .toSet()
        keys
    }
}

/**
 * Whether a `<Name>Test.kt` sits in the same package of the module's test source set.
 * `bot/discord` tests live under `botdiscord` instead of `discord`.
 */
internal fun KoFileDeclaration.hasTest(): Boolean {
    val packageName = packagee?.name.orEmpty()
    val testPackage = if (moduleName == BOT_MODULE) {
        packageName.replaceFirst(BOT_MAIN_PACKAGE, BOT_TEST_PACKAGE)
    } else {
        packageName
    }
    val hasTest = "$moduleName:$testPackage.${name}$TEST_SUFFIX" in CoverageScope.testFileKeys
    return hasTest
}

private val KoFileDeclaration.packageSegments: List<String>
    get() = packagee?.name?.split(".").orEmpty()


private const val BOT_MODULE = "bot/discord"
private const val BOT_MAIN_PACKAGE = "io.github.sophon.discord"
private const val BOT_TEST_PACKAGE = "io.github.sophon.botdiscord"

private const val SERVICE_PACKAGE = "service"
private const val ADAPTER_PACKAGE = "adapter"
private const val UTIL_PACKAGE = "util"
private const val MAPPER = "Mapper"
private const val MAPPERS = "Mappers"
private const val TEST_SUFFIX = "Test"
private const val EXCLUDE_ANNOTATION = "@ExcludeFromCoverage"
