package com.example.botdiscord.config

import kotlinx.serialization.Serializable

//TODO: ConfigRepo
@Serializable
data class DcConfig(
    val discordBotApiKey: String
)