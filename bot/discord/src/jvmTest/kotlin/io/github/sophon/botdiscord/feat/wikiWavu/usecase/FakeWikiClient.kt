package io.github.sophon.botdiscord.feat.wikiWavu.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class FakeWikiClient : WikiClient {
    override val featureInfo: FeatureInfo = WavuFeatureInfo.featureInfo

    override fun subscribeToCharacterList(): Flow<List<Character>> {
        val characterList = listOf(JIN, LAW)
        return flowOf(characterList)
    }

    override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> {
        val moveList = when (characterId.value) {
            JIN.id -> createJinZenMoves() + createJinDvsMoves()
            LAW.id -> createLawMoves()
            else -> emptyList()
        }
        return flowOf(moveList)
    }

    private fun createJinZenMoves(): List<Move> {
        return listOf(
            createMove(characterId = JIN.id, id = "jin-zen1", input = "zen1", stance = "zen"),
            createMove(characterId = JIN.id, id = "jin-zen1+2", input = "zen1+2", stance = "zen"),
            createMove(characterId = JIN.id, id = "jin-zen12", input = "zen12", stance = "zen"),
        )
    }

    private fun createJinDvsMoves(): List<Move> {
        return listOf(
            createMove(characterId = JIN.id, id = "jin-df4", input = "df4", stance = "dvs"),
        )
    }

    private fun createLawMoves(): List<Move> {
        return listOf(
            createMove(characterId = LAW.id, id = "law-1", input = "1"),
            createMove(characterId = LAW.id, id = "law-11", input = "12"),
            createMove(characterId = LAW.id, id = "law-112", input = "112"),
        )
    }

    private fun createMove(
        characterId: String,
        id: String,
        input: String,
        stance: String? = null,
    ): Move {
        return Move(
            characterId = characterId,
            id = id,
            input = input,
            urls = Move.Urls(wikiUrl = ""),
            t8Properties = stance?.let { Move.T8Properties(stance = it) },
        )
    }

    private companion object {
        private val JIN = Character(
            id = "jin",
            displayName = "Jin",
            remoteQueryId = "jin",
            wikiUrl = "",
        )
        private val LAW = Character(
            id = "law",
            displayName = "Law",
            remoteQueryId = "law",
            wikiUrl = "",
        )
    }

    override suspend fun refreshData(): EmptyResult<WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun clearCache(): EmptyResult<WikiError> = throw NotImplementedError("Not used in these tests")
    override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> = throw NotImplementedError("Not used in these tests")
    override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> = throw NotImplementedError("Not used in these tests")
}
