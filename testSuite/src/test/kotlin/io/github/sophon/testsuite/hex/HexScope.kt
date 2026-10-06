package io.github.sophon.testsuite.hex

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoImportDeclaration
import com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider

/**
 * A feature module checked by the hexagonal architecture tests.
 * [rootPackage] is where the module's root files (FeatureInfo, DI module) live and where the layer packages start.
 */
internal data class HexModule(
    val path: String,
    val rootPackage: String,
)

/**
 * Production sources of every module that has migrated to the hexagonal layout.
 * Add a module here once it's migrated.
 */
internal object HexScope {
    val modules = listOf(
        HexModule(path = "feat/admin", rootPackage = "io.github.sophon"),
        HexModule(path = "feat/ewgf", rootPackage = "io.github.sophon"),
        HexModule(path = "feat/glossaryInfil", rootPackage = "io.github.sophon.glossaryinfil"),
        HexModule(path = "feat/stats", rootPackage = "io.github.sophon"),
        HexModule(path = "feat/wiki", rootPackage = "io.github.sophon.wiki"),
    )

    val files: List<KoFileDeclaration> by lazy {
        val scopes = modules.map { Konsist.scopeFromProduction(moduleName = it.path) }
        val files = scopes.reduce { acc, scope -> acc + scope }.files
        files
    }

    fun filesIn(layer: String): List<KoFileDeclaration> {
        val filtered = files.filter { it.resideInLayer(layer) }
        return filtered
    }

    /**
     * Names of the top-level classes, interfaces and objects declared in [layer] of the module at [modulePath].
     */
    fun typeNamesIn(modulePath: String, layer: String): Set<String> {
        val names = filesIn(layer)
            .filter { it.moduleName == modulePath }
            .flatMap { file ->
                val types = (
                    file.classes(includeNested = false, includeLocal = false) +
                        file.interfaces(includeNested = false) +
                        file.objects(includeNested = false)
                    )
                types.map { it.name }
            }
            .toSet()
        return names
    }
}

internal fun KoFileDeclaration.topLevelDeclarations(): List<KoVisibilityModifierProvider> {
    val declarations = (
        classes(includeNested = false, includeLocal = false) +
            interfaces(includeNested = false) +
            objects(includeNested = false) +
            functions(includeNested = false, includeLocal = false) +
            properties(includeNested = false) +
            typeAliases
        )
    return declarations
}

internal val KoFileDeclaration.rootPackage: String
    get() = HexScope.modules.first { it.path == moduleName }.rootPackage

/**
 * Package relative to the module root - `""` for root files, `app.service`, `adapter.outbound.ktor` etc.
 * `null` when the file sits outside the module root package.
 */
internal val KoFileDeclaration.layer: String?
    get() {
        val packageName = packagee?.name.orEmpty()
        val layer = when {
            packageName == rootPackage -> ""
            packageName.startsWith("$rootPackage.") -> packageName.removePrefix("$rootPackage.")
            else -> null
        }
        return layer
    }

/**
 * Whether the file is in [layer] or one of its sub-packages.
 */
internal fun KoFileDeclaration.resideInLayer(layer: String): Boolean {
    val fileLayer = this.layer ?: return false
    val resides = (fileLayer == layer || fileLayer.startsWith("$layer."))
    return resides
}

/**
 * Whether the import points into [layer] of the importing file's module.
 */
internal fun KoImportDeclaration.isFromLayer(layer: String): Boolean = name.startsWith("${containingFile.rootPackage}.$layer.")
