package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import kotlinx.serialization.Serializable

@Serializable
internal data class DragDownBucketResponseDto<T>(
    val bucketQuery: String,
    val bucket: List<T>,
)
