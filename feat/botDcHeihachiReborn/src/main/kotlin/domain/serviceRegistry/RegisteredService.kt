package domain.serviceRegistry

import dev.kord.rest.builder.message.EmbedBuilder

interface RegisteredService {
    val command: Command
    val serviceInfo: ServiceInfo

    suspend fun start()

    suspend fun execute(
        command: Command,
        vararg args: String
    ): EmbedBuilder.() -> Unit
}

data class ServiceInfo(
    val name: String,
    val url: String,
    val iconUrl: String? = null,
)

enum class Command {
    GL,
    FD,
}