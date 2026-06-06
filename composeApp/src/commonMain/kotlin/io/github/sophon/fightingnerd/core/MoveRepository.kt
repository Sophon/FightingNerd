package io.github.sophon.fightingnerd.core

import io.github.sophon.core.wiki.model.Move

/**
 * THIS IS STRICTLY TEMPORARY
 * this class exists purely to temporarily cache the move list while avoiding having to pass the movelist as parameter
 */
internal class MoveRepository {
    val moveList: MutableList<Move> = mutableListOf()
}
