package io.github.sophon.fightingnerd.adapter.outbound.wiki

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.wiki.inPort.ConfigureWikiUseCase
import io.github.sophon.wiki.inPort.GetAvailableGamesUseCase
import io.github.sophon.wiki.inPort.GetCharacterListUseCase
import io.github.sophon.wiki.inPort.GetMoveListUseCase
import io.github.sophon.wiki.inPort.RefreshDataUseCase
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.RefreshEvent
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import io.github.sophon.wiki.model.Character as WikiCharacter
import io.github.sophon.wiki.model.wiki.Game as WikiGame

internal class WikiAdapterTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wikiName = "Wavu Wiki",
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

    @Test
    fun `only the game's characters are listed`() = runTest {
        // given
        val adapter = wikiAdapter(characterList = listOf(jin, ryu, armorKing))
        val expected = listOf(
            Character(id = "jin", displayName = "Jin"),
            Character(id = "armor_king", displayName = "Armor King"),
        )

        // when
        val result = adapter.subscribeToCharacters(tekken8).first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `moves come from the character's move list`() = runTest {
        // given
        val moveList = listOf(
            Move(input = "1,1,2", urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,1,2")),
            Move(input = "d/f+1", urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-df+1")),
        )
        val adapter = wikiAdapter(moveListById = mapOf(jin.id to moveList))
        val expected = moveList

        // when
        val result = adapter.subscribeToMoves(tekken8, "jin").first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unknown game has no moves`() = runTest {
        // given
        val tekken9 = tekken8.copy(id = "Tekken_9", displayName = "Tekken 9")
        val adapter = wikiAdapter()
        val expected = emptyList<Move>()

        // when
        val result = adapter.subscribeToMoves(tekken9, "jin").first()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private fun wikiAdapter(
        characterList: List<WikiCharacter> = emptyList(),
        moveListById: Map<CharacterId, List<Move>> = emptyMap(),
    ): WikiAdapter {
        val adapter = WikiAdapter(
            configureWikiUseCase = UnusedConfigureWikiUseCase(),
            refreshDataUseCase = UnusedRefreshDataUseCase(),
            getAvailableGamesUseCase = UnusedGetAvailableGamesUseCase(),
            getCharacterListUseCase = FakeGetCharacterListUseCase(characterList),
            getMoveListUseCase = FakeGetMoveListUseCase(moveListById),
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
        private val moveListById: Map<CharacterId, List<Move>>,
    ): GetMoveListUseCase {
        override fun invoke(characterId: CharacterId): Flow<List<Move>> {
            val flow = flowOf(moveListById[characterId].orEmpty())
            return flow
        }
    }

    private class UnusedConfigureWikiUseCase: ConfigureWikiUseCase {
        override suspend fun invoke(wikiConfig: WikiConfig): EmptyResult<WikiError> {
            error("not used")
        }
    }

    private class UnusedRefreshDataUseCase: RefreshDataUseCase {
        override fun invoke(): Flow<RefreshEvent> {
            return emptyFlow()
        }
    }

    private class UnusedGetAvailableGamesUseCase: GetAvailableGamesUseCase {
        override fun invoke(): Flow<Set<WikiGame>> {
            return emptyFlow()
        }
    }
}
