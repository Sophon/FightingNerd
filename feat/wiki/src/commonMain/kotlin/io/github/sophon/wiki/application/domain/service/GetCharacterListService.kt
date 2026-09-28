package io.github.sophon.wiki.application.domain.service

import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wiki.application.port.inbound.GetCharacterListUseCase
import kotlinx.coroutines.flow.Flow

internal class GetCharacterListService : GetCharacterListUseCase {
    override fun invoke(): Flow<List<Character>> {
        TODO("Not yet implemented")
    }
}
