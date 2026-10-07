package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeToCharactersServiceTest {
    @Test
    fun `characters of the game are emitted`() = runTest {
        // given
        val tekken8 = Game(
            id = "Tekken_8",
            displayName = "Tekken 8",
            iconUrl = "https://i.imgur.com/Yl6j809.png",
            wikiName = "Wavu Wiki",
        )
        val expected = listOf(
            Character(id = "jin", displayName = "Jin"),
            Character(id = "armor_king", displayName = "Armor King"),
        )
        val service = SubscribeToCharactersService(FakeCharacterPort(mapOf(tekken8.id to expected)))

        // when
        val result = service(tekken8).first()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeCharacterPort(
        private val characterListByGameId: Map<String, List<Character>>,
    ): CharacterPort {
        override fun subscribeToCharacters(gameId: String): Flow<List<Character>> {
            val flow = flowOf(characterListByGameId[gameId].orEmpty())
            return flow
        }
    }
}
