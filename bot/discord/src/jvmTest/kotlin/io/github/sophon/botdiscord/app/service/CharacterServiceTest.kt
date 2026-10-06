package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.GameList
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.response.AliasResponse
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.discord.app.service.CharacterServiceImpl
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class CharacterServiceTest {
    //region findCharacter
    @Test
    fun `character is found by id regardless of case`() = runTest {
        // given
        val expected = Result.Success(jin)
        val service = characterService()

        // when
        val result = service.findCharacter("JIN")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `character is found by alias regardless of case`() = runTest {
        // given
        val expected = Result.Success(kazuya)
        val service = characterService()

        // when
        val result = service.findCharacter("Masku")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `display name isn't matched`() = runTest {
        // given
        val service = characterService(characterList = listOf(jin.copy(id = "jin_kazama")))

        // when
        val result = service.findCharacter("Jin")

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.UnknownCharacter::class)
    }

    @Test
    fun `unknown character is an error`() = runTest {
        // given
        val service = characterService()

        // when
        val result = service.findCharacter("jni")

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.UnknownCharacter::class)
    }

    @Test
    fun `character without properties is skipped when properties are required`() = runTest {
        // given
        val service = characterService()

        // when
        val result = service.findCharacter(characterQuery = "jin", requireProperties = true)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.UnknownCharacter::class)
    }

    @Test
    fun `character with properties is found when properties are required`() = runTest {
        // given
        val expected = Result.Success(sol)
        val service = characterService()

        // when
        val result = service.findCharacter(characterQuery = "sol", requireProperties = true)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region findAliases
    @Test
    fun `blank game prompts with a numbered button per game`() = runTest {
        // given
        val expected = Result.Success(
            AliasResponse.GamePrompt(
                gameList = listOf("Tekken 8", "Guilty Gear -Strive-"),
                dataSource = wavuDataSource,
                buttonSet = BotResponse.ButtonSet(
                    buttonList = listOf(
                        BotResponse.EmbedButton(
                            label = "1",
                            action = BotResponse.EmbedButton.Action.Command(command = Command.Alias, query = "Tekken_8"),
                        ),
                        BotResponse.EmbedButton(
                            label = "2",
                            action = BotResponse.EmbedButton.Action.Command(command = Command.Alias, query = "GGST"),
                        ),
                    ),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            ),
        )
        val service = characterService(
            gameList = GameList(gameList = listOf(Game.Tekken8, Game.GGST), dataSource = wavuDataSource),
        )

        // when
        val result = service.findAliases(" ")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `game id lists the game's characters`() = runTest {
        // given
        val expected = Result.Success(AliasResponse.CharacterAliases(characterList = listOf(jin, kazuya)))
        val service = characterService()

        // when
        val result = service.findAliases("tekken_8")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `game without characters is unsupported`() = runTest {
        // given
        val service = characterService()

        // when
        val result = service.findAliases("Street_Fighter_6")

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.UnsupportedGame::class)
    }

    @Test
    fun `unknown game is unsupported`() = runTest {
        // given
        val service = characterService()

        // when
        val result = service.findAliases("Tekken_9")

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.UnsupportedGame::class)
    }
    //endregion


    private fun characterService(
        characterList: List<CharacterResponse> = listOf(jin, kazuya, sol),
        gameList: GameList = GameList(gameList = emptyList(), dataSource = wavuDataSource),
    ): CharacterServiceImpl {
        val service = CharacterServiceImpl(
            charactersPort = FakeCharactersPort(characterList),
            gamePort = FakeGamePort(gameList),
        )
        return service
    }
}


private val jin = characterResponse(id = "jin", displayName = "Jin", aliasList = listOf("jim"))
private val kazuya = characterResponse(id = "kazuya", displayName = "Kazuya", aliasList = listOf("kaz", "masku"))
private val sol = characterResponse(
    id = "sol",
    displayName = "Sol Badguy",
    game = Game.GGST,
    propertyList = listOf(BotResponse.Field(title = "Guts", value = "2")),
)
