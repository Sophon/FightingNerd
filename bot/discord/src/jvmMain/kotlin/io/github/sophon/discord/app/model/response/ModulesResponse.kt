package io.github.sophon.discord.app.model.response

data class ModulesResponse(
    val moduleList: List<Module>,
): BotResponse {
    data class Module(
        val name: String,
        val url: String,
        val gameList: List<String>,
    )
}
