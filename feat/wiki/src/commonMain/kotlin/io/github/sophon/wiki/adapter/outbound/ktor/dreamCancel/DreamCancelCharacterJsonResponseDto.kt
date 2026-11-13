package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import kotlinx.serialization.Serializable

@Serializable
internal data class DreamCancelCharacterJsonResponseDto(
    val characters: List<DreamCancelCharacterJsonDto>,
)

@Serializable
internal data class DreamCancelCharacterJsonDto(
    val id: String,
    val displayName: String,
    val aliasList: List<String> = listOf(),
    val images: Images? = null,
) {
    @Serializable
    data class Images(
        val portrait: String? = null,
        val full: String? = null,
    )
}
