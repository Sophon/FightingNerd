package io.github.sophon.botdiscord.feat.core.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.ExperimentalTime

class GetMovesUseCaseTest {
    @Test
    fun `useCase handles basic moves`() = runTest {
        //given
        val charName = "jin"
        val useCase = GetMovesUseCase()
        val moves = listOf(
            createMove(
                input = "4",
                properties = Move.T8Properties(
                    isHoming = true,
                ),
            ),
            createMove(
                input = "zen2",
                properties = Move.T8Properties(
                    isHoming = true,
                ),
            ),
            createMove(
                input = "zen2",
                properties = Move.T8Properties(
                    isHoming = true,
                ),
            ),
            createMove(
                input = "zen3",
                properties = Move.T8Properties(
                    isHoming = true,
                ),
            ),
        )
        val expected = Result.Success(
            listOf(
                createMove(
                    input = "4",
                    properties = Move.T8Properties(
                        isHoming = true,
                    ),
                ),
                createMove(
                    input = "zen2",
                    properties = Move.T8Properties(
                        isHoming = true,
                    ),
                ),
                createMove(
                    input = "zen3",
                    properties = Move.T8Properties(
                        isHoming = true,
                    ),
                ),
            )
        )

        //when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                character = Character(
                    id = charName,
                    displayName = charName,
                    remoteQueryId = charName,
                    wikiUrl = "",
                ),
                moves = moves,
            ),
            charName = charName,
            filter = Filter.None,
        )

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `useCase resolves alias to character id before fetching moves`() = runTest {
        //given
        val alias = "kuni"
        val characterId = "kunimitsu"
        val useCase = GetMovesUseCase()
        val moves = listOf(
            createMove(
                input = "1",
                properties = Move.T8Properties(isHoming = true),
            ),
        )
        val expected = Result.Success(moves)

        //when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                character = Character(
                    id = characterId,
                    displayName = characterId,
                    remoteQueryId = characterId,
                    wikiUrl = "",
                    aliasList = listOf(alias),
                ),
                moves = moves,
            ),
            charName = alias,
            filter = Filter.None,
        )

        //then
        assertThat(result).isEqualTo(expected)
    }

    //region Fakes and helpers
    private fun createMove(
        input: String,
        properties: Move.T8Properties,
    ): Move {
        return Move(
            characterId = "Test",
            id = input,
            startup = "",
            input = input,
            urls = Move.Urls(wikiUrl = "TODO"),
            t8Properties = properties,
        )
    }

    private class FakeWikiClient(
        private val character: Character? = null,
        private val moves: List<Move> = emptyList(),
    ): WikiClient {
        override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> {
            val matched = character?.takeIf {
                characterQuery == it.id || characterQuery in it.aliasList
            }
            return matched?.let { Result.Success(it) }
                ?: Result.Error(WikiError.UnknownCharacter(characterQuery))
        }

        override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> {
            return if (characterQuery == character?.id) {
                Result.Success(moves)
            } else {
                Result.Error(WikiError.UnknownCharacter(characterQuery))
            }
        }

        override suspend fun fetchMove(
            characterId: String,
            moveQuery: String,
        ): Result<Move, WikiError> {
            return moves.firstOrNull()?.let { Result.Success(it) }
                ?: Result.Error(WikiError.UnknownMove(moveQuery))
        }

        override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = error("Not yet implemented")
        override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = error("Not yet implemented")
        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = error("Not yet implemented")
        override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> = error("Not yet implemented")
        override suspend fun cacheMoveList(character: Character, moveList: List<Move>, ): EmptyResult<WikiError> = error("Not yet implemented")
        @OptIn(ExperimentalTime::class)
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = error("Not yet implemented")
        override suspend fun clearCache(): EmptyResult<WikiError> = error("Not yet implemented")
        override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = Result.Success(true)
        override val featureInfo: FeatureInfo = WavuFeatureInfo.featureInfo
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    }
    //endregion
}