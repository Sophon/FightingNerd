package io.github.sophon.data

import io.github.sophon.admin.data.AdminDatabase
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.model.Ban
import io.github.sophon.util.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.time.Duration

internal class BanRepoImpl(
    driver: DatabaseDriverFactory,
): BanRepo {
    private val db = AdminDatabase(driver.createDriver())
    private val queries = db.banQueries

    override suspend fun ban(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now()
                val expiresAt = now.plus(duration)
                queries.upsertBan(
                    userId = userId,
                    bannedAt = now.toEpochMilliseconds(),
                    expiresAt = expiresAt.toEpochMilliseconds(),
                    preventBotUsage = preventBotUsage.toLong(),
                )
                Result.Success(
                    Ban(
                        userId = userId,
                        bannedAt = now,
                        expiresAt = expiresAt,
                        preventBotUsage = preventBotUsage,
                    )
                )
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun isBanned(userId: String): Result<Boolean, AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val isBanned = queries.isBanned(userId = userId, expiresAt = now).executeAsOne()
                Result.Success(isBanned)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun getBan(userId: String): Result<Ban?, AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val ban = queries.getBan(userId).executeAsOneOrNull()
                    .toDomain()
                Result.Success(ban)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun getBanList(): Result<List<Ban>, AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val banList = queries.getActiveBans(now)
                    .executeAsList()
                    .mapNotNull { it.toDomain() }
                Result.Success(banList)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun unbanUser(userId: String): EmptyResult<AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(queries.removeBan(userId))
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun updatePenalty(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): EmptyResult<AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now()
                val expiration = now.plus(duration).toEpochMilliseconds()
                queries.updatePenalty(
                    userId = userId,
                    expiresAt = expiration,
                    preventBotUsage = preventBotUsage.toLong(),
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun cleanExpiredBans(): EmptyResult<AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                queries.cleanExpiredBans(now)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }
}
