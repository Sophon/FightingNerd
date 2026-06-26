package io.github.sophon.core.wiki.data

import kotlinx.serialization.Serializable

@Serializable
data class ImageUrlResponseDto(
    val batchcomplete: String? = null,
    val query: Query? = null,
    val error: ApiError? = null
) {
    @Serializable
    data class Query(
        val normalized: List<Normalized>? = null,
        val pages: Map<String, Page>
    )

    @Serializable
    data class Normalized(
        val from: String,
        val to: String
    )

    @Serializable
    data class Page(
        val title: String,
        val missing: String? = null,
        val imageinfo: List<ImageInfo>? = null
    )

    @Serializable
    data class ImageInfo(
        val url: String
    )

    @Serializable
    data class ApiError(
        val code: String,
        val info: String
    )
}