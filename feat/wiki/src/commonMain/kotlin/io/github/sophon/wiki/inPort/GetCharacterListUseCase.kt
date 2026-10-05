package io.github.sophon.wiki.inPort

import io.github.sophon.wiki.model.Character
import kotlinx.coroutines.flow.Flow

interface GetCharacterListUseCase {
    operator fun invoke(): Flow<List<Character>>
}
