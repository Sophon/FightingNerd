package io.github.sophon.fightingnerd.feat.home.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class LoadGameCharacterListUseCaseTest {
    private val testCharacterList = listOf(
        Character(
            id = "kazuya",
            displayName = "Kazuya",
            remoteQueryId = "Kazuya",
            wikiUrl = "https://wavu.wiki/t/Kazuya",
        ),
    )
    private val widget = HomeViewState.GameWidget(
        game = Game.Tekken8,
        featureName = "featureName",
    )
    
    @Test
    fun `usecase downloads character list if not cached`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            fetchCharacterListResult = Result.Success(emptyList()),
        )
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wikiClient))
        val usecase = LoadGameCharacterListUseCase(repo)

        // when
        val result = usecase.invoke(gameWidget = widget)

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wikiClient.downloadCharacterListCalled).isTrue()
    }
    
    @Test
    fun `usecase does not download character list if cached`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            fetchCharacterListResult = Result.Success(testCharacterList),
        )
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wikiClient))
        val usecase = LoadGameCharacterListUseCase(repo)

        // when
        val result = usecase.invoke(gameWidget = widget)

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wikiClient.downloadCharacterListCalled).isFalse()
    }

    @Test
    fun `usecase returns error when game does not exist`() = runTest {
        // given
        val repo = FakeFeatureRepo(gameClients = emptyMap())
        val usecase = LoadGameCharacterListUseCase(repo)

        // when
        val result = usecase.invoke(gameWidget = widget)

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as? Result.Error)?.error).isEqualTo(AppError.WikiClientNotFound(widget.game.id))
    }
}
