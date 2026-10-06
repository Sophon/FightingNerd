package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.FrameRange
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.discord.Emoji
import io.github.sophon.discord.app.model.frameData.CharacterId
import io.github.sophon.discord.app.model.frameData.MoveId
import io.github.sophon.discord.app.model.frameData.MoveType
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.ListResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.service.CharacterServiceImpl
import io.github.sophon.discord.app.service.MoveServiceImpl
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class MoveServiceTest {
    //region findFrameData
    @Test
    fun `move is found by input regardless of case`() = runTest {
        // given
        val expected = Result.Success(df1)
        val service = moveService()

        // when
        val result = service.findFrameData("jin DF+1")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move is found by its multi-word name`() = runTest {
        // given
        val expected = Result.Success(ewgf)
        val service = moveService()

        // when
        val result = service.findFrameData("jin electric wind god fist")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move is found by alias`() = runTest {
        // given
        val expected = Result.Success(ewgf)
        val service = moveService()

        // when
        val result = service.findFrameData("jin EWGF")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unknown move is an error`() = runTest {
        // given
        val service = moveService()

        // when
        val result = service.findFrameData("jin d+5")

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.UnknownMove::class)
    }

    @Test
    fun `unknown character is an error`() = runTest {
        // given
        val service = moveService()

        // when
        val result = service.findFrameData("jni df+1")

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.UnknownCharacter::class)
    }

    @Test
    fun `failed move load is returned`() = runTest {
        // given
        val expected = BotError.WikiError("DatabaseError(wiki.db)")
        val service = moveService(frameDataPort = FakeFrameDataPort(moveMap = emptyMap(), error = expected))

        // when
        val result = service.findFrameData("jin df+1")

        // then
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(expected)
    }
    //endregion

    //region findMovesOfType
    @Test
    fun `moves of type are requested for the character`() = runTest {
        // given
        val expected = (jinId to MoveType.HEAT)
        val getMovesOfTypePort = FakeGetMovesOfTypePort()
        val service = moveService(getMovesOfTypePort = getMovesOfTypePort)

        // when
        service.findMovesOfType(characterQuery = "jin", moveType = MoveType.HEAT)

        // then
        assertThat(getMovesOfTypePort.requestList).containsExactly(expected)
    }

    @Test
    fun `moves of type are listed with a query button each`() = runTest {
        // given
        val expected = Result.Success(
            ListResponse(
                game = Game.Tekken8,
                title = "${Emoji.TK_HEAT}JIN Heat moves",
                values = listOf("df+1", "f,n,d,df+2"),
                dataSource = wavuDataSource,
                buttonSet = queryButtonSet("df+1", "f,n,d,df+2"),
            ),
        )
        val service = moveService(getMovesOfTypePort = FakeGetMovesOfTypePort(Result.Success(listOf(df1, ewgf))))

        // when
        val result = service.findMovesOfType(characterQuery = "jin", moveType = MoveType.HEAT)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `no moves of type have no buttons`() = runTest {
        // given
        val expected = Result.Success(
            ListResponse(
                game = Game.Tekken8,
                title = "${Emoji.TK_PC}JIN Power Crush moves",
                values = emptyList(),
                dataSource = wavuDataSource,
                buttonSet = null,
            ),
        )
        val service = moveService()

        // when
        val result = service.findMovesOfType(characterQuery = "jin", moveType = MoveType.PC)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `moves of type for an unknown character aren't requested`() = runTest {
        // given
        val getMovesOfTypePort = FakeGetMovesOfTypePort()
        val service = moveService(getMovesOfTypePort = getMovesOfTypePort)

        // when
        service.findMovesOfType(characterQuery = "jni", moveType = MoveType.HOMING)

        // then
        assertThat(getMovesOfTypePort.requestList).isEmpty()
    }
    //endregion

    //region findMovesInRange
    @Test
    fun `two bounds are sorted into a range`() = runTest {
        // given
        val expected = (jinId to FrameRange(type = FrameRange.Type.ON_BLOCK, from = -12, to = -10))
        val getMovesInRangePort = FakeGetMovesInRangePort()
        val service = moveService(getMovesInRangePort = getMovesInRangePort)

        // when
        service.findMovesInRange(query = "jin -10 -12", command = Command.OnBlock)

        // then
        assertThat(getMovesInRangePort.requestList).containsExactly(expected)
    }

    @Test
    fun `one bound is an exact range`() = runTest {
        // given
        val expected = (jinId to FrameRange(type = FrameRange.Type.STARTUP, from = 10, to = 10))
        val getMovesInRangePort = FakeGetMovesInRangePort()
        val service = moveService(getMovesInRangePort = getMovesInRangePort)

        // when
        service.findMovesInRange(query = "jin 10", command = Command.Startup)

        // then
        assertThat(getMovesInRangePort.requestList).containsExactly(expected)
    }

    @Test
    fun `infinite bounds are open ends`() = runTest {
        // given
        val expected = (jinId to FrameRange(type = FrameRange.Type.ON_HIT, from = Int.MIN_VALUE, to = Int.MAX_VALUE))
        val getMovesInRangePort = FakeGetMovesInRangePort()
        val service = moveService(getMovesInRangePort = getMovesInRangePort)

        // when
        service.findMovesInRange(query = "jin +INF -inf", command = Command.OnHit)

        // then
        assertThat(getMovesInRangePort.requestList).containsExactly(expected)
    }

    @Test
    fun `only the first two bounds count`() = runTest {
        // given
        val expected = (jinId to FrameRange(type = FrameRange.Type.ON_COUNTER, from = 5, to = 15))
        val getMovesInRangePort = FakeGetMovesInRangePort()
        val service = moveService(getMovesInRangePort = getMovesInRangePort)

        // when
        service.findMovesInRange(query = "jin 15 5 30", command = Command.OnCounter)

        // then
        assertThat(getMovesInRangePort.requestList).containsExactly(expected)
    }

    @Test
    fun `moves in range are listed once per input with a range title`() = runTest {
        // given
        val expected = Result.Success(
            ListResponse(
                game = Game.Tekken8,
                title = "Jin On Block [-INF ; -10] moves",
                values = listOf("df+1", "f,n,d,df+2"),
                dataSource = wavuDataSource,
                buttonSet = queryButtonSet("df+1", "f,n,d,df+2"),
            ),
        )
        val service = moveService(
            getMovesInRangePort = FakeGetMovesInRangePort(Result.Success(listOf(df1, df1, ewgf))),
        )

        // when
        val result = service.findMovesInRange(query = "jin -inf -10", command = Command.OnBlock)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `range without a number is an invalid query`() = runTest {
        // given
        val service = moveService()

        // when
        val result = service.findMovesInRange(query = "jin fast", command = Command.Startup)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `non-range command is an invalid command`() = runTest {
        // given
        val service = moveService()

        // when
        val result = service.findMovesInRange(query = "jin 10", command = Command.Fd)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.InvalidCommand::class)
    }
    //endregion

    //region findStanceOrMove
    @Test
    fun `character alone lists its stances with a command button each`() = runTest {
        // given
        val expected = Result.Success(
            ListResponse(
                game = Game.Tekken8,
                title = "JIN stances",
                values = listOf("ZEN", "BT"),
                dataSource = wavuDataSource,
                buttonSet = BotResponse.ButtonSet(
                    buttonList = listOf(
                        BotResponse.EmbedButton(
                            label = "1",
                            action = BotResponse.EmbedButton.Action.Command(command = Command.Stance, query = "jin ZEN"),
                        ),
                        BotResponse.EmbedButton(
                            label = "2",
                            action = BotResponse.EmbedButton.Action.Command(command = Command.Stance, query = "jin BT"),
                        ),
                    ),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            ),
        )
        val service = moveService()

        // when
        val result = service.findStanceOrMove("jin")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `character without stances has no buttons`() = runTest {
        // given
        val expected = Result.Success(
            ListResponse(
                game = Game.Tekken8,
                title = "JIN stances",
                values = emptyList(),
                dataSource = wavuDataSource,
                buttonSet = null,
            ),
        )
        val service = moveService(frameDataPort = FakeFrameDataPort(mapOf(jinId to listOf(df1, ewgf))))

        // when
        val result = service.findStanceOrMove("jin")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `stance lists its moves regardless of case`() = runTest {
        // given
        val expected = Result.Success(
            ListResponse(
                game = Game.Tekken8,
                title = "zen moves",
                values = listOf("ZEN.1", "ZEN.3"),
                dataSource = wavuDataSource,
                buttonSet = queryButtonSet("ZEN.1", "ZEN.3"),
            ),
        )
        val service = moveService()

        // when
        val result = service.findStanceOrMove("jin zen")

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region findStrings
    @Test
    fun `followups start with the prefix`() = runTest {
        // given
        val expected = Result.Success(
            ListResponse(
                game = Game.Tekken8,
                title = "Jin followups to 1 moves",
                values = listOf("1", "1,2", "1,1,2"),
                dataSource = wavuDataSource,
                buttonSet = queryButtonSet("1", "1,2", "1,1,2"),
            ),
        )
        val service = moveService()

        // when
        val result = service.findStrings("jin 1")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `followups match the prefix regardless of case`() = runTest {
        // given
        val expected = listOf("ZEN.1", "ZEN.3")
        val service = moveService()

        // when
        val result = service.findStrings("jin zen")

        // then
        val values = (result as Result.Success).data.values
        assertThat(values).isEqualTo(expected)
    }
    //endregion

    //region getMoves
    @Test
    fun `moves are the character's frame data`() = runTest {
        // given
        val expected = Result.Success(jinMoveList)
        val service = moveService()

        // when
        val result = service.getMoves(jinId)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion


    private fun moveService(
        frameDataPort: FakeFrameDataPort = FakeFrameDataPort(mapOf(jinId to jinMoveList)),
        getMovesOfTypePort: FakeGetMovesOfTypePort = FakeGetMovesOfTypePort(),
        getMovesInRangePort: FakeGetMovesInRangePort = FakeGetMovesInRangePort(),
    ): MoveServiceImpl {
        val characterService = CharacterServiceImpl(
            charactersPort = FakeCharactersPort(listOf(jin)),
            gamePort = FakeGamePort(),
        )
        val service = MoveServiceImpl(
            characterService = characterService,
            frameDataPort = frameDataPort,
            getMovesOfTypePort = getMovesOfTypePort,
            getMovesInRangePort = getMovesInRangePort,
        )
        return service
    }
}

private fun queryButtonSet(vararg inputs: String): BotResponse.ButtonSet {
    val buttonSet = BotResponse.ButtonSet(
        buttonList = inputs.mapIndexed { index, input ->
            BotResponse.EmbedButton(
                label = (index + 1).toString(),
                action = BotResponse.EmbedButton.Action.Query(
                    moveId = MoveId(game = Game.Tekken8, characterId = "jin", input = input),
                ),
            )
        },
        duration = EMBED_BUTTON_DURATION_INF.seconds,
    )
    return buttonSet
}


private val jin = characterResponse(id = "jin", displayName = "Jin", aliasList = listOf("jim"))
private val jinId = CharacterId(game = Game.Tekken8, characterId = "jin")
private val df1 = moveResponse(input = "df+1", moveName = "Mid check")
private val ewgf = moveResponse(
    input = "f,n,d,df+2",
    moveName = "Electric Wind God Fist",
    aliasList = listOf("ewgf"),
)
private val jinMoveList: List<MoveResponse> = listOf(
    moveResponse(input = "1", moveName = "Jab"),
    moveResponse(input = "1,2"),
    moveResponse(input = "1,1,2"),
    moveResponse(input = "1+2", moveName = "Corpse Thrust"),
    df1,
    ewgf,
    moveResponse(input = "ZEN.1", stance = "ZEN"),
    moveResponse(input = "ZEN.3", stance = "ZEN"),
    moveResponse(input = "b+1+2", stance = "BT"),
)
