package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.move.model.Bookmark

internal class GroupMovesUseCase {
    fun invoke(
        moveList: List<Move>,
        groupList: List<Group>,
    ): Pair<List<Move>, List<Bookmark>> {
        val buckets = HashMap<Group, MutableList<Move>>()
        val other = mutableListOf<Move>()
        val bookmarkList = mutableListOf<Bookmark>()
        var nextStartingIndex = 0

        moveList.forEach { move ->
            val group = groupList.firstOrNull { group -> group.predicate(move) }
            if (group == null) {
                other.add(move)
            } else {
                buckets
                    .getOrPut(key = group, defaultValue = { mutableListOf() })
                    .add(move)
            }
        }

        val orderedMoveList = ArrayList<Move>(moveList.size)
        groupList.forEach { group ->
            val bucket = buckets[group]
            if (bucket != null) {
                orderedMoveList.addAll(bucket)
                bookmarkList.add(Bookmark(id = group.id, moveListIndex = nextStartingIndex))
                nextStartingIndex += bucket.size
            }
        }
        orderedMoveList.addAll(other)

        return orderedMoveList to bookmarkList
    }
}
