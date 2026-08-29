package io.github.sophon.botdiscord.feat.wikiDustLoop.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.wikiDustLoop.usecase.FetchDustLoopInvincibleMovesUseCase
import io.github.sophon.wikidustloop.integration.DustLoopFeatureInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class FetchDustLoopInvincibleMovesUseCaseTest {
    private val useCase = FetchDustLoopInvincibleMovesUseCase()

    @Test
    fun `usecase returns move list with only invicible moves`() = runTest {
        // given
        val character = Character(
            id = "charId",
            displayName = "charId",
            remoteQueryId = "charId",
            wikiUrl = "wikiUrl",
            aliasList = emptyList(),
        )
        val moveList = listOf(MoveSource.invulMove, MoveSource.normalMove)
        val wiki = FakeWikiClient(
            characterList = listOf(character),
            moveListByCharacterId = mapOf(character.id to moveList),
        )

        // when
        val result = useCase.invoke(Game.GGST, wiki, "charId")

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val (_, filtered) = (result as Result.Success).data
        val expected = listOf(MoveSource.invulMove)
        assertThat(filtered).isEqualTo(expected)
    }


    private object MoveSource {
        val invulMove = Move(
            characterId = "charId",
            id = "inv",
            input = "6p",
            urls = Move.Urls(wikiUrl = "wikiUrl"),
            invulnerability = "1-3 Below Crouch<br/>4-13 Low Profile"
        )

        val normalMove = Move(
            characterId = "charId",
            id = "normal",
            input = "5k",
            urls = Move.Urls(wikiUrl = "wikiUrl"),
        )
    }

    private class FakeWikiClient(
        private val characterList: List<Character> = emptyList(),
        private val moveListByCharacterId: Map<String, List<Move>> = emptyMap(),
    ) : WikiClient {
        override val featureInfo: FeatureInfo = DustLoopFeatureInfo.featureInfo

        override fun subscribeToCharacterList(): Flow<List<Character>> = flowOf(characterList)
        override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> {
            val moves = moveListByCharacterId[characterId.value].orEmpty()
            return flowOf(moves)
        }

        override fun refreshData(): Flow<RefreshEvent> = throw NotImplementedError("Not used in this use case")
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun clearCache(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    }
}