package io.github.sophon.data

import io.github.sophon.domain.AdminError
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.model.Ban
import kotlin.time.Duration

internal interface BanRepo {
    suspend fun ban(
        offenderId: String,
        duration: Duration,
        authorId: String,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError.DatabaseError>

    suspend fun getBanStatus(offenderId: String): Result<Ban?, AdminError.DatabaseError>

    suspend fun getBanList(): Result<List<Ban>, AdminError.DatabaseError>

    suspend fun unban(offenderId: String): EmptyResult<AdminError.DatabaseError>

    suspend fun updatePenalty(
        offenderId: String,
        duration: Duration,
        authorId: String,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError.DatabaseError>

    suspend fun cleanExpiredBans(): EmptyResult<AdminError.DatabaseError>
}
