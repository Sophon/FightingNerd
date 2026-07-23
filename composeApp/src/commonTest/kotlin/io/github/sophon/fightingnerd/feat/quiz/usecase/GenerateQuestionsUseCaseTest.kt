package io.github.sophon.fightingnerd.feat.quiz.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.quiz.COUNT_DISTRACTIONS
import io.github.sophon.fightingnerd.feat.quiz.COUNT_QUESTIONS
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.ExperimentalTime

internal class GenerateQuestionsUseCaseTest {
    private val testCharacterList = listOf(
        Character(id = "kazuya", displayName = "Kazuya", remoteQueryId = "kazuya", wikiUrl = ""),
        Character(id = "jin", displayName = "Jin", remoteQueryId = "jin", wikiUrl = ""),
    )
    private val testMoveList = listOf(
        Move(characterId = "kazuya", id = "1", input = "1", urls = Move.Urls(wikiUrl = "")),
        Move(characterId = "kazuya", id = "2", input = "2", urls = Move.Urls(wikiUrl = "")),
        Move(characterId = "kazuya", id = "3", input = "3", urls = Move.Urls(wikiUrl = "")),
        Move(characterId = "kazuya", id = "4", input = "4", urls = Move.Urls(wikiUrl = "")),
    )
    
    @Test
    fun `usecase generates exactly ten questions`() = runTest {
        // given
        val game = Game.Tekken8
        val wiki = FakeWikiClient(
            fetchCharacterListResult = Result.Success(testCharacterList),
            fetchMoveListResult = Result.Success(testMoveList),
        )
        val repo = FakeRepo(mapOf(game to wiki))
        val usecase = GenerateQuestionsUseCase(repo)
        val expectedCount = COUNT_QUESTIONS
        
        // when
        val result = usecase.invoke(game.id)

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as? Result.Success)?.data?.size).isEqualTo(expectedCount)
    }
    
    @Test
    fun `usecase generates questions with one correct and four distraction options`() = runTest {
        // given
        val game = Game.Tekken8
        val wiki = FakeWikiClient(
            fetchCharacterListResult = Result.Success(testCharacterList),
            fetchMoveListResult = Result.Success(testMoveList),
        )
        val repo = FakeRepo(mapOf(game to wiki))
        val usecase = GenerateQuestionsUseCase(repo)
        val expectedCount = (COUNT_DISTRACTIONS + 1)

        // when
        val result = usecase.invoke(game.id)

        assertThat(result).isInstanceOf(Result.Success::class)
        (result as? Result.Success)?.data?.forEach { question ->
            assertThat(question.options.size).isEqualTo(expectedCount)
        }
    }
    
    @Test
    fun `usecase returns error when game not found`() = runTest {
        // given
        val game = Game.Tekken8
        val repo = FakeRepo(emptyMap())
        val usecase = GenerateQuestionsUseCase(repo)
        val expected = AppError.WikiClientNotFound(game.id)
        
        // when
        val result = usecase.invoke(game.id)

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as? Result.Error)?.error).isEqualTo(expected)
    }
    
    @Test
    fun `usecase returns error when character list fetch fails`() = runTest {
        // given
        val game = Game.Tekken8
        val fetchCharacterListError = WikiError.UnknownCharacter("unknown")
        val wiki = FakeWikiClient(
            fetchCharacterListResult = Result.Error(fetchCharacterListError),
        )
        val repo = FakeRepo(mapOf(game to wiki))
        val usecase = GenerateQuestionsUseCase(repo)
        val expected = AppError.WikiError(fetchCharacterListError.inputs.joinToString(";"))

        // when
        val result = usecase.invoke(game.id)

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as? Result.Error)?.error).isEqualTo(expected)
    }
}


private class FakeRepo(
    private val gameClients: Map<Game, WikiClient> = emptyMap(),
) : FeatureRepo {
    override fun initialize(config: Config): EmptyResult<WikiError> = Result.Success(Unit)
    override fun getGameClients(): Map<Game, WikiClient> = gameClients
    override fun getOtherFeatures(): List<Config.Feature> = emptyList()
    override fun getEnabledFeatureNames(): Set<String> = emptySet()
    override fun getWikiClientFor(game: Game): WikiClient? = gameClients[game]
}

private class FakeWikiClient(
    private val fetchCharacterListResult: Result<List<Character>, WikiError> = Result.Success(emptyList()),
    private val fetchMoveListResult: Result<List<Move>, WikiError> = Result.Success(emptyList()),
) : WikiClient {
    override val featureInfo = FeatureInfo(name = "Fake Wiki", url = "", version = "1.0.0")

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = fetchCharacterListResult
    override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> = fetchMoveListResult

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = error("not used")
    override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = error("not used")
    override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> = error("not used")
    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> = error("not used")
    override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = error("not used")
    override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> = error("not used")
    override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> = error("not used")
    @OptIn(ExperimentalTime::class)
    override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = error("not used")
    override suspend fun clearCache(): EmptyResult<WikiError> = error("not used")
    override fun getFiltersFor(game: Game): Set<Filter> = error("not used")
}
