package io.github.sophon.fightingnerd.adapter.outbound.wiki

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.MoveFilter
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.model.game.T8Properties
import io.github.sophon.wiki.ConfigureWikiUseCase
import io.github.sophon.wiki.GetAvailableGamesUseCase
import io.github.sophon.wiki.GetCharacterListUseCase
import io.github.sophon.wiki.GetFiltersUseCase
import io.github.sophon.wiki.GetGroupsUseCase
import io.github.sophon.wiki.GetMoveListUseCase
import io.github.sophon.wiki.GetUpdateTimeStampUseCase
import io.github.sophon.wiki.RefreshDataUseCase
import io.github.sophon.wiki.SubscribeToWikiEventsUseCase
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Filter
import io.github.sophon.wiki.model.Group
import io.github.sophon.wiki.model.WikiEvent
import io.github.sophon.wiki.model.WavuFilters
import io.github.sophon.wiki.model.WavuGroups
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant
import io.github.sophon.wiki.model.Character as WikiCharacter
import io.github.sophon.wiki.model.Move as WikiMove
import io.github.sophon.wiki.model.game.T8Properties as WikiT8Properties
import io.github.sophon.wiki.model.wiki.Game as WikiGame

internal class WikiAdapterTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
    )
    private val jin = WikiCharacter(
        id = CharacterId(game = WikiGame.Tekken8, naturalId = "jin"),
        displayName = "Jin",
        remoteQueryId = "Jin",
        wikiUrl = "https://wavu.wiki/t/Jin",
    )
    private val armorKing = WikiCharacter(
        id = CharacterId(game = WikiGame.Tekken8, naturalId = "armor_king"),
        displayName = "Armor King",
        remoteQueryId = "Armor King",
        wikiUrl = "https://wavu.wiki/t/Armor_King",
    )
    private val ryu = WikiCharacter(
        id = CharacterId(game = WikiGame.StreetFighter6, naturalId = "ryu"),
        displayName = "Ryu",
        remoteQueryId = "Ryu",
        wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu",
    )

    private val jinOneOneTwo = WikiMove(
        input = "1,1,2",
        urls = WikiMove.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,1,2"),
    )
    private val jinDownForwardOne = WikiMove(
        input = "d/f+1",
        urls = WikiMove.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-df+1"),
    )
    private val jinHeatDash = WikiMove(
        input = "2,1,H.2",
        urls = WikiMove.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-2,1,H.2"),
        gameProperties = WikiT8Properties(isHeat = true, isHoming = true),
    )
    private val jinZenOne = WikiMove(
        input = "ZEN.1",
        urls = WikiMove.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-ZEN.1"),
        gameProperties = WikiT8Properties(stance = "zen"),
    )

    @Test
    fun `only the game's characters are listed`() = runTest {
        // given
        val adapter = wikiAdapter(characterList = listOf(jin, ryu, armorKing))
        val expected = listOf(
            Character(id = "jin", displayName = "Jin"),
            Character(id = "armor_king", displayName = "Armor King"),
        )

        // when
        val result = adapter.subscribeToCharacters(tekken8.id).first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `moves come from the character's move list`() = runTest {
        // given
        val adapter = wikiAdapter(
            moveListById = mapOf(jin.id to listOf(jinOneOneTwo, jinDownForwardOne)),
            groupList = listOf(WavuGroups.Neutral),
        )
        val expected = listOf(
            Move(
                input = "1,1,2",
                urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,1,2"),
                groupId = "n",
            ),
            Move(
                input = "d/f+1",
                urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-df+1"),
                groupId = "Other",
            ),
        )

        // when
        val result = adapter.subscribeToMoves(tekken8.id, "jin").first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move belongs to the first group it matches`() = runTest {
        // given
        val adapter = wikiAdapter(
            moveListById = mapOf(jin.id to listOf(jinHeatDash)),
            groupList = listOf(WavuGroups.Heat, WavuGroups.Neutral),
        )
        val expected = "Heat"

        // when
        val result = adapter.subscribeToMoves(tekken8.id, "jin").first()

        // then
        assertThat(result.single().groupId).isEqualTo(expected)
    }

    @Test
    fun `stance moves belong to their stance group`() = runTest {
        // given
        val adapter = wikiAdapter(
            moveListById = mapOf(jin.id to listOf(jinZenOne)),
            groupList = listOf(WavuGroups.Neutral),
        )
        val expected = "ZEN"

        // when
        val result = adapter.subscribeToMoves(tekken8.id, "jin").first()

        // then
        assertThat(result.single().groupId).isEqualTo(expected)
    }

    @Test
    fun `move lists the filters it passes`() = runTest {
        // given
        val adapter = wikiAdapter(
            moveListById = mapOf(jin.id to listOf(jinHeatDash)),
            filterSet = setOf(WavuFilters.Heat, WavuFilters.Homing, WavuFilters.PowerCrush),
        )
        val expected = setOf("Heat", "Homing")

        // when
        val result = adapter.subscribeToMoves(tekken8.id, "jin").first()

        // then
        assertThat(result.single().filterNameSet).isEqualTo(expected)
    }

    @Test
    fun `unknown game has no moves`() = runTest {
        // given
        val adapter = wikiAdapter()
        val expected = emptyList<Move>()

        // when
        val result = adapter.subscribeToMoves("Tekken_9", "jin").first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `filters are the game's filter names`() {
        // given
        val adapter = wikiAdapter(filterSet = setOf(WavuFilters.Heat, WavuFilters.PowerCrush))
        val expected = Result.Success(setOf(MoveFilter.Named("Heat"), MoveFilter.Named("PowerCrush")))

        // when
        val result = adapter.loadFilters(tekken8.id)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unknown game has no filters`() {
        // given
        val adapter = wikiAdapter()
        val expected = Result.Error(AppError.GameNotFound("Tekken_9"))

        // when
        val result = adapter.loadFilters("Tekken_9")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `group order has the stances after the game's groups and the ungrouped moves last`() {
        // given
        val adapter = wikiAdapter(groupList = listOf(WavuGroups.Heat, WavuGroups.Neutral))
        val moveList = listOf(
            Move(
                input = "ZEN.1",
                urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-ZEN.1"),
                gameProperties = T8Properties(stance = "zen"),
                groupId = "ZEN",
            ),
        )
        val expected = Result.Success(listOf("Heat", "n", "ZEN", "Other"))

        // when
        val result = adapter.loadGroupIdList(tekken8.id, moveList)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unknown game has no group order`() {
        // given
        val adapter = wikiAdapter()
        val expected = Result.Error(AppError.GameNotFound("Tekken_9"))

        // when
        val result = adapter.loadGroupIdList("Tekken_9", emptyList())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `each wiki is listed once`() = runTest {
        // given
        val adapter = wikiAdapter(availableGameSet = setOf(WikiGame.Tekken8, WikiGame.StreetFighter6, WikiGame.MK1))
        val expected = setOf(
            Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
            Wiki(name = "SuperCombo Wiki", url = "https://wiki.supercombo.gg/", iconUrl = "https://wiki.supercombo.gg/srk_wordmark.png"),
        )

        // when
        val result = adapter.subscribeToWikis().first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unknown games are left out of a refresh`() = runTest {
        // given
        val refreshDataUseCase = FakeRefreshDataUseCase()
        val adapter = wikiAdapter(refreshDataUseCase = refreshDataUseCase)
        val expected = listOf(setOf(WikiGame.Tekken8))

        // when
        adapter.refresh(setOf("Tekken_8", "Tekken_9")).toList()

        // then
        assertThat(refreshDataUseCase.refreshedList).isEqualTo(expected)
    }


    private fun wikiAdapter(
        characterList: List<WikiCharacter> = emptyList(),
        moveListById: Map<CharacterId, List<WikiMove>> = emptyMap(),
        filterSet: Set<Filter> = emptySet(),
        groupList: List<Group> = emptyList(),
        availableGameSet: Set<WikiGame> = emptySet(),
        refreshDataUseCase: FakeRefreshDataUseCase = FakeRefreshDataUseCase(),
    ): WikiAdapter {
        val adapter = WikiAdapter(
            configureWikiUseCase = UnusedConfigureWikiUseCase(),
            refreshDataUseCase = refreshDataUseCase,
            subscribeToWikiEventsUseCase = UnusedSubscribeToWikiEventsUseCase(),
            getAvailableGamesUseCase = FakeGetAvailableGamesUseCase(availableGameSet),
            getCharacterListUseCase = FakeGetCharacterListUseCase(characterList),
            getMoveListUseCase = FakeGetMoveListUseCase(moveListById),
            getFiltersUseCase = FakeGetFiltersUseCase(filterSet),
            getGroupsUseCase = FakeGetGroupsUseCase(groupList),
            getUpdateTimeStampUseCase = UnusedGetUpdateTimeStampUseCase(),
        )
        return adapter
    }

    private class FakeGetCharacterListUseCase(
        private val characterList: List<WikiCharacter>,
    ): GetCharacterListUseCase {
        override fun invoke(): Flow<List<WikiCharacter>> {
            val flow = flowOf(characterList)
            return flow
        }
    }

    private class FakeGetMoveListUseCase(
        private val moveListById: Map<CharacterId, List<WikiMove>>,
    ): GetMoveListUseCase {
        override fun invoke(characterId: CharacterId): Flow<List<WikiMove>> {
            val flow = flowOf(moveListById[characterId].orEmpty())
            return flow
        }
    }

    private class FakeGetFiltersUseCase(
        private val filterSet: Set<Filter>,
    ): GetFiltersUseCase {
        override fun invoke(game: WikiGame): Set<Filter> {
            return filterSet
        }
    }

    /**
     * Same contract as the wiki's - every extra is a Tekken stance that becomes its own group after the others.
     */
    private class FakeGetGroupsUseCase(
        private val groupList: List<Group>,
    ): GetGroupsUseCase {
        override fun invoke(game: WikiGame, extras: List<String>): List<Group> {
            val groupListWithStances = (groupList + extras.map { stance -> WavuGroups.Stance(stance) })
            return groupListWithStances
        }
    }

    private class UnusedConfigureWikiUseCase: ConfigureWikiUseCase {
        override suspend fun invoke(wikiConfig: WikiConfig): EmptyResult<WikiError> {
            error("not used")
        }
    }

    private class FakeRefreshDataUseCase: RefreshDataUseCase {
        val refreshedList = mutableListOf<Set<WikiGame>>()

        override fun invoke(): Flow<WikiEvent.Refresh> {
            error("not used")
        }

        override fun invoke(gameSet: Set<WikiGame>): Flow<WikiEvent.Refresh> {
            refreshedList.add(gameSet)
            return emptyFlow()
        }
    }

    private class UnusedSubscribeToWikiEventsUseCase: SubscribeToWikiEventsUseCase {
        override fun invoke(): Flow<WikiEvent> {
            return emptyFlow()
        }
    }

    private class UnusedGetUpdateTimeStampUseCase: GetUpdateTimeStampUseCase {
        override fun invoke(game: WikiGame): Flow<Instant?> {
            return emptyFlow()
        }
    }

    private class FakeGetAvailableGamesUseCase(
        private val gameSet: Set<WikiGame>,
    ): GetAvailableGamesUseCase {
        override fun invoke(): Flow<Set<WikiGame>> {
            return flowOf(gameSet)
        }
    }
}
