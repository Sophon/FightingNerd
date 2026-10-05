package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.URL_SCRIPT_LOBBY
import io.github.sophon.discord.URL_STEAM_LOBBY
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.UserRequest
import io.github.sophon.discord.app.port.outbound.LoadConfigPort
import io.github.sophon.wiki.model.wiki.Game
import kotlin.time.Duration.Companion.seconds

internal interface CoreBotService {
    fun createTipResponse(): Result<BotResponse.CoreResponse, BotError>

    fun createHelpResponse(): Result<BotResponse.CoreResponse, BotError>

    fun createCommandsResponse(): Result<BotResponse.CoreResponse, BotError>

    fun createRepoResponse(): Result<BotResponse.PlainText, BotError>

    fun createInviteResponse(): Result<BotResponse.PlainText, BotError>

    fun createModulesResponse(): Result<BotResponse.ModulesResponse, BotError>

    fun createSteamLobbyResponse(
        query: String,
        source: UserRequest.Source?,
    ): Result<BotResponse.SteamLobby, BotError>
}

internal class CoreBotServiceImpl(
    private val loadConfigPort: LoadConfigPort,
): CoreBotService {
    override fun createTipResponse(): Result<BotResponse.CoreResponse, BotError> {
        val response = BotResponse.CoreResponse(type = BotResponse.CoreResponse.Type.Tip)
        return Result.Success(response)
    }

    override fun createHelpResponse(): Result<BotResponse.CoreResponse, BotError> {
        val result = createHelpfulResponse(
            type = BotResponse.CoreResponse.Type.Help,
            linkedCommand = Command.Commands,
        )
        return result
    }

    override fun createCommandsResponse(): Result<BotResponse.CoreResponse, BotError> {
        val result = createHelpfulResponse(
            type = BotResponse.CoreResponse.Type.Commands,
            linkedCommand = Command.Help,
        )
        return result
    }

    override fun createRepoResponse(): Result<BotResponse.PlainText, BotError> {
        val response = BotResponse.PlainText(text = "Contribute to FightingNerd: $URL_REPO")
        return Result.Success(response)
    }

    override fun createInviteResponse(): Result<BotResponse.PlainText, BotError> {
        val response = BotResponse.PlainText(text = "FightingNerd bot invite: $URL_INVITE")
        return Result.Success(response)
    }

    override fun createModulesResponse(): Result<BotResponse.ModulesResponse, BotError> {
        val result = loadConfigPort.load()
            .map { discordConfig ->
                val moduleList = discordConfig.featureList
                    .asSequence()
                    .filter { it.isEnabled }
                    .flatMap { it.supportedGames }
                    .mapNotNull { gameId -> Game.fromId(gameId) }
                    .groupBy { it.wiki }
                    .map { (wiki, gameList) ->
                        BotResponse.ModulesResponse.Module(
                            name = wiki.displayName,
                            url = wiki.url,
                            gameList = gameList.map { it.displayName },
                        )
                    }
                    .toList()
                BotResponse.ModulesResponse(moduleList = moduleList)
            }
        return result
    }

    /**
     * Query is `[steamLobbyUrl] [password] [lobbyName]`; the lobby name can have spaces.
     * Button paths have no [source] to name the host.
     */
    override fun createSteamLobbyResponse(
        query: String,
        source: UserRequest.Source?,
    ): Result<BotResponse.SteamLobby, BotError> {
        val parts = query.split(" ")
        val steamLobbyUrl = parts[0]

        val result = when {
            (source == null) -> Result.Error(BotError.BotLogicError(Command.Join.name, query))
            (steamLobbyUrl.startsWith(URL_STEAM_LOBBY, ignoreCase = true).not()) -> {
                Result.Error(BotError.InvalidSteamLobbyUrl(steamLobbyUrl))
            }
            else -> {
                val joinUrl = "$URL_SCRIPT_LOBBY?target=$steamLobbyUrl"
                val response = BotResponse.SteamLobby(
                    hostName = source.username,
                    lobbyName = parts.drop(2).joinToString(" ").ifBlank { null },
                    password = parts.getOrNull(1),
                    buttonSet = BotResponse.ButtonSet(
                        buttonList = listOf(
                            BotResponse.EmbedButton(
                                label = "🎮 JOIN",
                                action = BotResponse.EmbedButton.Action.Url(joinUrl),
                            ),
                        ),
                        duration = EMBED_BUTTON_DURATION_INF.seconds,
                    ),
                )
                Result.Success(response)
            }
        }
        return result
    }

    //`Help` and `Commands`
    private fun createHelpfulResponse(
        type: BotResponse.CoreResponse.Type,
        linkedCommand: Command,
    ): Result<BotResponse.CoreResponse, BotError> {
        val response = BotResponse.CoreResponse(
            type = type,
            buttonSet = BotResponse.ButtonSet(
                buttonList = listOf(
                    BotResponse.EmbedButton(
                        label = linkedCommand.name,
                        action = BotResponse.EmbedButton.Action.Command(command = linkedCommand, query = ""),
                    ),
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            ),
        )
        return Result.Success(response)
    }
}
