package io.github.sophon.testsuite.hex

import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoParameterDeclaration
import com.lemonappdev.konsist.api.verify.assertEmpty
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class HexServiceTest {

    private val serviceFiles = HexScope.filesIn("app.service")
    private val serviceClasses = serviceFiles.flatMap { it.classes(includeNested = false, includeLocal = false) }
    private val serviceInterfaces = serviceFiles.flatMap { it.interfaces(includeNested = false) }

    @Test
    fun `service declares only classes and interfaces`() {
        serviceFiles
            .flatMap { it.topLevelDeclarations() }
            .filterNot { it is KoClassDeclaration || it is KoInterfaceDeclaration }
            .assertEmpty(additionalMessage = "service may only declare classes and interfaces")
    }

    @Test
    fun `service interfaces end with Service`() {
        serviceInterfaces.assertTrue(additionalMessage = "Service interfaces must end with `Service`") {
            it.name.endsWith(SERVICE)
        }
    }

    @Test
    fun `service classes end with Service or ServiceImpl`() {
        serviceClasses.assertTrue(additionalMessage = "Service classes must end with `Service` or `ServiceImpl`") {
            it.name.endsWith(SERVICE) || it.name.endsWith(SERVICE_IMPL)
        }
    }

    @Test
    fun `ServiceImpl implements the Service interface declared in its file`() {
        serviceClasses
            .filter { it.name.endsWith(SERVICE_IMPL) }
            .assertTrue(additionalMessage = "`XServiceImpl` must implement `XService` from the same file") { impl ->
                val interfaceName = impl.name.removeSuffix(IMPL)
                val declaredInFile = impl.containingFile.interfaces(includeNested = false).any { it.name == interfaceName }
                val implemented = (declaredInFile && impl.parents().any { it.name == interfaceName })
                implemented
            }
    }

    @Test
    fun `Service interface has its Impl in the same file`() {
        serviceInterfaces.assertTrue(additionalMessage = "`XService` interface needs `XServiceImpl` in the same file") { serviceInterface ->
            val implName = "${serviceInterface.name}$IMPL"
            val hasImpl = serviceInterface.containingFile
                .classes(includeNested = false, includeLocal = false)
                .any { it.name == implName }
            hasImpl
        }
    }

    @Test
    fun `Service class implements the inPort UseCase of the same name`() {
        serviceClasses
            .filter { it.name.endsWith(SERVICE) }
            .assertTrue(additionalMessage = "`XService` must implement `XUseCase` from inPort") { service ->
                val useCaseName = "${service.name.removeSuffix(SERVICE)}$USE_CASE"
                val useCaseExists = useCaseName in HexScope.inPortTypeNamesIn(service.containingFile.moduleName)
                val implemented = (useCaseExists && service.parents().any { it.name == useCaseName })
                implemented
            }
    }

    @Test
    fun `every UseCase has a Service of the same name`() {
        HexScope.inPortFiles
            .flatMap { it.interfaces(includeNested = false) }
            .assertTrue(additionalMessage = "`XUseCase` needs an `XService` in app.service") { useCase ->
                val serviceName = "${useCase.name.removeSuffix(USE_CASE)}$SERVICE"
                val hasService = serviceName in HexScope.typeNamesIn(useCase.containingFile.moduleName, "app.service")
                hasService
            }
    }

    @Test
    fun `service depends only on services, outbound ports, utils and stdlib`() {
        serviceClasses
            .flatMap { it.primaryConstructor?.parameters.orEmpty() }
            .assertTrue(
                additionalMessage = "Service constructor may only take services, outbound ports, utils or stdlib (`kotlin.*`) types",
            ) { parameter ->
                val modulePath = parameter.containingFile.moduleName
                val allowedTypes = DEPENDENCY_LAYERS.flatMap { HexScope.typeNamesIn(modulePath, it) }
                val allowed = (parameter.type.bareSourceType in allowedTypes || parameter.isStdlibType())
                allowed
            }
    }
}


/**
 * Whether the parameter's type comes from the Kotlin stdlib - imported from a `kotlin.` package,
 * or not imported at all and therefore default-imported (`String`, `List`, ...).
 * A non-stdlib wildcard import could be hiding where an un-imported type comes from, so it doesn't count then.
 */
private fun KoParameterDeclaration.isStdlibType(): Boolean {
    val typeName = type.bareSourceType
    val imports = containingFile.imports
    val typeImport = imports.firstOrNull { it.name.endsWith(".$typeName") }
    val isStdlib = if (typeImport == null) {
        imports.none { it.isWildcard && it.name.startsWith(STDLIB_PACKAGE).not() }
    } else {
        typeImport.name.startsWith(STDLIB_PACKAGE)
    }
    return isStdlib
}


private const val SERVICE = "Service"
private const val IMPL = "Impl"
private const val SERVICE_IMPL = "$SERVICE$IMPL"
private const val USE_CASE = "UseCase"
private const val STDLIB_PACKAGE = "kotlin."

private val DEPENDENCY_LAYERS = listOf("app.service", "app.outPort", "app.util")
