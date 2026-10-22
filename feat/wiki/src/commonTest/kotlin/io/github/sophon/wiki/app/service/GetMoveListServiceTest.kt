package io.github.sophon.wiki.app.service

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.app.outPort.LoadMoveListPort
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GetMoveListServiceTest {
    @Test
    fun `the stored move list of the character is returned`() = runTest {
        // given
        val service = GetMoveListService(
            FakeLoadMoveListPort(
                jinId to listOf(jinOneTwo),
                kazuyaId to listOf(kazuyaOneTwo),
            )
        )
        val expected = listOf(jinOneTwo)

        // when
        val result = service(jinId).first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a character without stored moves is an empty list`() = runTest {
        // given
        val service = GetMoveListService(FakeLoadMoveListPort(kazuyaId to listOf(kazuyaOneTwo)))

        // when
        val result = service(jinId).first()

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `a stored move list update re-emits`() = runTest {
        // given
        val moveListPort = FakeLoadMoveListPort(jinId to listOf(jinOneTwo))
        val service = GetMoveListService(moveListPort)
        val expected = listOf(jinOneTwo, jinOneTwoOne)

        service(jinId).test {
            awaitItem()

            // when
            moveListPort.store(jinId, listOf(jinOneTwo, jinOneTwoOne))
            val result = awaitItem()

            // then
            assertThat(result).isEqualTo(expected)
        }
    }
}


private val jinId = CharacterId(Game.Tekken8, "jin")

private val kazuyaId = CharacterId(Game.Tekken8, "kazuya")

private val jinOneTwo = Move(
    input = "1,2",
    name = "Jab, Cross Straight",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,2"),
)

private val jinOneTwoOne = Move(
    input = "1,2,1",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,2,1"),
)

private val kazuyaOneTwo = Move(
    input = "1,2",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-1,2"),
)

private class FakeLoadMoveListPort(vararg storedMoveLists: Pair<CharacterId, List<Move>>) : LoadMoveListPort {
    private val moveListByCharacter = storedMoveLists
        .associate { (characterId, moveList) -> characterId to MutableStateFlow(moveList) }
        .toMutableMap()

    fun store(
        characterId: CharacterId,
        moveList: List<Move>,
    ) {
        moveListByCharacter.getOrPut(characterId) { MutableStateFlow(emptyList()) }.value = moveList
    }

    override fun subscribe(characterId: CharacterId): Flow<List<Move>> {
        val moveListFlow = moveListByCharacter.getOrPut(characterId) { MutableStateFlow(emptyList()) }
        return moveListFlow
    }
}
