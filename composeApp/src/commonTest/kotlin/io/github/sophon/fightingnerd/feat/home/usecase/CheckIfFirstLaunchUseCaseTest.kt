package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import assertk.assertThat
import assertk.assertions.isEqualTo
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
import io.github.sophon.fightingnerd.KEY_FIRST_TIME_HOME_INIT_DONE
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
internal class CheckIfFirstLaunchUseCaseTest {
    private val storePath = "check_first_launch_test_${Random.nextInt()}.preferences_pb".toPath()
    private val store = PreferenceDataStoreFactory.createWithPath(
        scope = TestScope(UnconfinedTestDispatcher()),
        produceFile = { storePath },
    )
    private val firstTimeFlagKey = booleanPreferencesKey(KEY_FIRST_TIME_HOME_INIT_DONE)

    @AfterTest
    fun cleanup() {
        FileSystem.SYSTEM.delete(storePath)
    }


    @Test
    fun `usecase enables only the defined games on first launch`() = runTest {
        // given
        val featureRepo = FakeFeatureRepo(
            gameClients = mapOf(
                Game.Tekken8 to FakeWikiClient("Wavu Wiki"),
                Game.StreetFighter6 to FakeWikiClient("SuperCombo Wiki"),
                Game.GGST to FakeWikiClient("DustLoop Wiki"),
                Game.MBTL to FakeWikiClient("Mizuumi Wiki"),
            ),
        )
        val usecase = CheckIfFirstLaunchUseCase(featureRepo, store)
        val expectedFirstLaunchFlag = true
        val expectedTekken8Setting = true
        val expectedStreetFighter6Setting = true
        val expectedGgstSetting = true
        val expectedMbtlSetting = false

        // when
        usecase.invoke()
        val snapshot = store.data.first()
        val firstLaunchFlag = snapshot[firstTimeFlagKey]
        val tekken8Setting = snapshot[featureKey("Wavu Wiki", Game.Tekken8.id)]
        val streetFighter6Setting = snapshot[featureKey("SuperCombo Wiki", Game.StreetFighter6.id)]
        val ggstSetting = snapshot[featureKey("DustLoop Wiki", Game.GGST.id)]
        val mbtlSetting = snapshot[featureKey("Mizuumi Wiki", Game.MBTL.id)]

        // then
        assertThat(firstLaunchFlag).isEqualTo(expectedFirstLaunchFlag)
        assertThat(tekken8Setting).isEqualTo(expectedTekken8Setting)
        assertThat(streetFighter6Setting).isEqualTo(expectedStreetFighter6Setting)
        assertThat(ggstSetting).isEqualTo(expectedGgstSetting)
        assertThat(mbtlSetting).isEqualTo(expectedMbtlSetting)
    }

    @Test
    fun `usecase keeps preferences unchanged on successive launch`() = runTest {
        // given
        val featureRepo = FakeFeatureRepo()
        val usecase = CheckIfFirstLaunchUseCase(featureRepo, store)
        store.edit { prefs -> prefs[firstTimeFlagKey] = true }
        val expected = store.data.first()

        // when
        usecase.invoke()
        val after = store.data.first()

        // then
        assertThat(after).isEqualTo(expected)
    }


    //region Test Doubles
    private class FakeFeatureRepo(
        private val gameClients: Map<Game, WikiClient> = emptyMap(),
    ) : FeatureRepo {
        override fun initialize(config: Config): EmptyResult<WikiError> = Result.Success(Unit)
        override fun getGameClients(): Map<Game, WikiClient> = gameClients
        override fun getOtherFeatures(): List<Config.Feature> = emptyList()
        override fun getEnabledFeatureNames(): Set<String> = emptySet()
        override fun getWikiClientFor(game: Game): WikiClient? = gameClients[game]
    }

    @OptIn(ExperimentalTime::class)
    private class FakeWikiClient(name: String) : WikiClient {
        override val featureInfo = FeatureInfo(name = name, url = "", version = "1.0.0")

        override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = error("not used")
        override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = error("not used")
        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = error("not used")
        override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> = error("not used")
        override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> = error("not used")
        override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = error("not used")
        override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> = error("not used")
        override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> = error("not used")
        override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> = error("not used")
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = error("not used")
        override suspend fun clearCache(): EmptyResult<WikiError> = error("not used")
        override fun getFiltersFor(game: Game): Set<Filter> = error("not used")
    }
    //endregion
}
