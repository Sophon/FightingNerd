package io.github.sophon.wiki.app.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.app.outPort.LoadWikiConfigPort
import io.github.sophon.wiki.app.outPort.SaveCharacterListPort
import io.github.sophon.wiki.app.outPort.SaveGameDataPort
import io.github.sophon.wiki.app.outPort.SaveMoveListPort
import io.github.sophon.wiki.app.outPort.StrikeCharacterListPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.RefreshEvent
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class RefreshDataServiceTest {
    @Test
    fun `a duplicate input keeps the first move in wiki order`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(yoshimitsu))),
                moveListByRemoteQueryId = mapOf(
                    "Yoshimitsu" to Result.Success(listOf(fleaRoll, fleaDigger, fleaDiggerStrike)),
                ),
            ),
            store = store,
        )
        val expected = listOf("Yoshimitsu-FLE.1", "Yoshimitsu-FLE.d")

        // when
        service.invoke().toList()

        // then
        val savedRemoteIdList = store.savedMoveListById
            .getValue(CharacterId(Game.Tekken8, "yoshimitsu"))
            .map { move -> move.remoteId }
        assertThat(savedRemoteIdList).isEqualTo(expected)
    }

    @Test
    fun `the character list is saved before any move list`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(armorKing, yoshimitsu))),
                moveListByRemoteQueryId = mapOf(
                    "Armor King" to Result.Success(emptyList()),
                    "Yoshimitsu" to Result.Success(listOf(fleaRoll)),
                ),
            ),
            store = store,
        )
        val armorKingId = CharacterId(Game.Tekken8, "armor_king")
        val yoshimitsuId = CharacterId(Game.Tekken8, "yoshimitsu")
        val expected = listOf(
            SaveCall.CharacterList(listOf(armorKingId, yoshimitsuId)),
            SaveCall.MoveList(armorKingId),
            SaveCall.MoveList(yoshimitsuId),
        )

        // when
        service.invoke().toList()

        // then
        assertThat(store.saveCallList).isEqualTo(expected)
    }

    @Test
    fun `a failed move list doesn't stop the next character`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(armorKing, yoshimitsu))),
                moveListByRemoteQueryId = mapOf(
                    "Armor King" to Result.Error(DataError.Remote.REQUEST_TIMEOUT),
                    "Yoshimitsu" to Result.Success(listOf(fleaRoll)),
                ),
            ),
            store = store,
        )
        val expected = setOf(CharacterId(Game.Tekken8, "yoshimitsu"))

        // when
        val eventList = service.invoke().toList()

        // then
        assertThat(store.savedMoveListById.keys).isEqualTo(expected)
        val failedEvent = eventList.filterIsInstance<RefreshEvent.Failed>().single()
        assertThat(failedEvent.error).isInstanceOf(WikiError.DownloadError::class)
    }

    @Test
    fun `strikes spare the downloaded characters`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(armorKing, yoshimitsu))),
                moveListByRemoteQueryId = mapOf(
                    "Armor King" to Result.Success(emptyList()),
                    "Yoshimitsu" to Result.Success(emptyList()),
                ),
            ),
            store = store,
        )
        val expected = listOf(
            Game.Tekken8 to setOf(CharacterId(Game.Tekken8, "armor_king"), CharacterId(Game.Tekken8, "yoshimitsu")),
        )

        // when
        service.invoke().toList()

        // then
        assertThat(store.strikeCallList).isEqualTo(expected)
    }

    @Test
    fun `strikes spare a character whose move list failed`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(armorKing, yoshimitsu))),
                moveListByRemoteQueryId = mapOf(
                    "Armor King" to Result.Error(DataError.Remote.REQUEST_TIMEOUT),
                    "Yoshimitsu" to Result.Success(emptyList()),
                ),
            ),
            store = store,
        )
        val expected = listOf(
            Game.Tekken8 to setOf(CharacterId(Game.Tekken8, "armor_king"), CharacterId(Game.Tekken8, "yoshimitsu")),
        )

        // when
        service.invoke().toList()

        // then
        assertThat(store.strikeCallList).isEqualTo(expected)
    }

    @Test
    fun `a failed character list strikes nobody`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Error(DataError.Remote.NO_INTERNET)),
            ),
            store = store,
        )

        // when
        service.invoke().toList()

        // then
        assertThat(store.strikeCallList).isEmpty()
    }

    @Test
    fun `a failed strike emits a database error`() = runTest {
        // given
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(yoshimitsu))),
                moveListByRemoteQueryId = mapOf("Yoshimitsu" to Result.Success(listOf(fleaRoll))),
            ),
            store = FakeDownloadStore(strikeResult = Result.Error(DataError.Local.UNKNOWN)),
        )

        // when
        val eventList = service.invoke().toList()

        // then
        val failedEvent = eventList.filterIsInstance<RefreshEvent.Failed>().single()
        assertThat(failedEvent.error).isInstanceOf(WikiError.DatabaseError::class)
    }

    @Test
    fun `a bulk game is saved in one go`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                gameDataByGame = mapOf(
                    Game.Xko to Result.Success(listOf(darius to emptyList(), ahri to emptyList())),
                ),
            ),
            store = store,
            enabledGameList = listOf(Game.Xko),
        )
        val expected = listOf(
            SaveCall.GameData(listOf(CharacterId(Game.Xko, "darius"), CharacterId(Game.Xko, "ahri"))),
        )

        // when
        service.invoke().toList()

        // then
        assertThat(store.saveCallList).isEqualTo(expected)
    }

    @Test
    fun `a scoped refresh skips the games outside the set`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(
                    Game.Tekken8 to Result.Success(listOf(yoshimitsu)),
                    Game.StreetFighter6 to Result.Success(listOf(ryu)),
                ),
                moveListByRemoteQueryId = mapOf(
                    "Yoshimitsu" to Result.Success(emptyList()),
                    "Ryu" to Result.Success(emptyList()),
                ),
            ),
            store = store,
            enabledGameList = listOf(Game.Tekken8, Game.StreetFighter6),
        )
        val expected = listOf(Game.StreetFighter6)

        // when
        service.invoke(setOf(Game.StreetFighter6)).toList()

        // then
        val refreshedGameList = store.strikeCallList.map { (game, _) -> game }
        assertThat(refreshedGameList).isEqualTo(expected)
    }

    @Test
    fun `a scoped refresh skips the games that aren't enabled`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(
                    Game.Tekken8 to Result.Success(listOf(yoshimitsu)),
                    Game.StreetFighter6 to Result.Success(listOf(ryu)),
                ),
                moveListByRemoteQueryId = mapOf(
                    "Yoshimitsu" to Result.Success(emptyList()),
                    "Ryu" to Result.Success(emptyList()),
                ),
            ),
            store = store,
        )
        val expected = listOf(Game.Tekken8)

        // when
        service.invoke(setOf(Game.Tekken8, Game.StreetFighter6)).toList()

        // then
        val refreshedGameList = store.strikeCallList.map { (game, _) -> game }
        assertThat(refreshedGameList).isEqualTo(expected)
    }
}

private fun refreshDataService(
    fetchGameDataPort: FetchGameDataPort,
    store: FakeDownloadStore,
    enabledGameList: List<Game> = listOf(Game.Tekken8),
): RefreshDataService {
    val service = RefreshDataService(
        loadWikiConfigPort = FakeLoadWikiConfigPort(enabledGameList.toSet()),
        fetchGameDataPort = fetchGameDataPort,
        saveCharacterListPort = store,
        saveMoveListPort = store,
        saveGameDataPort = store,
        strikeCharacterListPort = store,
    )
    return service
}

// raw, like the adapters map them - the service normalizes

private val yoshimitsu = Character(
    id = CharacterId(Game.Tekken8, "Yoshimitsu"),
    displayName = "Yoshimitsu",
    remoteQueryId = "Yoshimitsu",
    wikiUrl = "https://wavu.wiki/t/Yoshimitsu",
)

private val armorKing = Character(
    id = CharacterId(Game.Tekken8, "Armor King"),
    displayName = "Armor King",
    remoteQueryId = "Armor King",
    wikiUrl = "https://wavu.wiki/t/Armor_King",
)

private val ryu = Character(
    id = CharacterId(Game.StreetFighter6, "Ryu"),
    displayName = "Ryu",
    remoteQueryId = "Ryu",
    wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu",
)

private val darius = Character(
    id = CharacterId(Game.Xko, "Darius"),
    displayName = "Darius",
    remoteQueryId = "Darius",
    wikiUrl = "https://wiki.play2xko.com/en-us/Darius",
)

private val ahri = Character(
    id = CharacterId(Game.Xko, "Ahri"),
    displayName = "Ahri",
    remoteQueryId = "Ahri",
    wikiUrl = "https://wiki.play2xko.com/en-us/Ahri",
)

private val fleaRoll = Move(
    input = "FLE.1",
    remoteId = "Yoshimitsu-FLE.1",
    name = "Flea Roll",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Yoshimitsu_movelist#Yoshimitsu-FLE.1"),
)

private val fleaDigger = Move(
    input = "FLE.d",
    remoteId = "Yoshimitsu-FLE.d",
    name = "Flea Digger",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Yoshimitsu_movelist#Yoshimitsu-FLE.d"),
)

// a wiki error - Flea Digger Strike repeats Flea Digger's input
private val fleaDiggerStrike = Move(
    input = "FLE.d",
    remoteId = "Yoshimitsu-FLE.ds",
    name = "Flea Digger Strike",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Yoshimitsu_movelist#Yoshimitsu-FLE.ds"),
)

private class FakeLoadWikiConfigPort(enabledGameSet: Set<Game>) : LoadWikiConfigPort {
    private val wikiConfig = WikiConfig.create(
        availableGameSet = enabledGameSet,
        enabledGameSet = enabledGameSet,
    )

    override fun subscribe(): Flow<WikiConfig?> {
        val config = (wikiConfig as Result.Success).data
        return flowOf(config)
    }
}

/**
 * Hands out what each test sets up - an unset game or character is [DataError.Remote.PAGE_NOT_FOUND],
 * like a shape the wiki doesn't serve.
 */
private class FakeFetchGameDataPort(
    private val characterListByGame: Map<Game, Result<List<Character>, DataError.Remote>> = emptyMap(),
    private val moveListByRemoteQueryId: Map<String, Result<List<Move>, DataError.Remote>> = emptyMap(),
    private val gameDataByGame: Map<Game, Result<List<Pair<Character, List<Move>>>, DataError.Remote>> = emptyMap(),
) : FetchGameDataPort {
    override suspend fun fetchCharacterList(game: Game): Result<List<Character>, DataError.Remote> {
        return characterListByGame[game] ?: Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }

    override suspend fun fetchMoveList(character: Character): Result<List<Move>, DataError.Remote> {
        return moveListByRemoteQueryId[character.remoteQueryId] ?: Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }

    override suspend fun fetchGameData(game: Game): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        return gameDataByGame[game] ?: Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }
}

private sealed interface SaveCall {
    data class CharacterList(val idList: List<CharacterId>) : SaveCall
    data class MoveList(val id: CharacterId) : SaveCall
    data class GameData(val idList: List<CharacterId>) : SaveCall
}

/**
 * Stands in for every write port - records what the service hands over, keeps no strikes of its own.
 */
private class FakeDownloadStore(
    private val strikeResult: EmptyResult<DataError.Local> = Result.Success(Unit),
) : SaveCharacterListPort, SaveMoveListPort, SaveGameDataPort, StrikeCharacterListPort {
    val saveCallList = mutableListOf<SaveCall>()
    val savedMoveListById = mutableMapOf<CharacterId, List<Move>>()
    val strikeCallList = mutableListOf<Pair<Game, Set<CharacterId>>>()

    override suspend fun saveCharacterList(
        characterList: List<Character>,
    ): EmptyResult<DataError.Local> {
        saveCallList.add(SaveCall.CharacterList(characterList.map { character -> character.id }))
        return Result.Success(Unit)
    }

    override suspend fun saveMoveList(
        characterId: CharacterId,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local> {
        saveCallList.add(SaveCall.MoveList(characterId))
        savedMoveListById[characterId] = moveList
        return Result.Success(Unit)
    }

    override suspend fun saveGameData(
        gameData: List<Pair<Character, List<Move>>>,
    ): EmptyResult<DataError.Local> {
        saveCallList.add(SaveCall.GameData(gameData.map { (character, _) -> character.id }))
        gameData.forEach { (character, moveList) -> savedMoveListById[character.id] = moveList }
        return Result.Success(Unit)
    }

    override suspend fun strike(
        game: Game,
        downloadedIdSet: Set<CharacterId>,
    ): EmptyResult<DataError.Local> {
        strikeCallList.add(game to downloadedIdSet.toSet())
        return strikeResult
    }
}
