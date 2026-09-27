package io.github.sophon.wiki.application.domain.service

import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.port.outbound.DeleteCharacterListPort
import io.github.sophon.wiki.application.port.outbound.DeleteMoveListPort
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.SaveWikiConfigPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

val availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6, Game.GGST)

internal class ConfigureWikiServiceTest {
    @Test
    fun `saves the config on the first call`() = runTest {
        // given
        val configPort = FakeWikiConfigPort(previousConfig = null)
        val service = configureWikiService(configPort = configPort)
        val newConfig = wikiConfig(Game.Tekken8)

        // when
        service.invoke(newConfig)

        // then
        assertThat(configPort.wikiConfig.value).isEqualTo(newConfig)
    }

    @Test
    fun `nothing is deleted on the first call`() = runTest {
        // given
        val moveStore = FakeGameDataStore(storedGameSet = availableGameSet)
        val characterStore = FakeGameDataStore(storedGameSet = availableGameSet)
        val service = configureWikiService(
            configPort = FakeWikiConfigPort(previousConfig = null),
            moveStore = moveStore,
            characterStore = characterStore,
        )
        val expected = availableGameSet

        // when
        service.invoke(wikiConfig(Game.Tekken8))

        // then
        assertThat(moveStore.storedGameSet).isEqualTo(expected)
        assertThat(characterStore.storedGameSet).isEqualTo(expected)
    }

    @Test
    fun `disabling a game deletes moves and characters`() = runTest {
        // given
        val moveStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val characterStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val service = configureWikiService(
            configPort = FakeWikiConfigPort(previousConfig = wikiConfig(Game.Tekken8, Game.StreetFighter6)),
            moveStore = moveStore,
            characterStore = characterStore,
        )
        val expected = setOf(Game.Tekken8)

        // when
        service.invoke(wikiConfig(Game.Tekken8))

        // then
        assertThat(moveStore.storedGameSet).isEqualTo(expected)
        assertThat(characterStore.storedGameSet).isEqualTo(expected)
    }

    @Test
    fun `adding a game doesn't delete`() = runTest {
        // given
        val moveStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val characterStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val service = configureWikiService(
            configPort = FakeWikiConfigPort(previousConfig = wikiConfig(Game.Tekken8)),
            moveStore = moveStore,
            characterStore = characterStore,
        )
        val expected = setOf(Game.Tekken8, Game.StreetFighter6)

        // when
        service.invoke(wikiConfig(Game.Tekken8, Game.StreetFighter6))

        // then
        assertThat(moveStore.storedGameSet).isEqualTo(expected)
        assertThat(characterStore.storedGameSet).isEqualTo(expected)
    }

    @Test
    fun `delete fail returns database error`() = runTest {
        // given
        val service = configureWikiService(
            configPort = FakeWikiConfigPort(previousConfig = wikiConfig(Game.StreetFighter6)),
            moveStore = FakeGameDataStore(
                storedGameSet = setOf(Game.StreetFighter6),
                failingGameSet = setOf(Game.StreetFighter6),
            ),
            characterStore = FakeGameDataStore(storedGameSet = setOf(Game.StreetFighter6)),
        )

        // when
        val result = service.invoke(wikiConfig())

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(WikiError.DatabaseError::class)
    }

    @Test
    fun `new config is kept when a delete fails`() = runTest {
        // given
        val configPort = FakeWikiConfigPort(previousConfig = wikiConfig(Game.Tekken8, Game.StreetFighter6))
        val service = configureWikiService(
            configPort = configPort,
            moveStore = FakeGameDataStore(
                storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                failingGameSet = setOf(Game.StreetFighter6),
            ),
            characterStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6)),
        )
        val newConfig = wikiConfig(Game.Tekken8)
        val expected = newConfig

        // when
        service.invoke(newConfig)

        // then
        assertThat(configPort.wikiConfig.value).isEqualTo(expected)
    }

    @Test
    fun `other disabled games are deleted when a delete fails`() = runTest {
        // given
        val moveStore = FakeGameDataStore(
            storedGameSet = availableGameSet,
            failingGameSet = setOf(Game.StreetFighter6),
        )
        val characterStore = FakeGameDataStore(storedGameSet = availableGameSet)
        val service = configureWikiService(
            configPort = FakeWikiConfigPort(previousConfig = wikiConfig(Game.Tekken8, Game.StreetFighter6, Game.GGST)),
            moveStore = moveStore,
            characterStore = characterStore,
        )

        // when
        service.invoke(wikiConfig(Game.Tekken8))

        // then
        assertThat(moveStore.storedGameSet).doesNotContain(Game.GGST)
        assertThat(characterStore.storedGameSet).doesNotContain(Game.GGST)
    }

    @Test
    fun `save fail returns the save error`() = runTest {
        // given
        val expected = Result.Error(WikiError.DatabaseError("UNKNOWN"))
        val service = configureWikiService(
            configPort = FakeWikiConfigPort(
                previousConfig = wikiConfig(Game.Tekken8),
                saveResult = expected,
            ),
        )

        // when
        val result = service.invoke(wikiConfig(Game.Tekken8, Game.StreetFighter6))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `nothing is deleted when saving fails`() = runTest {
        // given
        val moveStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val characterStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val service = configureWikiService(
            configPort = FakeWikiConfigPort(
                previousConfig = wikiConfig(Game.Tekken8, Game.StreetFighter6),
                saveResult = Result.Error(WikiError.DatabaseError("UNKNOWN")),
            ),
            moveStore = moveStore,
            characterStore = characterStore,
        )
        val expected = setOf(Game.Tekken8, Game.StreetFighter6)

        // when
        service.invoke(wikiConfig(Game.Tekken8))

        // then
        assertThat(moveStore.storedGameSet).isEqualTo(expected)
        assertThat(characterStore.storedGameSet).isEqualTo(expected)
    }
}

private fun configureWikiService(
    configPort: FakeWikiConfigPort,
    moveStore: FakeGameDataStore = FakeGameDataStore(storedGameSet = emptySet()),
    characterStore: FakeGameDataStore = FakeGameDataStore(storedGameSet = emptySet()),
): ConfigureWikiService = ConfigureWikiService(
    loadWikiConfigPort = configPort,
    saveWikiConfigPort = configPort,
    deleteMoveListPort = moveStore,
    deleteCharacterListPort = characterStore,
)

private fun wikiConfig(vararg enabledGames: Game): WikiConfig = WikiConfig(
    availableGameSet = availableGameSet,
    enabledGameSet = enabledGames.toSet(),
)

private class FakeWikiConfigPort(
    previousConfig: WikiConfig?,
    private val saveResult: EmptyResult<WikiError> = Result.Success(Unit),
) : LoadWikiConfigPort, SaveWikiConfigPort {
    val wikiConfig = MutableStateFlow(previousConfig)

    override fun subscribe(): Flow<WikiConfig?> {
        return wikiConfig
    }

    override fun save(wikiConfig: WikiConfig): EmptyResult<WikiError> {
        if (saveResult is Result.Success) {
            this.wikiConfig.value = wikiConfig
        }
        return saveResult
    }
}

/**
 * Stands in for both delete ports - one instance for moves, one for characters.
 */
private class FakeGameDataStore(
    storedGameSet: Set<Game>,
    private val failingGameSet: Set<Game> = emptySet(),
) : DeleteMoveListPort, DeleteCharacterListPort {
    val storedGameSet = storedGameSet.toMutableSet()

    override suspend fun delete(game: Game): EmptyResult<DataError.Local> {
        val result: EmptyResult<DataError.Local> = if (game in failingGameSet) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            storedGameSet.remove(game)
            Result.Success(Unit)
        }
        return result
    }
}


