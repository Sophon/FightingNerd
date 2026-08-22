package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

internal class GroupMovesUseCase {
    fun invoke(moveList: List<Move>, groupList: List<Group>): List<Move> {
        val buckets = HashMap<Group, MutableList<Move>>()
        val other = mutableListOf<Move>()

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
            }
        }
        orderedMoveList.addAll(other)

        val result = orderedMoveList
        return result
    }
}
