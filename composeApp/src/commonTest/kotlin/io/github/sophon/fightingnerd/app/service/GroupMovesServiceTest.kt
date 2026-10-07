package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Bookmark
import io.github.sophon.fightingnerd.app.model.GroupedMoveList
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.outPort.MoveGroupPort
import kotlin.test.Test

internal class GroupMovesServiceTest {
    private val tekkenGroupIdList = listOf("Heat", "n", "f", "df", "d", "db", "b", "Motion input", "ZEN", "Other")

    private val oneTwo = jinMove(input = "1,2", groupId = "n")
    private val jab = jinMove(input = "1", groupId = "n")
    private val heatSmash = jinMove(input = "H.2+3", groupId = "Heat")
    private val forwardFour = jinMove(input = "f+4", groupId = "f")
    private val electric = jinMove(input = "f,n,d,d/f+2", groupId = "Motion input")
    private val zenOne = jinMove(input = "ZEN.1", groupId = "ZEN")
    private val taunt = jinMove(input = "b+1+2+3+4", groupId = "Other")

    @Test
    fun `moves are ordered by the game's groups`() {
        // given
        val service = GroupMovesService(FakeMoveGroupPort(Result.Success(tekkenGroupIdList)))
        val moveList = listOf(taunt, electric, oneTwo, zenOne, heatSmash, forwardFour, jab)
        val expected = listOf(heatSmash, oneTwo, jab, forwardFour, electric, zenOne, taunt)

        // when
        val result = service("Tekken_8", moveList)

        // then
        assertThat((result as? Result.Success)?.data?.moveList).isEqualTo(expected)
    }

    @Test
    fun `bookmarks point at the first move of every group with moves`() {
        // given
        val service = GroupMovesService(FakeMoveGroupPort(Result.Success(tekkenGroupIdList)))
        val moveList = listOf(taunt, oneTwo, heatSmash, jab, electric)
        val expected = listOf(
            Bookmark(id = "Heat", moveListIndex = 0),
            Bookmark(id = "n", moveListIndex = 1),
            Bookmark(id = "Motion input", moveListIndex = 3),
            Bookmark(id = "Other", moveListIndex = 4),
        )

        // when
        val result = service("Tekken_8", moveList)

        // then
        assertThat((result as? Result.Success)?.data?.bookmarkList).isEqualTo(expected)
    }

    @Test
    fun `moves outside of the game's groups come last without a bookmark`() {
        // given
        val service = GroupMovesService(FakeMoveGroupPort(Result.Success(listOf("Heat", "n"))))
        val moveList = listOf(forwardFour, jab, heatSmash)
        val expected = GroupedMoveList(
            moveList = listOf(heatSmash, jab, forwardFour),
            bookmarkList = listOf(
                Bookmark(id = "Heat", moveListIndex = 0),
                Bookmark(id = "n", moveListIndex = 1),
            ),
        )

        // when
        val result = service("Tekken_8", moveList)

        // then
        assertThat(result).isEqualTo(Result.Success(expected))
    }

    @Test
    fun `missing groups fail`() {
        // given
        val error = AppError.GameNotFound("Tekken_9")
        val service = GroupMovesService(FakeMoveGroupPort(Result.Error(error)))
        val expected = Result.Error(error)

        // when
        val result = service("Tekken_9", listOf(jab))

        // then
        assertThat(result).isEqualTo(expected)
    }


    private fun jinMove(input: String, groupId: String): Move {
        val move = Move(
            input = input,
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-$input"),
            groupId = groupId,
        )
        return move
    }

    private class FakeMoveGroupPort(
        private val groupIdListResult: Result<List<String>, AppError>,
    ): MoveGroupPort {
        override fun loadGroupIdList(gameId: String, moveList: List<Move>): Result<List<String>, AppError> {
            return groupIdListResult
        }
    }
}
