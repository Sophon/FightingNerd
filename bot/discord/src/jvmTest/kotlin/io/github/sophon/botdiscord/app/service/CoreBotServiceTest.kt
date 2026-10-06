package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.BOT_DATA_SOURCE
import io.github.sophon.discord.BOT_NAME
import io.github.sophon.discord.BuildKonfig
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.CoreResponse
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.SteamLobbyResponse
import io.github.sophon.discord.app.outPort.FeatureInfoPort
import io.github.sophon.discord.app.service.CoreBotServiceImpl
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class CoreBotServiceTest {
    //region static responses
    @Test
    fun `tip is a bot response`() {
        // given
        val expected = Result.Success(CoreResponse(type = CoreResponse.Type.Tip, dataSource = BOT_DATA_SOURCE))
        val service = coreBotService()

        // when
        val result = service.createTipResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `help links to commands`() {
        // given
        val expected = Result.Success(
            CoreResponse(
                type = CoreResponse.Type.Help,
                dataSource = BOT_DATA_SOURCE,
                buttonSet = commandButtonSet(Command.Commands),
            ),
        )
        val service = coreBotService()

        // when
        val result = service.createHelpResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `commands link to help`() {
        // given
        val expected = Result.Success(
            CoreResponse(
                type = CoreResponse.Type.Commands,
                dataSource = BOT_DATA_SOURCE,
                buttonSet = commandButtonSet(Command.Help),
            ),
        )
        val service = coreBotService()

        // when
        val result = service.createCommandsResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `repo response carries the repo url`() {
        // given
        val expected = Result.Success(PlainTextResponse(text = "Contribute to FightingNerd: $URL_REPO"))
        val service = coreBotService()

        // when
        val result = service.createRepoResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `invite response carries the invite url`() {
        // given
        val expected = Result.Success(PlainTextResponse(text = "FightingNerd bot invite: $URL_INVITE"))
        val service = coreBotService()

        // when
        val result = service.createInviteResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region createModulesResponse
    @Test
    fun `bot module comes first, followed by each enabled module once`() {
        // given
        val expected = Result.Success(
            ModulesResponse(
                moduleList = listOf(botModule, wikiModule, glossaryModule),
                dataSource = BOT_DATA_SOURCE,
            ),
        )
        val featureList = listOf(
            feature(name = WAVU_FEATURE),
            feature(name = DUSTLOOP_FEATURE),
            feature(name = GLOSSARY_FEATURE),
        )
        val service = coreBotService(featureList = featureList)

        // when
        val result = service.createModulesResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `disabled feature isn't listed`() {
        // given
        val expected = Result.Success(
            ModulesResponse(moduleList = listOf(botModule, wikiModule), dataSource = BOT_DATA_SOURCE),
        )
        val featureList = listOf(
            feature(name = WAVU_FEATURE),
            feature(name = GLOSSARY_FEATURE, isEnabled = false),
        )
        val service = coreBotService(featureList = featureList)

        // when
        val result = service.createModulesResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `feature without a module isn't listed`() {
        // given
        val expected = Result.Success(ModulesResponse(moduleList = listOf(botModule), dataSource = BOT_DATA_SOURCE))
        val service = coreBotService(featureList = listOf(feature(name = "Tekken Warehouse")))

        // when
        val result = service.createModulesResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed config load is returned`() {
        // given
        val expected = Result.Error(BotError.FileError("discordConfig.json"))
        val service = CoreBotServiceImpl(
            loadConfigPort = FakeLoadConfigPort(expected),
            featureInfoPort = FakeFeatureInfoPort(),
        )

        // when
        val result = service.createModulesResponse()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region createSteamLobbyResponse
    @Test
    fun `lobby without a source is a logic error`() {
        // given
        val service = coreBotService()

        // when
        val result = service.createSteamLobbyResponse(query = STEAM_LOBBY_URL, source = null)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.BotLogicError::class)
    }

    @Test
    fun `non-steam url is an invalid lobby`() {
        // given
        val service = coreBotService()

        // when
        val result = service.createSteamLobbyResponse(query = "https://steamcommunity.com/id/arslan", source = host)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.InvalidSteamLobbyUrl::class)
    }

    @Test
    fun `lobby url alone has no password or name`() {
        // given
        val expected = Result.Success(steamLobbyResponse(lobbyName = null, password = null))
        val service = coreBotService()

        // when
        val result = service.createSteamLobbyResponse(query = STEAM_LOBBY_URL, source = host)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `password and multi-word lobby name follow the url`() {
        // given
        val expected = Result.Success(steamLobbyResponse(lobbyName = "Friday night sets", password = "ewgf"))
        val service = coreBotService()

        // when
        val result = service.createSteamLobbyResponse(query = "$STEAM_LOBBY_URL ewgf Friday night sets", source = host)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `steam url is matched regardless of case`() {
        // given
        val upperCaseUrl = "STEAM://JOINLOBBY/586140/109775241137042824/76561198443042808"
        val service = coreBotService()

        // when
        val result = service.createSteamLobbyResponse(query = upperCaseUrl, source = host)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion


    private class FakeFeatureInfoPort: FeatureInfoPort {
        override fun getModule(featureName: String): ModulesResponse.Module? {
            val module = when (featureName) {
                WAVU_FEATURE,
                DUSTLOOP_FEATURE -> wikiModule
                GLOSSARY_FEATURE -> glossaryModule
                else -> null
            }
            return module
        }
    }

    private fun coreBotService(featureList: List<DiscordConfig.Feature> = emptyList()): CoreBotServiceImpl {
        val service = CoreBotServiceImpl(
            loadConfigPort = FakeLoadConfigPort(Result.Success(discordConfigOf(featureList = featureList))),
            featureInfoPort = FakeFeatureInfoPort(),
        )
        return service
    }
}

private fun feature(name: String, isEnabled: Boolean = true): DiscordConfig.Feature {
    val feature = DiscordConfig.Feature(name = name, isEnabled = isEnabled, supportedGames = emptyList())
    return feature
}

private fun commandButtonSet(command: Command): BotResponse.ButtonSet {
    val buttonSet = BotResponse.ButtonSet(
        buttonList = listOf(
            BotResponse.EmbedButton(
                label = command.name,
                action = BotResponse.EmbedButton.Action.Command(command = command, query = ""),
            ),
        ),
        duration = EMBED_BUTTON_DURATION_INF.seconds,
    )
    return buttonSet
}

private fun steamLobbyResponse(lobbyName: String?, password: String?): SteamLobbyResponse {
    val steamLobbyResponse = SteamLobbyResponse(
        hostName = host.username,
        lobbyName = lobbyName,
        password = password,
        buttonSet = BotResponse.ButtonSet(
            buttonList = listOf(
                BotResponse.EmbedButton(
                    label = "🎮 JOIN",
                    action = BotResponse.EmbedButton.Action.Url(
                        "https://Sophon.github.io/lobby.html?target=$STEAM_LOBBY_URL",
                    ),
                ),
            ),
            duration = EMBED_BUTTON_DURATION_INF.seconds,
        ),
    )
    return steamLobbyResponse
}


private const val WAVU_FEATURE = "Wavu Wiki"
private const val DUSTLOOP_FEATURE = "DustLoop Wiki"
private const val GLOSSARY_FEATURE = "Infil Glossary"
private const val STEAM_LOBBY_URL = "steam://joinlobby/586140/109775241137042824/76561198443042808"
private val host = UserRequest.Source(username = "arslan", id = "333333333333333333", channelId = "555555555555555555")
private val botModule = ModulesResponse.Module(name = BOT_NAME, url = URL_REPO, version = BuildKonfig.VERSION)
private val wikiModule = ModulesResponse.Module(
    name = "Wiki",
    url = "https://github.com/Sophon/FightingNerd/tree/main/feat/wiki",
    version = "2.3.0",
)
private val glossaryModule = ModulesResponse.Module(
    name = "Infil Glossary",
    url = "https://glossary.infil.net",
    version = "1.1.0",
)
