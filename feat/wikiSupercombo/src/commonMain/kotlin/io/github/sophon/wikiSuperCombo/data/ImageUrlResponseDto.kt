package io.github.sophon.wikiSuperCombo.data

import kotlinx.serialization.Serializable

@Serializable
internal data class ImageUrlResponseDto(
    val query: Query
) {
    @Serializable
    data class Query(
        val pages: Map<String, Page>
    )

    @Serializable
    data class Page(
        val imageinfo: List<ImageInfo>? = null
    )

    @Serializable
    data class ImageInfo(
        val url: String
    )
}