package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import kotlinx.serialization.Serializable

@Serializable
internal data class MizuumiCharacterJsonResponseDto(
    val characters: List<MizuumiCharacterJsonDto>,
)

@Serializable
internal data class MizuumiCharacterJsonDto(
    val id: String,
    val displayName: String,
    val aliasList: List<String> = listOf(),
    val images: Images? = null,
) {
    @Serializable
    data class Images(
        val largePng: String? = null,
        val officialLargePng: String? = null,
    )
}
