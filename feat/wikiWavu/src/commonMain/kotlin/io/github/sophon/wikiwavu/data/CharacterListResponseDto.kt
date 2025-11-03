package io.github.sophon.wikiwavu.data

import io.github.sophon.wikiwavu.domain.model.Character
import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterListResponseDto(
    val characters: List<Character>,
)
