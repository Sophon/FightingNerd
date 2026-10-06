package io.github.sophon.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import io.github.sophon.model.BanRequest

interface BanUserUseCase {
    //upsert
    suspend operator fun invoke(banRequest: BanRequest): Result<Ban, AdminError>
}
