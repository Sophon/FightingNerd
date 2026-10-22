package io.github.sophon.discord.app.model

sealed interface EwgfOperation {
    data object Help: EwgfOperation
    data class Register(val polarisId: String): EwgfOperation
    data object Data: EwgfOperation
    data class Update(val polarisId: String): EwgfOperation
    data object Unregister: EwgfOperation
    data class Search(val discordId: String): EwgfOperation
}
