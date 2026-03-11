package io.github.sophon.data.local

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.data.PlayerRepo
import io.github.sophon.domain.EwgfError
import io.github.sophon.domain.Player
import io.github.sophon.ewgf.data.EwgfDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class PlayerRepoImpl(
    driver: DatabaseDriverFactory,
): PlayerRepo {
    private val db = EwgfDatabase(driver.createDriver())
    private val queries = db.playerQueries

    override suspend fun getPlayer(discordId: String): Result<Player?, EwgfError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val player = queries.getPlayerByDiscordId(discordId)
                    .executeAsOneOrNull()
                    .toDomain()
                Result.Success(player)
            } catch (e: Exception) {
                Result.Error(EwgfError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun registerPlayer(player: Player): EmptyResult<EwgfError.DatabaseError> {
        if (player.discordId == null) {
            return Result.Error(EwgfError.DatabaseError("discord: null"))
        }

        return withContext(Dispatchers.IO) {
            try {
                queries.upsertPlayer(
                    discordId = player.discordId,
                    polarisId = player.polarisId,
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(EwgfError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                queries.delete(discordId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(EwgfError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun updatePolarisId(
        discordId: String,
        polarisId: String
    ): EmptyResult<EwgfError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                queries.updatePolarisId(discordId = discordId, polarisId = polarisId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(EwgfError.DatabaseError(e.toString()))
            }
        }
    }
}