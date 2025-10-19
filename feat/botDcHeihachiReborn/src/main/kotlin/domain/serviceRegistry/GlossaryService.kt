package domain.serviceRegistry

import com.example.core.domain.Result
import domain.EmbedBuilder
import usecase.SearchGlossaryUseCase
import usecase.StartGlossaryUseCase

internal class GlossaryService(
    private val startGlossaryUseCase: StartGlossaryUseCase,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
    private val embedBuilder: EmbedBuilder,
): RegisteredService {
    override val command: Command = Command.GL
    override val serviceInfo = ServiceInfo(
        name = "Infil Glossary",
        url = "https://glossary.infil.net/",
        iconUrl = "https://i.imgur.com/0cnTzNk.png",
    )

    override suspend fun start() {
        startGlossaryUseCase.invoke()
    }

    override suspend fun execute(
        command: Command,
        vararg args: String
    ): dev.kord.rest.builder.message.EmbedBuilder.() -> Unit {
        val result = searchGlossaryUseCase.invoke(
            args.joinToString(" ")
        )

        return when (result) {
            is Result.Success -> {
                embedBuilder.glossaryEmbed(result.data)
            }
            is Result.Error -> {
                embedBuilder.errorEmbed(result.error)
            }
        }
    }
}