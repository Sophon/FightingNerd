package io.github.sophon.wikiwavu.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterListResponseDto(
    val characters: List<CharacterDto>,
)

@Serializable
internal data class CharacterDto(
    val id: String,
    val displayName: String,
    val wavuName: String,
    val aliasList: List<String> = listOf(),
    val images: Images? = null,
) {
    @Serializable
    data class Images(
        val largePng: String? = null,
        val officialLargePng: String? = null,
    )
}
