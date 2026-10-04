package io.github.sophon.wiki.application.domain.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.RefreshEvent
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.domain.model.wiki.Game
import io.github.sophon.wiki.application.port.outbound.FetchGameDataPort
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.SaveCharacterMoveListPort
import io.github.sophon.wiki.application.port.outbound.StrikeCharacterListPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class RefreshDataServiceTest {
    @Test
    fun `a duplicate input keeps the first move in wiki order`() = runTest {
        // given
        val store = FakeCharacterMoveListStore()
        val service = refreshDataService(
            downloadList = listOf(Result.Success(yoshimitsu to listOf(fleaRoll, fleaDigger, fleaDiggerStrike))),
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
    fun `strikes spare the downloaded characters`() = runTest {
        // given
        val store = FakeCharacterMoveListStore()
        val service = refreshDataService(
            downloadList = listOf(
                Result.Success(armorKing to emptyList()),
                Result.Success(yoshimitsu to emptyList()),
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
    fun `a failed download strikes nobody`() = runTest {
        // given
        val store = FakeCharacterMoveListStore()
        val service = refreshDataService(
            downloadList = listOf(Result.Error(DataError.Remote.NO_INTERNET)),
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
            downloadList = listOf(Result.Success(yoshimitsu to listOf(fleaRoll))),
            store = FakeCharacterMoveListStore(strikeResult = Result.Error(DataError.Local.UNKNOWN)),
        )

        // when
        val eventList = service.invoke().toList()

        // then
        val failedEvent = eventList.filterIsInstance<RefreshEvent.Failed>().single()
        assertThat(failedEvent.error).isInstanceOf(WikiError.DatabaseError::class)
    }
}

private fun refreshDataService(
    downloadList: List<Result<Pair<Character, List<Move>>, DataError.Remote>>,
    store: FakeCharacterMoveListStore,
): RefreshDataService = RefreshDataService(
    loadWikiConfigPort = FakeLoadWikiConfigPort(Game.Tekken8),
    fetchGameDataPort = FakeFetchGameDataPort(mapOf(Game.Tekken8 to downloadList)),
    saveCharacterMoveListPort = store,
    strikeCharacterListPort = store,
)

// raw, like the Wavu adapter maps them - the service normalizes

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

private class FakeLoadWikiConfigPort(vararg enabledGames: Game) : LoadWikiConfigPort {
    private val wikiConfig = WikiConfig.create(
        availableGameSet = enabledGames.toSet(),
        enabledGameSet = enabledGames.toSet(),
    )

    override fun subscribe(): Flow<WikiConfig?> {
        val config = (wikiConfig as Result.Success).data
        return flowOf(config)
    }
}

private class FakeFetchGameDataPort(
    private val downloadListByGame: Map<Game, List<Result<Pair<Character, List<Move>>, DataError.Remote>>>,
) : FetchGameDataPort {
    override fun fetch(game: Game): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>> {
        return downloadListByGame[game].orEmpty().asFlow()
    }
}

/**
 * Stands in for both write ports - records what the service hands over, keeps no strikes of its own.
 */
private class FakeCharacterMoveListStore(
    private val strikeResult: EmptyResult<DataError.Local> = Result.Success(Unit),
) : SaveCharacterMoveListPort, StrikeCharacterListPort {
    val savedMoveListById = mutableMapOf<CharacterId, List<Move>>()
    val strikeCallList = mutableListOf<Pair<Game, Set<CharacterId>>>()

    override suspend fun save(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local> {
        savedMoveListById[character.id] = moveList
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
