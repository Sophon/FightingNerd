package io.github.sophon.testsuite.hex

import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class HexStructureTest {

    @Test
    fun `every file resides in its module root package`() {
        HexScope.files.assertTrue(additionalMessage = "File package must start with the module root package") {
            it.layer != null
        }
    }

    @Test
    fun `module root contains only the allowed packages`() {
        HexScope.files.assertTrue(
            additionalMessage = "Allowed: root files, files directly in `app`, and ${LAYER_PACKAGES.joinToString()}",
        ) { file ->
            val allowed = (file.layer in FILE_LEVEL_PACKAGES || LAYER_PACKAGES.any { file.resideInLayer(it) })
            allowed
        }
    }

    @Test
    fun `only root files, model and inPort are public`() {
        HexScope.files
            .filterNot { it.layer == "" || it.resideInLayer("model") || it.resideInLayer("inPort") }
            .flatMap { it.topLevelDeclarations() }
            .assertFalse(additionalMessage = "Must be internal or private") { it.hasPublicOrDefaultModifier }
    }
}


/**
 * Packages that may hold files directly, but no sub-packages beyond [LAYER_PACKAGES].
 */
private val FILE_LEVEL_PACKAGES = setOf("", "app")

/**
 * Packages that may hold files, including in their own sub-packages.
 */
private val LAYER_PACKAGES = listOf(
    "adapter.inbound",
    "adapter.outbound",
    "app.model",
    "app.outPort",
    "app.service",
    "app.util",
    "docs",
    "inPort",
    "model",
)
