package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Bookmark
import io.github.sophon.fightingnerd.app.model.GroupedMoveList
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.outPort.MoveGroupPort
import io.github.sophon.fightingnerd.inPort.GroupMovesUseCase

internal class GroupMovesService(
    private val moveGroupPort: MoveGroupPort,
): GroupMovesUseCase {
    override fun invoke(
        gameId: String,
        moveList: List<Move>,
    ): Result<GroupedMoveList, AppError> {
        val result = moveGroupPort.loadGroupIdList(gameId, moveList)
            .map { groupIdList -> group(moveList, groupIdList) }
        return result
    }

    private fun group(
        moveList: List<Move>,
        groupIdList: List<String>,
    ): GroupedMoveList {
        val moveListByGroupId = moveList.groupBy { move -> move.groupId }
        val orderedMoveList = mutableListOf<Move>()
        val bookmarkList = mutableListOf<Bookmark>()

        groupIdList.forEach { groupId ->
            val groupMoveList = moveListByGroupId[groupId]
            if (groupMoveList != null) {
                bookmarkList.add(Bookmark(id = groupId, moveListIndex = orderedMoveList.size))
                orderedMoveList.addAll(groupMoveList)
            }
        }

        val groupIdSet = groupIdList.toSet()
        val ungroupedMoveList = moveList.filter { move -> move.groupId !in groupIdSet }
        orderedMoveList.addAll(ungroupedMoveList)

        val groupedMoveList = GroupedMoveList(
            moveList = orderedMoveList,
            bookmarkList = bookmarkList,
        )
        return groupedMoveList
    }
}
