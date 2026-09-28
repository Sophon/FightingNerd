package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.wiki.model.Character
import kotlinx.coroutines.flow.Flow

interface GetCharacterListUseCase {
    operator fun invoke(): Flow<List<Character>>
}
