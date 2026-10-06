package io.github.sophon.testsuite.coverage

import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class CoverageTest {

    @Test
    fun `every service has a test`() {
        CoverageScope.services.assertTrue(additionalMessage = "Service needs a `<Name>Test.kt` in the same package") {
            it.hasTest()
        }
    }

    @Test
    fun `every adapter mapper has a test`() {
        CoverageScope.adapterMappers.assertTrue(additionalMessage = "Adapter mapper needs a `<Name>Test.kt` in the same package") {
            it.hasTest()
        }
    }

    @Test
    fun `every util has a test`() {
        CoverageScope.utils.assertTrue(additionalMessage = "Util needs a `<Name>Test.kt` in the same package") {
            it.hasTest()
        }
    }
}
