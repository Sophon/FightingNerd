package io.github.sophon.wiki.app.service

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.app.outPort.LoadCharacterListPort
import io.github.sophon.wiki.app.outPort.LoadWikiConfigPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GetCharacterListServiceTest {
    @Test
    fun `characters of every enabled game are returned in enabled game order`() = runTest {
        // given
        val service = getCharacterListService(
            configPort = FakeCharacterListConfigPort(
                wikiConfig(
                    availableGameSet = setOf(Game.StreetFighter6, Game.Tekken8),
                    enabledGameSet = setOf(Game.StreetFighter6, Game.Tekken8),
                )
            ),
        )
        val expected = listOf(ryu, jin)

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `characters of disabled games are left out`() = runTest {
        // given
        val service = getCharacterListService(
            configPort = FakeCharacterListConfigPort(
                wikiConfig(
                    availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                    enabledGameSet = setOf(Game.Tekken8),
                )
            ),
        )
        val expected = listOf(jin)

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `no enabled games is an empty list`() = runTest {
        // given
        val service = getCharacterListService(
            configPort = FakeCharacterListConfigPort(
                wikiConfig(
                    availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                    enabledGameSet = emptySet(),
                )
            ),
        )

        // when
        val result = service().first()

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `nothing is emitted before a config is saved`() = runTest {
        // given
        val service = getCharacterListService(configPort = FakeCharacterListConfigPort(wikiConfig = null))

        service().test {
            // then
            expectNoEvents()
        }
    }

    @Test
    fun `enabling a game adds its characters`() = runTest {
        // given
        val configPort = FakeCharacterListConfigPort(
            wikiConfig(
                availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                enabledGameSet = setOf(Game.Tekken8),
            )
        )
        val service = getCharacterListService(configPort = configPort)
        val expected = listOf(jin, ryu)

        service().test {
            awaitItem()

            // when
            configPort.wikiConfig.value = wikiConfig(
                availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                enabledGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
            )
            val result = awaitItem()

            // then
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun `a config change that keeps the enabled games doesn't re-emit`() = runTest {
        // given
        val configPort = FakeCharacterListConfigPort(
            wikiConfig(
                availableGameSet = setOf(Game.Tekken8),
                enabledGameSet = setOf(Game.Tekken8),
            )
        )
        val service = getCharacterListService(configPort = configPort)

        service().test {
            awaitItem()

            // when
            configPort.wikiConfig.value = wikiConfig(
                availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                enabledGameSet = setOf(Game.Tekken8),
            )

            // then
            expectNoEvents()
        }
    }

    @Test
    fun `a stored character list update re-emits`() = runTest {
        // given
        val characterListPort = FakeLoadCharacterListPort(
            Game.Tekken8 to listOf(jin),
            Game.StreetFighter6 to listOf(ryu),
        )
        val service = getCharacterListService(
            configPort = FakeCharacterListConfigPort(
                wikiConfig(
                    availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                    enabledGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                )
            ),
            characterListPort = characterListPort,
        )
        val expected = listOf(jin, kazuya, ryu)

        service().test {
            awaitItem()

            // when
            characterListPort.store(Game.Tekken8, listOf(jin, kazuya))
            val result = awaitItem()

            // then
            assertThat(result).isEqualTo(expected)
        }
    }
}

private fun getCharacterListService(
    configPort: FakeCharacterListConfigPort,
    characterListPort: FakeLoadCharacterListPort = FakeLoadCharacterListPort(
        Game.Tekken8 to listOf(jin),
        Game.StreetFighter6 to listOf(ryu),
    ),
): GetCharacterListService =
    GetCharacterListService(
        loadWikiConfigPort = configPort,
        loadCharacterListPort = characterListPort,
    )

private fun wikiConfig(
    availableGameSet: Set<Game>,
    enabledGameSet: Set<Game>,
): WikiConfig {
    val result = WikiConfig.create(
        availableGameSet = availableGameSet,
        enabledGameSet = enabledGameSet,
    )
    val wikiConfig = (result as Result.Success).data
    return wikiConfig
}

private val jin = Character(
    id = CharacterId(Game.Tekken8, "jin"),
    displayName = "Jin",
    remoteQueryId = "Jin",
    wikiUrl = "https://wavu.wiki/t/Jin",
)

private val kazuya = Character(
    id = CharacterId(Game.Tekken8, "kazuya"),
    displayName = "Kazuya",
    remoteQueryId = "Kazuya",
    wikiUrl = "https://wavu.wiki/t/Kazuya",
)

private val ryu = Character(
    id = CharacterId(Game.StreetFighter6, "ryu"),
    displayName = "Ryu",
    remoteQueryId = "Ryu",
    wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu",
)

private class FakeCharacterListConfigPort(wikiConfig: WikiConfig?) : LoadWikiConfigPort {
    val wikiConfig = MutableStateFlow(wikiConfig)

    override fun subscribe(): Flow<WikiConfig?> {
        return wikiConfig
    }
}

private class FakeLoadCharacterListPort(vararg storedCharacterLists: Pair<Game, List<Character>>) : LoadCharacterListPort {
    private val characterListByGame = storedCharacterLists
        .associate { (game, characterList) -> game to MutableStateFlow(characterList) }
        .toMutableMap()

    fun store(
        game: Game,
        characterList: List<Character>,
    ) {
        characterListByGame.getOrPut(game) { MutableStateFlow(emptyList()) }.value = characterList
    }

    override fun subscribe(game: Game): Flow<List<Character>> {
        val characterListFlow = characterListByGame.getOrPut(game) { MutableStateFlow(emptyList()) }
        return characterListFlow
    }
}
