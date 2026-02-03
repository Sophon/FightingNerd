package ewgf.data

import ewgf.domain.EwgfError
import ewgf.domain.Player
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result

interface PlayerRepo {
    suspend fun getPlayer(discordId: String): Result<Player, EwgfError>
    suspend fun registerPlayer(discordId: String, polarisId: String): EmptyResult<EwgfError>
    suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError>
    suspend fun updatePolarisId(polarisId: String): EmptyResult<EwgfError>
}