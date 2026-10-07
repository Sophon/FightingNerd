package io.github.sophon.fightingnerd.app.service

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.wiki.model.Move
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class CheckCharacterHasMovesServiceTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wikiName = "Wavu Wiki",
    )
    private val jab = Move(input = "1", urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1"))
    private val oneTwo = Move(input = "1,2", urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,2"))
    private val electric = Move(input = "f,n,d,d/f+2", urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-f,n,d,df+2"))

    @Test
    fun `character without moves has no moves`() = runTest {
        // given
        val service = CheckCharacterHasMovesService(FakeMovePort(moveList = emptyList()))
        val expected = false

        // when
        val result = service(tekken8, "jin").first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `character with moves has moves`() = runTest {
        // given
        val service = CheckCharacterHasMovesService(FakeMovePort(moveList = listOf(jab, oneTwo)))
        val expected = true

        // when
        val result = service(tekken8, "jin").first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `downloaded moves are emitted`() = runTest {
        // given
        val movePort = FakeMovePort(moveList = emptyList())
        val service = CheckCharacterHasMovesService(movePort)
        val expected = true

        service(tekken8, "jin").test {
            awaitItem()

            // when
            movePort.moveList.value = listOf(jab, oneTwo)

            // then
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `move list changes don't re-emit`() = runTest {
        // given
        val movePort = FakeMovePort(moveList = listOf(jab, oneTwo))
        val service = CheckCharacterHasMovesService(movePort)

        service(tekken8, "jin").test {
            awaitItem()

            // when
            movePort.moveList.value = listOf(jab, oneTwo, electric)

            // then
            expectNoEvents()
        }
    }


    private class FakeMovePort(moveList: List<Move>): MovePort {
        val moveList = MutableStateFlow(moveList)

        override fun subscribeToMoves(game: Game, characterId: String): Flow<List<Move>> {
            return moveList
        }
    }
}
