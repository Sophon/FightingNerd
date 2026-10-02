package io.github.sophon.wiki.application.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.port.inbound.GetMoveUseCase
import io.github.sophon.wiki.application.port.outbound.LoadMovePort

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
