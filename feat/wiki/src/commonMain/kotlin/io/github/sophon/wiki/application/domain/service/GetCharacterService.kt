package io.github.sophon.wiki.application.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.port.inbound.GetCharacterUseCase
import io.github.sophon.wiki.application.port.outbound.LoadCharacterPort

internal class GetCharacterService(
    private val loadCharacterPort: LoadCharacterPort,
) : GetCharacterUseCase {
    override suspend fun invoke(characterId: CharacterId): Result<Character, WikiError> {
        val character = loadCharacterPort.get(characterId)
        val result = if (character == null) {
            Result.Error(WikiError.UnknownCharacter(characterId.naturalId))
        } else {
            Result.Success(character)
        }
        return result
    }
}
