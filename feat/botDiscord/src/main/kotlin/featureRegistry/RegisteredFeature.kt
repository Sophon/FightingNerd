package featureRegistry

import dev.kord.rest.builder.message.EmbedBuilder

interface RegisteredFeature {
    val mainCommand: Command //TODO: refactor to List<Command>
    val serviceInfo: ServiceInfo
    val slashCommands: List<SlashCommand>

    suspend fun start()

    /**
     * Service:Command is 1:n
     */
    suspend fun execute(
        command: Command,
        vararg args: String
    ): EmbedBuilder.() -> Unit

    fun buildQuery(
        args: Map<String, String>,
        command: Command
    ): String {
        val slashCommand = slashCommands.first { it.name == command }
        return slashCommand.arguments
            .mapNotNull { args[it.name] }
            .joinToString(" ")
            .trim()
    }
}

data class ServiceInfo(
    val name: String,
    val url: String,
    val iconUrl: String? = null,
)

data class SlashCommand(
    val name: Command,
    val description: String,
    val arguments: List<Argument>
) {
    data class Argument(
        val name: String,
        val description: String,
        val isRequired: Boolean = true,
    )
}
