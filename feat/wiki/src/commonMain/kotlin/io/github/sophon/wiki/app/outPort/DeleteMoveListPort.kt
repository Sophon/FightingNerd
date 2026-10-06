package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.model.wiki.Game

internal interface DeleteMoveListPort {
    suspend fun delete(game: Game): EmptyResult<DataError.Local>
}
