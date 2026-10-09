package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.BOT_DATA_SOURCE
import io.github.sophon.discord.BOT_NAME
import io.github.sophon.discord.BuildKonfig
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.URL_SCRIPT_LOBBY
import io.github.sophon.discord.URL_STEAM_LOBBY
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.CoreResponse
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.SteamLobbyResponse
import io.github.sophon.discord.app.outPort.FeatureInfoPort
import io.github.sophon.discord.app.outPort.LoadConfigPort
import kotlin.time.Duration.Companion.seconds

internal interface CoreBotService {
    fun createTipResponse(): Result<CoreResponse, BotError>

    fun createHelpResponse(): Result<CoreResponse, BotError>

    fun createCommandsResponse(): Result<CoreResponse, BotError>

    fun createRepoResponse(): Result<PlainTextResponse, BotError>

    fun createInviteResponse(): Result<PlainTextResponse, BotError>

    fun createModulesResponse(): Result<ModulesResponse, BotError>

    fun createSteamLobbyResponse(
        query: String,
        source: UserRequest.Source?,
    ): Result<SteamLobbyResponse, BotError>
}

internal class CoreBotServiceImpl(
    private val loadConfigPort: LoadConfigPort,
    private val featureInfoPort: FeatureInfoPort,
): CoreBotService {
    override fun createTipResponse(): Result<CoreResponse, BotError> {
        val response = CoreResponse(
            type = CoreResponse.Type.Tip,
            dataSource = BOT_DATA_SOURCE,
        )
        return Result.Success(response)
    }

    override fun createHelpResponse(): Result<CoreResponse, BotError> {
        val result = createHelpfulResponse(
            type = CoreResponse.Type.Help,
            linkedCommand = Command.Commands,
        )
        return result
    }

    override fun createCommandsResponse(): Result<CoreResponse, BotError> {
        val result = createHelpfulResponse(
            type = CoreResponse.Type.Commands,
            linkedCommand = Command.Help,
        )
        return result
    }

    override fun createRepoResponse(): Result<PlainTextResponse, BotError> {
        val response = PlainTextResponse(text = "Contribute to FightingNerd: $URL_REPO")
        return Result.Success(response)
    }

    override fun createInviteResponse(): Result<PlainTextResponse, BotError> {
        val response = PlainTextResponse(
            text = "- Discord 💬: $URL_INVITE\n" +
                    "- Android 🤖: <https://play.google.com/store/apps/details?id=io.github.sophon.fightingnerd>\n" +
                    "- iOS 🍏: <https://apps.apple.com/us/app/fighting-nerd/id6793185357>"
        )
        return Result.Success(response)
    }

    override fun createModulesResponse(): Result<ModulesResponse, BotError> {
        val result = loadConfigPort.load()
            .map { discordConfig ->
                val enabledModuleList = discordConfig.featureList
                    .filter { it.isEnabled }
                    .mapNotNull { feature -> featureInfoPort.getModule(feature.name) }
                    .distinct()
                val botModule = ModulesResponse.Module(
                    name = BOT_NAME,
                    url = URL_REPO,
                    version = BuildKonfig.VERSION,
                )
                ModulesResponse(
                    moduleList = listOf(botModule) + enabledModuleList,
                    dataSource = BOT_DATA_SOURCE,
                )
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
    ): Result<SteamLobbyResponse, BotError> {
        val parts = query.split(" ")
        val steamLobbyUrl = parts[0]

        val result = when {
            (source == null) -> Result.Error(BotError.BotLogicError(Command.Join.name, query))
            (steamLobbyUrl.startsWith(URL_STEAM_LOBBY, ignoreCase = true).not()) -> {
                Result.Error(BotError.InvalidSteamLobbyUrl(steamLobbyUrl))
            }
            else -> {
                val joinUrl = "$URL_SCRIPT_LOBBY?target=$steamLobbyUrl"
                val response = SteamLobbyResponse(
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
        type: CoreResponse.Type,
        linkedCommand: Command,
    ): Result<CoreResponse, BotError> {
        val response = CoreResponse(
            type = type,
            dataSource = BOT_DATA_SOURCE,
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
