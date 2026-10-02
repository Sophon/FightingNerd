package io.github.sophon.wiki.application.domain.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.port.outbound.LoadMovePort
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GetMoveServiceTest {
    @Test
    fun `a stored move is returned`() = runTest {
        // given
        val service = GetMoveService(FakeLoadMovePort(jinId to oneTwo))
        val expected = Result.Success(oneTwo)

        // when
        val result = service(jinId, oneTwo.input)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a missing move is an unknown move`() = runTest {
        // given
        val service = GetMoveService(FakeLoadMovePort())
        val expected = listOf("jin", "1,2")

        // when
        val result = service(jinId, oneTwo.input)

        // then
        assertThat(result)
            .isInstanceOf(Result.Error::class)
            .prop(Result.Error<*>::error)
            .isInstanceOf(WikiError.UnknownMove::class)
            .transform { error -> error.inputs.toList() }
            .isEqualTo(expected)
    }
}


private val jinId = CharacterId(Game.Tekken8, "jin")

private val oneTwo = Move(
    input = "1,2",
    name = "Jab, Cross Straight",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,2"),
)

private class FakeLoadMovePort(vararg storedMoves: Pair<CharacterId, Move>) : LoadMovePort {
    private val storedMoveList = storedMoves.toList()

    override suspend fun get(characterId: CharacterId, input: String): Move? {
        val move = storedMoveList
            .firstOrNull { (storedId, storedMove) -> (storedId == characterId) && (storedMove.input == input) }
            ?.second
        return move
    }
}
