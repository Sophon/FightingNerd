package io.github.sophon.data

import io.github.sophon.integration.model.EwgfError
import io.github.sophon.integration.model.Player
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result

interface PlayerRepo {
    suspend fun getPlayer(discordId: String): Result<Player?, EwgfError.DatabaseError>
    suspend fun registerPlayer(player: Player): EmptyResult<EwgfError.DatabaseError>
    suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError.DatabaseError>
    suspend fun updatePolarisId(discordId: String, polarisId: String): EmptyResult<EwgfError.DatabaseError>
}