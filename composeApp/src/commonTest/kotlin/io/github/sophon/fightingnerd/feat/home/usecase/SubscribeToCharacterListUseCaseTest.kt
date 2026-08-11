package io.github.sophon.fightingnerd.feat.home.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeToCharacterListUseCaseTest {
    val game = Game.Tekken8

    @Test
    fun `usecase refreshes data when character list empty`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            subscribeToCharacterListResult = emptyList(),
        )
        val expected = true
        val usecase = SubscribeToCharacterListUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient))
        )

        // when
        usecase.invoke(game).toList()
        val isCalled = wikiClient.refreshCalled

        //then
        assertThat(isCalled).isEqualTo(expected)
    }

    @Test
    fun `usecase does not refresh data when character list is not empty`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            subscribeToCharacterListResult = listOf(character("kazuya")),
        )
        val expected = false
        val usecase = SubscribeToCharacterListUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient))
        )

        // when
        usecase.invoke(game).toList()
        val isCalled = wikiClient.refreshCalled

        //then
        assertThat(isCalled).isEqualTo(expected)
    }

    @Test
    fun `usecase emits empty flow when no wiki client is registered for the game`() = runTest {
        // given
        val expected = emptyList<List<Character>>()
        val usecase = SubscribeToCharacterListUseCase(
            featureRepo = FakeFeatureRepo(gameClients = emptyMap())
        )

        // when
        val emissions = usecase.invoke(game).toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `usecase emits the characters returned by the wiki client`() = runTest {
        // given
        val characters = listOf(
            character("kazuya"),
            character("jin"),
        )
        val wikiClient = FakeWikiClient(
            subscribeToCharacterListResult = characters,
        )
        val expected = listOf(characters)
        val usecase = SubscribeToCharacterListUseCase(
            featureRepo = FakeFeatureRepo(mapOf(game to wikiClient))
        )

        // when
        val result = usecase.invoke(game).toList()

        //then
        assertThat(result).isEqualTo(expected)
    }

    private fun character(id: String) = Character(
        id = id,
        displayName = id,
        remoteQueryId = id,
        wikiUrl = "",
    )
}
