package io.github.sophon.xko.data.remote

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.wiki.data.CharacterRemoteAdapter
import io.github.sophon.core.wiki.data.MoveRemoteAdapter
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

internal class XkoCharacterRemoteAdapter(
    private val cache: XkoDataCache,
) : CharacterRemoteAdapter {

    override suspend fun download(): Result<List<Character>, DataError> {
        val result = cache.getOrFetch().map { map ->
            val characters = map.keys.toList()
            characters
        }
        return result
    }
}

internal class XkoMoveRemoteAdapter(
    private val cache: XkoDataCache,
) : MoveRemoteAdapter {

    override suspend fun download(character: Character): Result<List<Move>, DataError> {
        val result = cache.getOrFetch().map { map ->
            val moveList = map
                .filterKeys { it.remoteQueryId == character.remoteQueryId }
                .values
                .flatten()
            moveList
        }
        return result
    }
}
