package io.github.sophon.inboundPorts

import io.github.sophon.app.model.AdminError
import io.github.sophon.app.model.Ban
import io.github.sophon.app.model.BanRequest
import io.github.sophon.core.architecture.Result

interface BanUserUseCase {
    //upsert
    suspend operator fun invoke(banRequest: BanRequest): Result<Ban, AdminError>
}
