package io.github.sophon.fightingnerd.feat.home.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class CheckCharacterHasMovesUseCaseTest {
    private val game = Game.Tekken8
    private val characterId = "kazuya"
    private val moves = listOf(
        Move(characterId = characterId, id = "1", input = "1", urls = Move.Urls(wikiUrl = "")),
    )

    @Test
    fun `usecase emits empty flow when no wiki client is registered for the game`() = runTest {
        // given
        val usecase = CheckCharacterHasMovesUseCase(
            featureRepo = FakeFeatureRepo(gameClients = emptyMap())
        )
        val expected = emptyList<Boolean>()

        // when
        val emissions = usecase.invoke(game, characterId).toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `usecase emits true when the move list is not empty`() = runTest {
        // given
        val wikiClient = FakeWikiClient(subscribeToMoveListResult = moves)
        val usecase = CheckCharacterHasMovesUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient))
        )
        val expected = true

        // when
        val result = usecase.invoke(game, characterId).first()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `usecase emits false when the move list is empty`() = runTest {
        // given
        val wikiClient = FakeWikiClient(subscribeToMoveListResult = emptyList())
        val usecase = CheckCharacterHasMovesUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient))
        )
        val expected = false

        // when
        val result = usecase.invoke(game, characterId).first()

        //then
        assertThat(result).isEqualTo(expected)
    }
}
