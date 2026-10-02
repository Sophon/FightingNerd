package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.WikiError

interface GetMoveUseCase {
    suspend operator fun invoke(characterId: CharacterId, input: String): Result<Move, WikiError>
}
