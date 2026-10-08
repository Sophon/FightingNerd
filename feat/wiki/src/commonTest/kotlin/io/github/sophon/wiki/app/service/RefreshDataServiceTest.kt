package io.github.sophon.wiki.app.service

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.app.outPort.LoadWikiConfigPort
import io.github.sophon.wiki.app.outPort.PublishWikiEventPort
import io.github.sophon.wiki.app.outPort.SaveCharacterListPort
import io.github.sophon.wiki.app.outPort.SaveGameDataPort
import io.github.sophon.wiki.app.outPort.SaveMoveListPort
import io.github.sophon.wiki.app.outPort.StrikeCharacterListPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.WikiEvent
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
        val refreshFailedEvent = eventList.filterIsInstance<WikiEvent.Refresh.Failure>().single()
        assertThat(refreshFailedEvent.error).isInstanceOf(WikiError.DownloadError::class)
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
        val refreshFailedEvent = eventList.filterIsInstance<WikiEvent.Refresh.Failure>().single()
        assertThat(refreshFailedEvent.error).isInstanceOf(WikiError.DatabaseError::class)
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

    @Test
    fun `at most three games download at once`() = runTest {
        // given
        val fetchGameDataPort = FakeFetchGameDataPort(
            characterListByGame = mapOf(
                Game.Tekken8 to Result.Success(listOf(yoshimitsu)),
                Game.StreetFighter6 to Result.Success(listOf(ryu)),
                Game.GGST to Result.Success(listOf(sol)),
                Game.MK1 to Result.Success(listOf(scorpion)),
            ),
            fetchDelay = 1.seconds,
        )
        val service = refreshDataService(
            fetchGameDataPort = fetchGameDataPort,
            store = FakeDownloadStore(),
            enabledGameList = listOf(Game.Tekken8, Game.StreetFighter6, Game.GGST, Game.MK1),
        )
        val expected = 3

        // when
        service.invoke().toList()

        // then
        assertThat(fetchGameDataPort.maxCharacterListInFlightCount).isEqualTo(expected)
    }

    @Test
    fun `a game another refresh is downloading is skipped`() = runTest {
        // given
        val fetchGameDataPort = FakeFetchGameDataPort(
            characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(yoshimitsu))),
            moveListByRemoteQueryId = mapOf("Yoshimitsu" to Result.Success(listOf(fleaRoll))),
            fetchDelay = 1.seconds,
        )
        val service = refreshDataService(
            fetchGameDataPort = fetchGameDataPort,
            store = FakeDownloadStore(),
        )
        val expected = emptyList<WikiEvent>()

        // when
        val firstRefresh = async { service.invoke().toList() }
        runCurrent()
        val secondEventList = service.invoke().toList()
        firstRefresh.await()

        // then
        assertThat(secondEventList).isEqualTo(expected)
        assertThat(fetchGameDataPort.characterListCallCount).isEqualTo(1)
    }

    @Test
    fun `a finished game can be refreshed again`() = runTest {
        // given
        val fetchGameDataPort = FakeFetchGameDataPort(
            characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(yoshimitsu))),
            moveListByRemoteQueryId = mapOf("Yoshimitsu" to Result.Success(listOf(fleaRoll))),
        )
        val service = refreshDataService(
            fetchGameDataPort = fetchGameDataPort,
            store = FakeDownloadStore(),
        )
        val expected = listOf(
            WikiEvent.Refresh.Started(Game.Tekken8),
            WikiEvent.Refresh.Progress(Game.Tekken8, 1f),
            WikiEvent.Refresh.Finished(Game.Tekken8, 1),
        )

        // when
        service.invoke().toList()
        val secondEventList = service.invoke().toList()

        // then
        assertThat(secondEventList).isEqualTo(expected)
    }

    @Test
    fun `a crashing game doesn't cancel the others`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(yoshimitsu))),
                moveListByRemoteQueryId = mapOf("Yoshimitsu" to Result.Success(listOf(fleaRoll))),
                crashingGameSet = setOf(Game.StreetFighter6),
            ),
            store = store,
            enabledGameList = listOf(Game.Tekken8, Game.StreetFighter6),
        )
        val expected = setOf(CharacterId(Game.Tekken8, "yoshimitsu"))

        // when
        val eventList = service.invoke().toList()

        // then
        assertThat(store.savedMoveListById.keys).isEqualTo(expected)
        val refreshFailedEvent = eventList.filterIsInstance<WikiEvent.Refresh.Failure>().single()
        assertThat(refreshFailedEvent.error).isInstanceOf(WikiError.DownloadError::class)
    }

    @Test
    fun `a hanging move list times out and the next character still downloads`() = runTest {
        // given
        val store = FakeDownloadStore()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(armorKing, yoshimitsu))),
                moveListByRemoteQueryId = mapOf("Yoshimitsu" to Result.Success(listOf(fleaRoll))),
                hangingRemoteQueryIdSet = setOf("Armor King"),
            ),
            store = store,
        )
        val expected = setOf(CharacterId(Game.Tekken8, "yoshimitsu"))

        // when
        val eventList = service.invoke().toList()

        // then
        assertThat(store.savedMoveListById.keys).isEqualTo(expected)
        val refreshFailedEvent = eventList.filterIsInstance<WikiEvent.Refresh.Failure>().single()
        assertThat(refreshFailedEvent.error).isInstanceOf(WikiError.DownloadError::class)
    }

    @Test
    fun `each game sends its own finished event`() = runTest {
        // given
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(
                    Game.Tekken8 to Result.Success(listOf(armorKing, yoshimitsu)),
                    Game.StreetFighter6 to Result.Success(listOf(ryu)),
                ),
                moveListByRemoteQueryId = mapOf(
                    "Armor King" to Result.Success(emptyList()),
                    "Yoshimitsu" to Result.Success(emptyList()),
                    "Ryu" to Result.Success(emptyList()),
                ),
            ),
            store = FakeDownloadStore(),
            enabledGameList = listOf(Game.Tekken8, Game.StreetFighter6),
        )
        val expected = arrayOf(WikiEvent.Refresh.Finished(Game.Tekken8, 2), WikiEvent.Refresh.Finished(Game.StreetFighter6, 1))

        // when
        val eventList = service.invoke().toList()

        // then
        val refreshFinishedEventList = eventList.filterIsInstance<WikiEvent.Refresh.Finished>()
        assertThat(refreshFinishedEventList).containsExactlyInAnyOrder(*expected)
    }

    @Test
    fun `progress reaches the end even when the last move list fails`() = runTest {
        // given
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(yoshimitsu, armorKing))),
                moveListByRemoteQueryId = mapOf(
                    "Yoshimitsu" to Result.Success(listOf(fleaRoll)),
                    "Armor King" to Result.Error(DataError.Remote.REQUEST_TIMEOUT),
                ),
            ),
            store = FakeDownloadStore(),
        )
        val expected = listOf(0.5f, 1f)

        // when
        val eventList = service.invoke().toList()

        // then
        val fractionList = eventList
            .filterIsInstance<WikiEvent.Refresh.Progress>()
            .map { event -> event.fraction }
        assertThat(fractionList).isEqualTo(expected)
    }

    @Test
    fun `a crashed game still finishes`() = runTest {
        // given
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(crashingGameSet = setOf(Game.Tekken8)),
            store = FakeDownloadStore(),
        )
        val expected = WikiEvent.Refresh.Finished(Game.Tekken8, 0)

        // when
        val eventList = service.invoke().toList()

        // then
        assertThat(eventList.last()).isEqualTo(expected)
    }

    @Test
    fun `every refresh event is published`() = runTest {
        // given
        val publishWikiEventPort = FakePublishWikiEventPort()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(armorKing, yoshimitsu))),
                moveListByRemoteQueryId = mapOf(
                    "Armor King" to Result.Success(emptyList()),
                    "Yoshimitsu" to Result.Success(emptyList()),
                ),
            ),
            store = FakeDownloadStore(),
            publishWikiEventPort = publishWikiEventPort,
        )
        val expected = listOf(
            WikiEvent.Refresh.Started(Game.Tekken8),
            WikiEvent.Refresh.Progress(Game.Tekken8, 0.5f),
            WikiEvent.Refresh.Progress(Game.Tekken8, 1f),
            WikiEvent.Refresh.Finished(Game.Tekken8, 2),
        )

        // when
        service.invoke().toList()

        // then
        assertThat(publishWikiEventPort.publishedEventList).isEqualTo(expected)
    }

    @Test
    fun `a cancelled game still finishes for the subscribers`() = runTest {
        // given
        val publishWikiEventPort = FakePublishWikiEventPort()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(yoshimitsu))),
                fetchDelay = 1.seconds,
            ),
            store = FakeDownloadStore(),
            publishWikiEventPort = publishWikiEventPort,
        )
        val expected = listOf(
            WikiEvent.Refresh.Started(Game.Tekken8),
            WikiEvent.Refresh.Finished(Game.Tekken8, 0),
        )

        // when
        val refresh = launch { service.invoke().toList() }
        runCurrent()
        refresh.cancelAndJoin()

        // then
        assertThat(publishWikiEventPort.publishedEventList).isEqualTo(expected)
    }

    @Test
    fun `a game cancelled while waiting for a download slot sends nothing`() = runTest {
        // given
        val publishWikiEventPort = FakePublishWikiEventPort()
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(
                    Game.Tekken8 to Result.Success(listOf(yoshimitsu)),
                    Game.StreetFighter6 to Result.Success(listOf(ryu)),
                    Game.GGST to Result.Success(listOf(sol)),
                    Game.MK1 to Result.Success(listOf(scorpion)),
                ),
                fetchDelay = 1.seconds,
            ),
            store = FakeDownloadStore(),
            enabledGameList = listOf(Game.Tekken8, Game.StreetFighter6, Game.GGST, Game.MK1),
            publishWikiEventPort = publishWikiEventPort,
        )

        // when
        val refresh = launch { service.invoke().toList() }
        runCurrent()
        refresh.cancelAndJoin()

        // then
        val startedGameSet = publishWikiEventPort.publishedEventList
            .filterIsInstance<WikiEvent.Refresh.Started>()
            .map { event -> event.game }
            .toSet()
        val finishedGameSet = publishWikiEventPort.publishedEventList
            .filterIsInstance<WikiEvent.Refresh.Finished>()
            .map { event -> event.game }
            .toSet()
        assertThat(startedGameSet).hasSize(3)
        assertThat(finishedGameSet).isEqualTo(startedGameSet)
    }

    @Test
    fun `a failed move list names its character`() = runTest {
        // given
        val service = refreshDataService(
            fetchGameDataPort = FakeFetchGameDataPort(
                characterListByGame = mapOf(Game.Tekken8 to Result.Success(listOf(armorKing))),
                moveListByRemoteQueryId = mapOf("Armor King" to Result.Error(DataError.Remote.REQUEST_TIMEOUT)),
            ),
            store = FakeDownloadStore(),
        )
        val expected = CharacterId(Game.Tekken8, "armor_king")

        // when
        val eventList = service.invoke().toList()

        // then
        val failureEvent = eventList.filterIsInstance<WikiEvent.Refresh.Failure>().single()
        assertThat(failureEvent.characterId).isEqualTo(expected)
    }
}

private fun refreshDataService(
    fetchGameDataPort: FetchGameDataPort,
    store: FakeDownloadStore,
    enabledGameList: List<Game> = listOf(Game.Tekken8),
    publishWikiEventPort: PublishWikiEventPort = FakePublishWikiEventPort(),
): RefreshDataService {
    val service = RefreshDataService(
        loadWikiConfigPort = FakeLoadWikiConfigPort(enabledGameList.toSet()),
        fetchGameDataPort = fetchGameDataPort,
        saveCharacterListPort = store,
        saveMoveListPort = store,
        saveGameDataPort = store,
        strikeCharacterListPort = store,
        publishWikiEventPort = publishWikiEventPort,
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

private val sol = Character(
    id = CharacterId(Game.GGST, "Sol Badguy"),
    displayName = "Sol Badguy",
    remoteQueryId = "Sol Badguy",
    wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy",
)

private val scorpion = Character(
    id = CharacterId(Game.MK1, "Scorpion"),
    displayName = "Scorpion",
    remoteQueryId = "Scorpion",
    wikiUrl = "https://srk.shib.live/w/Mortal_Kombat_1/Scorpion",
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
 * like a shape the wiki doesn't serve. A crashing game throws, a hanging character never answers.
 */
private class FakeFetchGameDataPort(
    private val characterListByGame: Map<Game, Result<List<Character>, DataError.Remote>> = emptyMap(),
    private val moveListByRemoteQueryId: Map<String, Result<List<Move>, DataError.Remote>> = emptyMap(),
    private val gameDataByGame: Map<Game, Result<List<Pair<Character, List<Move>>>, DataError.Remote>> = emptyMap(),
    private val fetchDelay: Duration = Duration.ZERO,
    private val crashingGameSet: Set<Game> = emptySet(),
    private val hangingRemoteQueryIdSet: Set<String> = emptySet(),
) : FetchGameDataPort {
    var characterListCallCount = 0
    var maxCharacterListInFlightCount = 0
    private var characterListInFlightCount = 0

    override suspend fun fetchCharacterList(game: Game): Result<List<Character>, DataError.Remote> {
        characterListCallCount++
        characterListInFlightCount++
        maxCharacterListInFlightCount = maxOf(maxCharacterListInFlightCount, characterListInFlightCount)
        try {
            delay(fetchDelay)
        } finally {
            characterListInFlightCount--
        }
        check(game !in crashingGameSet) { "${game.id} wiki crashed" }

        return characterListByGame[game] ?: Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }

    override suspend fun fetchMoveList(character: Character): Result<List<Move>, DataError.Remote> {
        if (character.remoteQueryId in hangingRemoteQueryIdSet) awaitCancellation()

        return moveListByRemoteQueryId[character.remoteQueryId] ?: Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }

    override suspend fun fetchGameData(game: Game): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        return gameDataByGame[game] ?: Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }
}

private class FakePublishWikiEventPort : PublishWikiEventPort {
    val publishedEventList = mutableListOf<WikiEvent>()

    override suspend fun publish(event: WikiEvent) {
        publishedEventList.add(event)
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
