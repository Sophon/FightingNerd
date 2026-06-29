package io.github.sophon.fightingnerd.feat.quiz.usecase

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.quiz.COUNT_QUESTIONS
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant

@OptIn(ExperimentalTime::class)
class GenerateQuestionsUseCaseTest {

    private val fakeWiki = FakeWikiClient()
    private val fakeRepo = FakeCoreFeatureRepo()
    private val usecase = GenerateQuestionsUseCase(fakeRepo)

    @BeforeTest
    fun setup() {
        fakeRepo.wikiClient = fakeWiki
        fakeWiki.characterListResult = Result.Success(listOf(MoveSource.character))
        fakeWiki.moveListResults = mapOf(MoveSource.character.id to Result.Success(MoveSource.all))
    }

    @Test
    fun `invoke returns GameNotFound when gameId is invalid`() = runTest {
        // given
        val gameId = "not_a_game"
        val expected = Result.Error(AppError.GameNotFound(gameId))

        // when
        val result = usecase.invoke(gameId)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `invoke returns WikiClientNotFound when repo has no wiki for game`() = runTest {
        // given
        val gameId = Game.Tekken8.id
        fakeRepo.wikiClient = null
        val expected = Result.Error(AppError.WikiClientNotFound(gameId))

        // when
        val result = usecase.invoke(gameId)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `invoke returns WikiError when fetchCharacterList fails`() = runTest {
        // given
        val gameId = Game.Tekken8.id
        fakeWiki.characterListResult = Result.Error(WikiError.DownloadError("boom"))
        val expected = Result.Error(AppError.WikiError("boom"))

        // when
        val result = usecase.invoke(gameId)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `invoke returns COUNT_QUESTIONS questions on happy path`() = runTest {
        // given
        val gameId = Game.Tekken8.id
        val expectedSize = COUNT_QUESTIONS

        // when
        val result = usecase.invoke(gameId) as Result.Success

        // then
        assertThat(result.data).hasSize(expectedSize)
    }

    @Test
    fun `invoke trims startup follow-ups on every option`() = runTest {
        // given
        val gameId = Game.Tekken8.id
        val expectedOptions = listOf(
            MoveSource.parenFollowUp.copy(startup = "i14"),
            MoveSource.commaFollowUp.copy(startup = "i20"),
            MoveSource.mixedFollowUp.copy(startup = "i10"),
            MoveSource.clean,
        )

        // when
        val result = usecase.invoke(gameId) as Result.Success
        val firstQuestionOptions = result.data.first().options

        // then
        assertThat(firstQuestionOptions).containsOnly(*expectedOptions.toTypedArray())
    }

    //region Fakes
    private class FakeCoreFeatureRepo : CoreFeatureRepo {
        var wikiClient: WikiClient? = null

        override fun getWikiClientFor(game: Game): WikiClient? {
            return wikiClient
        }

        override fun initialize(config: Config): EmptyResult<WikiError> =
            throw NotImplementedError("Not used in this use case")

        override fun getGameClients(): Map<Game, WikiClient> =
            throw NotImplementedError("Not used in this use case")

        override fun getOtherFeatures(): List<Config.Feature> =
            throw NotImplementedError("Not used in this use case")

        override fun getEnabledFeatureNames(): Set<String> =
            throw NotImplementedError("Not used in this use case")
    }

    private class FakeWikiClient(
        override val featureInfo: FeatureInfo = FeatureInfo(
            name = "fake",
            url = "",
            version = "",
            supportedGameSet = setOf(),
            iconUrl = "",
        )
    ) : WikiClient {
        var characterListResult: Result<List<Character>, WikiError> = Result.Success(emptyList())
        var moveListResults: Map<String, Result<List<Move>, WikiError>> = emptyMap()

        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
            return characterListResult
        }

        override suspend fun fetchMoveList(
            characterQuery: String,
            filter: Filter,
        ): Result<List<Move>, WikiError> {
            return moveListResults[characterQuery]
                ?: Result.Error(WikiError.UnknownCharacter(characterQuery))
        }

        override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> =
            throw NotImplementedError("Not used in this use case")

        override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> =
            throw NotImplementedError("Not used in this use case")

        override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> =
            throw NotImplementedError("Not used in this use case")

        override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> =
            throw NotImplementedError("Not used in this use case")

        override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> =
            throw NotImplementedError("Not used in this use case")

        override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> =
            throw NotImplementedError("Not used in this use case")

        override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> =
            throw NotImplementedError("Not used in this use case")

        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> =
            throw NotImplementedError("Not used in this use case")

        override suspend fun clearCache(): EmptyResult<WikiError> =
            throw NotImplementedError("Not used in this use case")

        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    }
    //endregion
}

private object MoveSource {
    val character = Character(
        id = "kazuya",
        displayName = "Kazuya",
        remoteQueryId = "Kazuya",
        wikiUrl = "",
    )

    private val urls = Move.Urls(wikiUrl = "")

    val parenFollowUp = Move(
        characterId = character.id,
        id = "ewgf",
        input = "fnddf+2",
        startup = "i14 (i17)",
        urls = urls,
    )
    val commaFollowUp = Move(
        characterId = character.id,
        id = "df3",
        input = "df+3",
        startup = "i20, i26",
        urls = urls,
    )
    val mixedFollowUp = Move(
        characterId = character.id,
        id = "hellsweep",
        input = "fnddf+4",
        startup = "i10 (i15, i13)",
        urls = urls,
    )
    val clean = Move(
        characterId = character.id,
        id = "df1",
        input = "df+1",
        startup = "i13",
        urls = urls,
    )

    val all = listOf(parenFollowUp, commaFollowUp, mixedFollowUp, clean)
}
