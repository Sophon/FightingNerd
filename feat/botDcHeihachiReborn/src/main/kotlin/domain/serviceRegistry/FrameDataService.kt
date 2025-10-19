package domain.serviceRegistry

import com.example.core.domain.Result
import domain.EmbedBuilder
import usecase.SearchFrameDataUseCase
import usecase.StartWikiUseCase

internal class FrameDataService(
    private val startWikiUseCase: StartWikiUseCase,
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
    private val embedBuilder: EmbedBuilder,
): RegisteredService {
    override val mainCommand: Command = Command.FD
    override val serviceInfo = ServiceInfo(
        name = "Wavu Wiki",
        url = "https://wavu.wiki/",
        iconUrl = "https://i.imgur.com/0cnTzNk.png",
    )
    override val slashCommands: List<SlashCommand> = listOf(
        SlashCommand(
            name = Command.FD,
            description = "Tekken 8 frame data",
            arguments = listOf(
                SlashCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SlashCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move",
                )
            )
        )
    )

    override suspend fun start() {
        startWikiUseCase.invoke()
    }

    override suspend fun execute(
        command: Command,
        vararg args: String
    ): dev.kord.rest.builder.message.EmbedBuilder.() -> Unit {
        val result = searchFrameDataUseCase.invoke(
            query = args.joinToString(" ")
        )

        return when (result) {
            is Result.Success -> {
                embedBuilder.moveEmbed(result.data)
            }
            is Result.Error -> {
                embedBuilder.errorEmbed(result.error)
            }
        }
    }
}


private const val KEY_CHAR_NAME = "character"
private const val KEY_MOVE = "move"