package io.github.sophon.discord.domain

data class SupportedCommand(
    val command: Command,
    val description: String,
    val arguments: List<Argument> = listOf(),
) {
    data class Argument(
        val name: String,
        val description: String,
        val isRequired: Boolean = true,
    )
}