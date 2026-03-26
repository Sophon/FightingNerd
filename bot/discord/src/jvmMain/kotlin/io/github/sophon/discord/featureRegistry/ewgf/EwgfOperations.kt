package io.github.sophon.discord.featureRegistry.ewgf

internal object EwgfOperations {
    sealed class Operation {
        data object Help: Operation()
        data class Register(val polarisId: String): Operation()
        data object Data: Operation()
        data class Update(val polarisId: String): Operation()
        data object Unregister: Operation()
    }

    fun findOperation(alias: String, data: String): Operation? {
        return dictionary.firstOrNull { (aliases, _) -> alias in aliases }
            ?.second?.invoke(data)
    }

    private val dictionary = listOf(
        listOf("+", "register") to { polarisId: String -> Operation.Register(polarisId) },
        listOf("update") to { polarisId: String -> Operation.Update(polarisId) },
        listOf("-", "unregister") to { _: String -> Operation.Unregister },
        listOf("?", "help") to { _: String -> Operation.Help }
    )
}
