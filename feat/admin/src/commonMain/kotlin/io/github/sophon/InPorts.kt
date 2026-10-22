package io.github.sophon

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import io.github.sophon.model.BanRequest
import io.github.sophon.model.UnbanRequest

interface BanUserUseCase {
    //upsert
    suspend operator fun invoke(banRequest: BanRequest): Result<Ban, AdminError>
}

interface ConfigureAdminToolUseCase {
    operator fun invoke(adminIdList: List<String>): EmptyResult<AdminError>
}

interface IsUserAdminUseCase {
    operator fun invoke(userId: String): Result<Boolean, AdminError>
}

interface IsUserBannedUseCase {
    suspend operator fun invoke(userId: String): Result<Boolean, AdminError>
}

interface UnbanUserUseCase {
    suspend operator fun invoke(unbanRequest: UnbanRequest): EmptyResult<AdminError>
}
