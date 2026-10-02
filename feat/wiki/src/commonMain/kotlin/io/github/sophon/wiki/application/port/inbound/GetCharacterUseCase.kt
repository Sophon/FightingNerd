package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.WikiError

interface GetCharacterUseCase {
    suspend operator fun invoke(characterId: CharacterId): Result<Character, WikiError>
}
