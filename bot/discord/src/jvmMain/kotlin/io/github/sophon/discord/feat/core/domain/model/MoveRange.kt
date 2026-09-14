package io.github.sophon.discord.feat.core.domain.model

import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move

data class MoveRange(
    val rangeType: Filter,
    val character: Character,
    val from: Int,
    val to: Int,
    val moveList: List<Move>,
) {
    val formattedMin: String get() {
        return if (from < -LIMIT_NUMBER) "-INF"
        else from.toString()
    }

    val formattedMax: String get() {
        return if (to > LIMIT_NUMBER) "INF"
        else to.toString()
    }
}

private const val LIMIT_NUMBER = 50