package io.github.sophon.testsuite.hex

import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

class HexDependencyTest {

    @Test
    fun `inPort and model do not depend on app or adapter`() {
        (HexScope.filesIn("inPort") + HexScope.filesIn("model"))
            .flatMap { it.imports }
            .assertFalse(additionalMessage = "inPort and model must not import from app or adapter") {
                it.isFromLayer("app") || it.isFromLayer("adapter")
            }
    }

    @Test
    fun `app does not depend on adapter`() {
        HexScope.filesIn("app")
            .flatMap { it.imports }
            .assertFalse(additionalMessage = "app must not import from adapter") { it.isFromLayer("adapter") }
    }

    @Test
    fun `inbound adapters do not depend on services`() {
        HexScope.filesIn("adapter.inbound")
            .flatMap { it.imports }
            .assertFalse(additionalMessage = "Inbound adapters must go through inPort, not app.service") {
                it.isFromLayer("app.service")
            }
    }
}
