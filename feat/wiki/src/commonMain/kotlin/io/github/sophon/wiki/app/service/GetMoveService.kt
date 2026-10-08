package io.github.sophon.wiki.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.GetMoveUseCase
import io.github.sophon.wiki.app.outPort.LoadMovePort
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.WikiError

internal class GetMoveService(
    private val loadMovePort: LoadMovePort,
) : GetMoveUseCase {
    override suspend fun invoke(characterId: CharacterId, input: String): Result<Move, WikiError> {
        val move = loadMovePort.get(characterId, input)
        val result = if (move == null) {
            Result.Error(WikiError.UnknownMove(characterId.naturalId, input))
        } else {
            Result.Success(move)
        }
        return result
    }
}
