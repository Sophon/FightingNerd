package config

import kotlinx.serialization.Serializable

//TODO: ConfigRepo
@Serializable
data class DcConfig(
    val heihachiRebornApiKey: String
)