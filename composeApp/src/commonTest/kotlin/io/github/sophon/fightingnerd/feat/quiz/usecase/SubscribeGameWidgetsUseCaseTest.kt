package io.github.sophon.fightingnerd.feat.quiz.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import io.github.sophon.fightingnerd.feat.quiz.model.QuizGameWidget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeGameWidgetsUseCaseTest {

    @Test
    fun `usecase emits empty list when feature repo has no game clients`() = runTest {
        // given
        val usecase = SubscribeGameWidgetsUseCase(
            store = fakeStore(),
            featureRepo = FakeFeatureRepo(gameClients = emptyMap()),
        )
        val expected = emptyList<QuizGameWidget>()

        // when
        val emission = usecase().first()

        // then
        assertThat(emission).isEqualTo(expected)
    }

    @Test
    fun `usecase emits empty list when no clients are enabled in preferences`() = runTest {
        // given
        val client = FakeWikiClient(name = "Wavu Wiki")
        val usecase = SubscribeGameWidgetsUseCase(
            store = fakeStore(),
            featureRepo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to client)),
        )
        val expected = emptyList<QuizGameWidget>()

        // when
        val emission = usecase().first()

        // then
        assertThat(emission).isEqualTo(expected)
    }

    @Test
    fun `usecase filters out clients whose preference is explicitly false`() = runTest {
        // given
        val client = FakeWikiClient(name = "Wavu Wiki")
        val usecase = SubscribeGameWidgetsUseCase(
            store = fakeStore(featureKey("Wavu Wiki", Game.Tekken8.id) to false),
            featureRepo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to client)),
        )
        val expected = emptyList<QuizGameWidget>()

        // when
        val emission = usecase().first()

        // then
        assertThat(emission).isEqualTo(expected)
    }

    @Test
    fun `usecase emits a ready widget for an enabled client with characters`() = runTest {
        // given
        val client = FakeWikiClient(
            name = "Wavu Wiki",
            subscribeToCharacterListResult = listOf(character("kazuya")),
        )
        val usecase = SubscribeGameWidgetsUseCase(
            store = fakeStore(featureKey("Wavu Wiki", Game.Tekken8.id) to true),
            featureRepo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to client)),
        )
        val expected = listOf(
            QuizGameWidget(game = Game.Tekken8, featureName = "Wavu Wiki", isReady = true),
        )

        // when
        val emission = usecase().first()

        // then
        assertThat(emission).isEqualTo(expected)
    }

    @Test
    fun `usecase emits a not-ready widget for an enabled client without characters`() = runTest {
        // given
        val client = FakeWikiClient(
            name = "Wavu Wiki",
            subscribeToCharacterListResult = emptyList(),
        )
        val usecase = SubscribeGameWidgetsUseCase(
            store = fakeStore(featureKey("Wavu Wiki", Game.Tekken8.id) to true),
            featureRepo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to client)),
        )
        val expected = listOf(
            QuizGameWidget(game = Game.Tekken8, featureName = "Wavu Wiki", isReady = false),
        )

        // when
        val emission = usecase().first()

        // then
        assertThat(emission).isEqualTo(expected)
    }

    @Test
    fun `usecase emits only widgets for enabled clients when others are disabled`() = runTest {
        // given
        val enabledClient = FakeWikiClient(
            name = "Wavu Wiki",
            subscribeToCharacterListResult = listOf(character("kazuya")),
        )
        val disabledClient = FakeWikiClient(
            name = "SuperCombo",
            subscribeToCharacterListResult = listOf(character("ryu")),
        )
        val usecase = SubscribeGameWidgetsUseCase(
            store = fakeStore(featureKey("Wavu Wiki", Game.Tekken8.id) to true),
            featureRepo = FakeFeatureRepo(
                gameClients = mapOf(
                    Game.Tekken8 to enabledClient,
                    Game.StreetFighter6 to disabledClient,
                ),
            ),
        )
        val expected = listOf(
            QuizGameWidget(game = Game.Tekken8, featureName = "Wavu Wiki", isReady = true),
        )

        // when
        val emission = usecase().first()

        // then
        assertThat(emission).isEqualTo(expected)
    }

    @Test
    fun `usecase maps isReady per enabled client from its own character list`() = runTest {
        // given
        val readyClient = FakeWikiClient(
            name = "Wavu Wiki",
            subscribeToCharacterListResult = listOf(character("kazuya")),
        )
        val notReadyClient = FakeWikiClient(
            name = "SuperCombo",
            subscribeToCharacterListResult = emptyList(),
        )
        val usecase = SubscribeGameWidgetsUseCase(
            store = fakeStore(
                featureKey("Wavu Wiki", Game.Tekken8.id) to true,
                featureKey("SuperCombo", Game.StreetFighter6.id) to true,
            ),
            featureRepo = FakeFeatureRepo(
                gameClients = mapOf(
                    Game.Tekken8 to readyClient,
                    Game.StreetFighter6 to notReadyClient,
                ),
            ),
        )
        val expected = listOf(
            QuizGameWidget(game = Game.Tekken8, featureName = "Wavu Wiki", isReady = true),
            QuizGameWidget(game = Game.StreetFighter6, featureName = "SuperCombo", isReady = false),
        )

        // when
        val emission = usecase().first()

        // then
        assertThat(emission).isEqualTo(expected)
    }

    private fun character(id: String) = Character(
        id = id,
        displayName = id,
        remoteQueryId = id,
        wikiUrl = "",
    )


    private fun fakeStore(vararg pairs: Preferences.Pair<*>): DataStore<Preferences> {
        val state = MutableStateFlow(preferencesOf(*pairs))
        return object : DataStore<Preferences> {
            override val data: Flow<Preferences> = state
            override suspend fun updateData(
                transform: suspend (Preferences) -> Preferences,
            ): Preferences {
                state.update { current -> transform(current) }
                return state.value
            }
        }
    }
}
