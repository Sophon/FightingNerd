package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.ButtonEvent
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.frameData.CharacterId
import io.github.sophon.discord.app.model.frameData.MoveId
import io.github.sophon.discord.app.model.response.AliasResponse
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.RedirectResponse
import io.github.sophon.discord.app.outPort.ForwardPort
import io.github.sophon.discord.app.service.ProcessButtonEventService
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ProcessButtonEventServiceTest {
    //region expand
    @Test
    fun `expanded move is forced open without its details button`() = runTest {
        // given
        val expected = Result.Success(
            sf6Move.copy(
                forceExpand = true,
                buttonSet = BotResponse.ButtonSet(buttonList = listOf(videoButton)),
            ),
        )
        val service = processButtonEventService()

        // when
        val result = service(ButtonEvent.Expand(sf6MoveId))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `expanded move with only a details button has no buttons`() = runTest {
        // given
        val expected = Result.Success(ggstMove.copy(forceExpand = true, buttonSet = null))
        val service = processButtonEventService()

        // when
        val result = service(ButtonEvent.Expand(ggstMoveId))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `expand isn't recorded as usage`() = runTest {
        // given
        val statsPort = FakeStatsPort()
        val service = processButtonEventService(statsPort = statsPort)

        // when
        service(ButtonEvent.Expand(sf6MoveId))

        // then
        assertThat(statsPort.registeredList).isEmpty()
    }
    //endregion

    //region query
    @Test
    fun `query returns the move's frame data`() = runTest {
        // given
        val expected = Result.Success(sf6Move)
        val service = processButtonEventService()

        // when
        val result = service(ButtonEvent.Query(sf6MoveId))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `query is recorded as frame data for the move's game`() = runTest {
        // given
        val expected = (Command.Fd to Game.StreetFighter6)
        val statsPort = FakeStatsPort()
        val service = processButtonEventService(statsPort = statsPort)

        // when
        service(ButtonEvent.Query(sf6MoveId))

        // then
        assertThat(statsPort.registeredList).containsExactly(expected)
    }

    @Test
    fun `failed query is recorded as a failure`() = runTest {
        // given
        val statsPort = FakeStatsPort()
        val service = processButtonEventService(statsPort = statsPort)

        // when
        service(ButtonEvent.Query(sf6MoveId.copy(input = "5MP~5HP")))

        // then
        assertThat(statsPort.failureCount).isEqualTo(1)
    }
    //endregion

    //region text
    @Test
    fun `text is a plain text response`() = runTest {
        // given
        val expected = Result.Success(PlainTextResponse(text = VIDEO_URL))
        val service = processButtonEventService()

        // when
        val result = service(ButtonEvent.Text(VIDEO_URL))

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region forward
    @Test
    fun `forward copies the message to the target channel`() = runTest {
        // given
        val expected = "forward($SOURCE_CHANNEL_ID, $SOURCE_MESSAGE_ID, $TARGET_CHANNEL_ID)"
        val forwardPort = FakeForwardPort()
        val service = processButtonEventService(forwardPort = forwardPort)

        // when
        service(forwardEvent)

        // then
        assertThat(forwardPort.callList).containsExactly(expected)
    }

    @Test
    fun `forward redirects to the target channel`() = runTest {
        // given
        val expected = Result.Success(RedirectResponse(channelId = TARGET_CHANNEL_ID))
        val service = processButtonEventService()

        // when
        val result = service(forwardEvent)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed forward is returned`() = runTest {
        // given
        val expected = Result.Error(BotError.Kord("Missing access"))
        val service = processButtonEventService(forwardPort = FakeForwardPort(result = expected))

        // when
        val result = service(forwardEvent)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region command
    @Test
    fun `command is routed without a source`() = runTest {
        // given
        val fixture = RouterFixture()
        val service = processButtonEventService(routerFixture = fixture)

        // when
        service(ButtonEvent.Command(command = Command.Join, query = STEAM_LOBBY_URL))

        // then
        assertThat(fixture.callList).containsExactly("createSteamLobbyResponse($STEAM_LOBBY_URL, null)")
    }

    @Test
    fun `command is recorded with its own command and game`() = runTest {
        // given
        val expected = (Command.Alias to Game.Tekken8)
        val statsPort = FakeStatsPort()
        val fixture = RouterFixture(
            characterService = FakeCharacterService(
                aliasResult = Result.Success(AliasResponse.CharacterAliases(listOf(characterResponse("jin", "Jin")))),
            ),
        )
        val service = processButtonEventService(routerFixture = fixture, statsPort = statsPort)

        // when
        service(ButtonEvent.Command(command = Command.Alias, query = "Tekken_8"))

        // then
        assertThat(statsPort.registeredList).containsExactly(expected)
    }

    @Test
    fun `failed command is recorded as a failure`() = runTest {
        // given
        val statsPort = FakeStatsPort()
        val service = processButtonEventService(statsPort = statsPort)

        // when
        service(ButtonEvent.Command(command = Command.Join, query = STEAM_LOBBY_URL))

        // then
        assertThat(statsPort.failureCount).isEqualTo(1)
    }
    //endregion


    private class FakeForwardPort(
        private val result: EmptyResult<BotError> = Result.Success(Unit),
    ): ForwardPort {
        val callList = mutableListOf<String>()

        override suspend fun forward(
            sourceChannelId: String,
            sourceMessageId: String,
            targetChannelId: String,
        ): EmptyResult<BotError> {
            callList += "forward($sourceChannelId, $sourceMessageId, $targetChannelId)"
            return result
        }
    }

    private fun processButtonEventService(
        routerFixture: RouterFixture = RouterFixture(),
        statsPort: FakeStatsPort = FakeStatsPort(),
        forwardPort: FakeForwardPort = FakeForwardPort(),
    ): ProcessButtonEventService {
        val moveMap = mapOf(
            CharacterId(game = Game.StreetFighter6, characterId = "ryu") to listOf(sf6Move),
            CharacterId(game = Game.GGST, characterId = "sol") to listOf(ggstMove),
        )
        val service = ProcessButtonEventService(
            frameDataPort = FakeFrameDataPort(moveMap),
            commandRouterService = routerFixture.router,
            statsPort = statsPort,
            forwardPort = forwardPort,
        )
        return service
    }
}


private const val VIDEO_URL = "https://wiki.supercombo.gg/images/Ryu_5MP.mp4"
private const val STEAM_LOBBY_URL = "steam://joinlobby/586140/109775241137042824/76561198443042808"
private const val SOURCE_CHANNEL_ID = "555555555555555555"
private const val SOURCE_MESSAGE_ID = "1290000000000000000"
private const val TARGET_CHANNEL_ID = "1193118389825175582"
private val forwardEvent = ButtonEvent.Forward(
    sourceChannelId = SOURCE_CHANNEL_ID,
    sourceMessageId = SOURCE_MESSAGE_ID,
    targetChannelId = TARGET_CHANNEL_ID,
)
private val sf6MoveId = MoveId(game = Game.StreetFighter6, characterId = "ryu", input = "5MP")
private val ggstMoveId = MoveId(game = Game.GGST, characterId = "sol", input = "5K")
private val videoButton = BotResponse.EmbedButton(label = "Video", action = BotResponse.EmbedButton.Action.Text(VIDEO_URL))
private val sf6Move = moveResponse(
    input = "5MP",
    game = Game.StreetFighter6,
    characterName = "Ryu",
    buttonSet = BotResponse.ButtonSet(
        buttonList = listOf(
            BotResponse.EmbedButton(label = "Details", action = BotResponse.EmbedButton.Action.Expand(sf6MoveId)),
            videoButton,
        ),
    ),
)
private val ggstMove = moveResponse(
    input = "5K",
    game = Game.GGST,
    characterName = "Sol Badguy",
    buttonSet = BotResponse.ButtonSet(
        buttonList = listOf(
            BotResponse.EmbedButton(label = "Details", action = BotResponse.EmbedButton.Action.Expand(ggstMoveId)),
        ),
    ),
)
