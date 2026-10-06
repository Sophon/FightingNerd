package io.github.sophon.testsuite.hex

import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.verify.assertEmpty
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class HexPortTest {

    @Test
    fun `inPort declares only interfaces`() {
        HexScope.filesIn("inPort")
            .flatMap { it.topLevelDeclarations() }
            .filterNot { it is KoInterfaceDeclaration }
            .assertEmpty(additionalMessage = "inPort may only declare interfaces")
    }

    @Test
    fun `inPort interfaces end with UseCase`() {
        HexScope.filesIn("inPort")
            .flatMap { it.interfaces(includeNested = false) }
            .assertTrue(additionalMessage = "inPort interfaces must end with `UseCase`") { it.name.endsWith("UseCase") }
    }

    @Test
    fun `outPort declares only interfaces`() {
        HexScope.filesIn("app.outPort")
            .flatMap { it.topLevelDeclarations() }
            .filterNot { it is KoInterfaceDeclaration }
            .assertEmpty(additionalMessage = "outPort may only declare interfaces")
    }

    @Test
    fun `outPort interfaces end with Port`() {
        HexScope.filesIn("app.outPort")
            .flatMap { it.interfaces(includeNested = false) }
            .assertTrue(additionalMessage = "outPort interfaces must end with `Port`") { it.name.endsWith("Port") }
    }
}
