package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.discord.app.model.discord.AutocompleteChoice
import io.github.sophon.discord.app.model.frameData.CharacterId
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.service.CharacterServiceImpl
import io.github.sophon.discord.app.service.MoveServiceImpl
import io.github.sophon.discord.app.service.ProduceAutoCompleteService
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ProduceAutoCompleteServiceTest {
    //region routing
    @Test
    fun `unknown command has no choices`() = runTest {
        // given
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "frames", argument = "character", query = "", argumentMap = emptyMap())

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `unknown argument has no choices`() = runTest {
        // given
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "fd", argument = "stance", query = "", argumentMap = emptyMap())

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `argument without autocomplete has no choices`() = runTest {
        // given
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "startup", argument = "min", query = "1", argumentMap = jinArgumentMap)

        // then
        assertThat(result).isEmpty()
    }
    //endregion

    //region character
    @Test
    fun `blank character query offers every character with its game`() = runTest {
        // given
        val expected = listOf(
            AutocompleteChoice(name = "Jin (Tekken 8)", value = "jin::Tekken8"),
            AutocompleteChoice(name = "Kazuya (Tekken 8)", value = "kazuya::Tekken8"),
            AutocompleteChoice(name = "Sol Badguy (Guilty Gear -Strive-)", value = "sol::GGST"),
        )
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "fd", argument = "Character", query = "", argumentMap = emptyMap())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `char offers only characters with properties`() = runTest {
        // given
        val expected = listOf(AutocompleteChoice(name = "Sol Badguy (Guilty Gear -Strive-)", value = "sol::GGST"))
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "char", argument = "character", query = "", argumentMap = emptyMap())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `character name matches regardless of spaces and case`() = runTest {
        // given
        val expected = listOf(AutocompleteChoice(name = "Sol Badguy (Guilty Gear -Strive-)", value = "sol::GGST"))
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "fd", argument = "character", query = "SOLBAD", argumentMap = emptyMap())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `character alias matches partially`() = runTest {
        // given
        val expected = listOf(AutocompleteChoice(name = "Kazuya (Tekken 8)", value = "kazuya::Tekken8"))
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "fd", argument = "character", query = "mask", argumentMap = emptyMap())

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region move
    @Test
    fun `move choice shows padded input, first four fields and name`() = runTest {
        // given
        val expected = listOf(
            AutocompleteChoice(
                name = "f,n,d,df+2 ____ [ i11 | +5a | +5 | +31a ] ----- Electric Wind God Fist",
                value = "f,n,d,df+2",
            ),
        )
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "fd", argument = "move", query = "ewgf", argumentMap = jinArgumentMap)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move choice without a name has no name column and blank fields are dashes`() = runTest {
        // given
        val expected = listOf(AutocompleteChoice(name = "1,2 ___________ [ - | - | - | - ] ", value = "1,2"))
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "fd", argument = "move", query = "1,2", argumentMap = jinArgumentMap)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `long move choice is cut to discord's limit`() = runTest {
        // given
        val fullName = "uf+4 __________ [ i15 | +31a | -9 | +31a ] ----- $UF4_NAME"
        val expected = listOf(AutocompleteChoice(name = (fullName.take(97) + "..."), value = "uf+4"))
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "fd", argument = "move", query = "uf+4", argumentMap = jinArgumentMap)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move matches input, name or alias regardless of case`() = runTest {
        // given
        val expected = listOf("f,n,d,df+2", "df+1")
        val service = produceAutoCompleteService()

        // when
        val inputMatchList = service(commandString = "fd", argument = "move", query = "F,N,D", argumentMap = jinArgumentMap)
        val nameMatchList = service(commandString = "fd", argument = "move", query = "MID CH", argumentMap = jinArgumentMap)

        // then
        assertThat((inputMatchList + nameMatchList).map { it.value }).isEqualTo(expected)
    }

    @Test
    fun `blank move query offers every move`() = runTest {
        // given
        val expected = jinMoveList.map { it.input }
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "fd", argument = "move", query = "", argumentMap = jinArgumentMap)

        // then
        assertThat(result.map { it.value }).isEqualTo(expected)
    }

    @Test
    fun `undecodable character has no move choices`() = runTest {
        // given
        val argumentMapList = listOf(
            mapOf("character" to "jin"),
            mapOf("character" to "jin::Tekken9"),
            emptyMap(),
        )
        val service = produceAutoCompleteService()

        // when
        val result = argumentMapList.flatMap { argumentMap ->
            service(commandString = "fd", argument = "move", query = "", argumentMap = argumentMap)
        }

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `failed move load has no move choices`() = runTest {
        // given
        val service = produceAutoCompleteService()

        // when
        val result = service(
            commandString = "fd",
            argument = "move",
            query = "",
            argumentMap = mapOf("character" to "kazuya::Tekken8"),
        )

        // then
        assertThat(result).isEmpty()
    }
    //endregion

    //region stance
    @Test
    fun `stance choices are the character's distinct stances`() = runTest {
        // given
        val expected = listOf(
            AutocompleteChoice(name = "ZEN", value = "ZEN"),
            AutocompleteChoice(name = "BT", value = "BT"),
        )
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "stance", argument = "stance", query = "", argumentMap = jinArgumentMap)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `stance matches partially regardless of case`() = runTest {
        // given
        val expected = listOf(AutocompleteChoice(name = "ZEN", value = "ZEN"))
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "stance", argument = "stance", query = "ze", argumentMap = jinArgumentMap)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region game
    @Test
    fun `game choices are the games with characters`() = runTest {
        // given
        val expected = listOf(
            AutocompleteChoice(name = "Tekken 8", value = "Tekken_8"),
            AutocompleteChoice(name = "Guilty Gear -Strive-", value = "GGST"),
        )
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "alias", argument = "game", query = "", argumentMap = emptyMap())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `game matches partially regardless of case`() = runTest {
        // given
        val service = produceAutoCompleteService()

        // when
        val result = service(commandString = "alias", argument = "game", query = "GEAR", argumentMap = emptyMap())

        // then
        assertThat(result).containsExactly(AutocompleteChoice(name = "Guilty Gear -Strive-", value = "GGST"))
    }
    //endregion


    private fun produceAutoCompleteService(): ProduceAutoCompleteService {
        val characterService = CharacterServiceImpl(
            charactersPort = FakeCharactersPort(listOf(jin, kazuya, sol)),
            gamePort = FakeGamePort(),
        )
        val moveService = MoveServiceImpl(
            characterService = characterService,
            frameDataPort = FakeFrameDataPort(mapOf(CharacterId(game = Game.Tekken8, characterId = "jin") to jinMoveList)),
            getMovesOfTypePort = FakeGetMovesOfTypePort(),
            getMovesInRangePort = FakeGetMovesInRangePort(),
        )
        val service = ProduceAutoCompleteService(characterService = characterService, moveService = moveService)
        return service
    }
}

private fun fieldList(vararg values: String): List<BotResponse.Field> {
    val titleList = listOf("Startup", "Hit", "Block", "Counter", "Damage")
    val fieldList = values.mapIndexed { index, value -> BotResponse.Field(title = titleList[index], value = value) }
    return fieldList
}


private const val UF4_NAME = "Left Spinning Axe Kick into Hellsweep into Electric Wind Hook Fist into Heat Smash"
private val jinArgumentMap = mapOf("character" to "jin::Tekken8")
private val jin = characterResponse(id = "jin", displayName = "Jin", aliasList = listOf("jim"))
private val kazuya = characterResponse(id = "kazuya", displayName = "Kazuya", aliasList = listOf("kaz", "masku"))
private val sol = characterResponse(
    id = "sol",
    displayName = "Sol Badguy",
    game = Game.GGST,
    propertyList = listOf(BotResponse.Field(title = "Guts", value = "2")),
)
private val jinMoveList = listOf(
    moveResponse(input = "1,2", primaryFields = fieldList("", " ", "", "")),
    moveResponse(
        input = "f,n,d,df+2",
        moveName = "Electric Wind God Fist",
        aliasList = listOf("ewgf"),
        primaryFields = fieldList("i11~12", "+5a (+15)", "+5", "[+31a (+21)](https://wavu.wiki/t/Jin_combos)", "25"),
    ),
    moveResponse(input = "df+1", moveName = "Mid check", primaryFields = fieldList("i13", "+8", "-1", "+8")),
    moveResponse(
        input = "uf+4",
        moveName = UF4_NAME,
        primaryFields = fieldList("i15~16", "+31a (+21)", "-9", "+31a (+21)"),
    ),
    moveResponse(input = "ZEN.1", stance = "ZEN"),
    moveResponse(input = "ZEN.3", stance = "ZEN"),
    moveResponse(input = "b+1+2", stance = "BT"),
)
