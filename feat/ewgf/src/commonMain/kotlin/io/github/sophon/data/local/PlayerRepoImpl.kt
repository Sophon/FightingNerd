package io.github.sophon.data.local

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.integration.data.PlayerRepo
import io.github.sophon.ewgf.data.EwgfDatabase
import io.github.sophon.integration.model.EwgfError
import io.github.sophon.integration.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PlayerRepoImpl(
    driver: DatabaseDriverFactory,
): PlayerRepo {
    private val db = EwgfDatabase(driver.createDriver())
    private val queries = db.playerQueries

    override suspend fun getPlayer(discordId: String): Result<Player?, EwgfError.DatabaseError> {
        val result = withContext(Dispatchers.IO) {
            try {
                val player = queries.getPlayerByDiscordId(discordId)
                    .executeAsOneOrNull()
                    .toDomain()
                Result.Success(player)
            } catch (e: Exception) {
                Result.Error(EwgfError.DatabaseError(e.toString()))
            }
        }
        return result
    }

    override suspend fun registerPlayer(player: Player): EmptyResult<EwgfError.DatabaseError> {
        if (player.discordId == null) {
            return Result.Error(EwgfError.DatabaseError("discord: null"))
        }

        val result = withContext(Dispatchers.IO) {
            try {
                queries.upsertPlayer(
                    discordId = player.discordId,
                    polarisId = player.polarisId,
                    name = player.name,
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(EwgfError.DatabaseError(e.toString()))
            }
        }
        return result
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
        val result = withContext(Dispatchers.IO) {
            try {
                queries.updatePolarisId(discordId = discordId, polarisId = polarisId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(EwgfError.DatabaseError(e.toString()))
            }
        }
        return result
    }
}