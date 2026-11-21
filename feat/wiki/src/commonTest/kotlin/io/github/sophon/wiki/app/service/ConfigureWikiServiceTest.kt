package io.github.sophon.wiki.app.service

import assertk.assertThat
import assertk.assertions.containsNone
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.app.outPort.DeleteCharacterListPort
import io.github.sophon.wiki.app.outPort.DeleteMoveListPort
import io.github.sophon.wiki.app.outPort.SaveWikiConfigPort
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

private val availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6, Game.GGST)

internal class ConfigureWikiServiceTest {
    @Test
    fun `saves the config on the first call`() = runTest {
        // given
        val configPort = FakeSaveWikiConfigPort()
        val service = configureWikiService(configPort = configPort)
        val newConfig = wikiConfig(Game.Tekken8)

        // when
        service.invoke(newConfig)

        // then
        assertThat(configPort.savedConfig).isEqualTo(newConfig)
    }

    @Test
    fun `disabled games are deleted on the first call`() = runTest {
        // given
        val moveStore = FakeGameDataStore(storedGameSet = availableGameSet)
        val characterStore = FakeGameDataStore(storedGameSet = availableGameSet)
        val service = configureWikiService(
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
    fun `every disabled game is deleted not only newly disabled ones`() = runTest {
        // given
        val moveStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val characterStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val service = configureWikiService(
            moveStore = moveStore,
            characterStore = characterStore,
        )
        service.invoke(wikiConfig(Game.Tekken8, Game.StreetFighter6))
        moveStore.storedGameSet.add(Game.GGST)
        characterStore.storedGameSet.add(Game.GGST)
        val expected = setOf(Game.Tekken8)

        // when
        service.invoke(wikiConfig(Game.Tekken8))

        // then
        assertThat(moveStore.storedGameSet).isEqualTo(expected)
        assertThat(characterStore.storedGameSet).isEqualTo(expected)
    }

    @Test
    fun `enabled games are never deleted`() = runTest {
        // given
        val moveStore = FakeGameDataStore(storedGameSet = availableGameSet)
        val characterStore = FakeGameDataStore(storedGameSet = availableGameSet)
        val service = configureWikiService(
            moveStore = moveStore,
            characterStore = characterStore,
        )
        val enabledGameSet = setOf(Game.Tekken8, Game.StreetFighter6)

        // when
        service.invoke(wikiConfig(Game.Tekken8, Game.StreetFighter6))

        // then
        assertThat(moveStore.deletedGameList).containsNone(*enabledGameSet.toTypedArray())
        assertThat(characterStore.deletedGameList).containsNone(*enabledGameSet.toTypedArray())
    }

    @Test
    fun `disabling a game deletes moves and characters`() = runTest {
        // given
        val moveStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val characterStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8, Game.StreetFighter6))
        val service = configureWikiService(
            moveStore = moveStore,
            characterStore = characterStore,
        )
        service.invoke(wikiConfig(Game.Tekken8, Game.StreetFighter6))
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
        val moveStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8))
        val characterStore = FakeGameDataStore(storedGameSet = setOf(Game.Tekken8))
        val service = configureWikiService(
            moveStore = moveStore,
            characterStore = characterStore,
        )
        service.invoke(wikiConfig(Game.Tekken8))
        moveStore.storedGameSet.add(Game.StreetFighter6)
        characterStore.storedGameSet.add(Game.StreetFighter6)
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
        val configPort = FakeSaveWikiConfigPort()
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
        assertThat(configPort.savedConfig).isEqualTo(expected)
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
            configPort = FakeSaveWikiConfigPort(saveResult = expected),
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
            configPort = FakeSaveWikiConfigPort(saveResult = Result.Error(WikiError.DatabaseError("UNKNOWN"))),
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
    configPort: FakeSaveWikiConfigPort = FakeSaveWikiConfigPort(),
    moveStore: FakeGameDataStore = FakeGameDataStore(storedGameSet = emptySet()),
    characterStore: FakeGameDataStore = FakeGameDataStore(storedGameSet = emptySet()),
): ConfigureWikiService {
    val service = ConfigureWikiService(
        saveWikiConfigPort = configPort,
        deleteMoveListPort = moveStore,
        deleteCharacterListPort = characterStore,
    )
    return service
}

private fun wikiConfig(vararg enabledGames: Game): WikiConfig {
    val result = WikiConfig.create(
        availableGameSet = availableGameSet,
        enabledGameSet = enabledGames.toSet(),
    )
    val wikiConfig = (result as Result.Success).data
    return wikiConfig
}

private class FakeSaveWikiConfigPort(
    private val saveResult: EmptyResult<WikiError> = Result.Success(Unit),
) : SaveWikiConfigPort {
    var savedConfig: WikiConfig? = null
        private set

    override fun save(wikiConfig: WikiConfig): EmptyResult<WikiError> {
        if (saveResult is Result.Success) {
            savedConfig = wikiConfig
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
    val deletedGameList = mutableListOf<Game>()

    override suspend fun delete(game: Game): EmptyResult<DataError.Local> {
        val result: EmptyResult<DataError.Local> = if (game in failingGameSet) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            storedGameSet.remove(game)
            deletedGameList.add(game)
            Result.Success(Unit)
        }
        return result
    }
}
