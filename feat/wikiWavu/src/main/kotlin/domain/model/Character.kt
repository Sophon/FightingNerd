package domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val name: String,
    @SerialName("portrait") val portraitUrl: String? = null,
    @SerialName("wavu_page") val wavuPageUrl: String? = null,
    val alias: List<String>,
)