package io.github.sophon.wiki.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.WikiError

interface GetCharacterUseCase {
    suspend operator fun invoke(characterId: CharacterId): Result<Character, WikiError>
}
