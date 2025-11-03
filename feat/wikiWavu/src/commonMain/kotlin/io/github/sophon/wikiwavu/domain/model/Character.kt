package io.github.sophon.wikiwavu.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: String,
    val displayName: String,
    @SerialName("wavuName") val wikiName: String,
    val aliasList: List<String> = listOf(),
    val image: Image? = null,
) {
    @Serializable
    data class Image(
        val url: String? = null,
        val officialUrl: String? = null,
    )
}