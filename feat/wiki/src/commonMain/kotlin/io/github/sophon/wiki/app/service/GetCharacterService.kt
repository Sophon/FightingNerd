package io.github.sophon.wiki.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.app.outPort.LoadCharacterPort
import io.github.sophon.wiki.inPort.GetCharacterUseCase
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.WikiError

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
