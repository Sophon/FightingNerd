package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move

internal class FetchFastestNormalsUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        charName: String,
    ): Result<List<Move>, WikiError> {
        return db.fetchMoveListFor(charName)
            .map { moveList ->
                val normals = moveList.filter { move ->
                    move.input.length == 3 && move.input.first() in setOf('5', '2')
                }
                normals
                    .groupBy { it.startup?.toIntOrNull() }
                    .minByOrNull { it.key ?: Int.MAX_VALUE }
                    ?.value
                    ?: emptyList()
            }
    }
}