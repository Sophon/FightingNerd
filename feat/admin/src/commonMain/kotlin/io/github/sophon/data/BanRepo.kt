package io.github.sophon.data

import io.github.sophon.domain.AdminError
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.model.Ban
import kotlin.time.Duration

internal interface BanRepo {
    suspend fun ban(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError.DatabaseError>

    suspend fun isBanned(userId: String): Result<Boolean, AdminError.DatabaseError>

    suspend fun getBan(userId: String): Result<Ban?, AdminError.DatabaseError>

    suspend fun getBanList(): Result<List<Ban>, AdminError.DatabaseError>

    suspend fun unbanUser(userId: String): EmptyResult<AdminError.DatabaseError>

    suspend fun updatePenalty(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): EmptyResult<AdminError.DatabaseError>

    suspend fun cleanExpiredBans(): EmptyResult<AdminError.DatabaseError>
}
