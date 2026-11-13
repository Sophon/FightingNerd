package io.github.sophon.wiki.adapter.outbound.ktor.xko

import kotlinx.serialization.Serializable

@Serializable
internal data class XkoCharacterJsonResponseDto(
    val characters: List<XkoCharacterJsonDto>,
)

@Serializable
internal data class XkoCharacterJsonDto(
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
