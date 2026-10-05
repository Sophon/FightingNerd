package io.github.sophon.app.domain.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.domain.model.AdminError
import io.github.sophon.app.port.inbound.ConfigureAdminToolUseCase
import io.github.sophon.app.port.outbound.AdminListPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess

internal class ConfigureAdminToolService(
    private val adminListPort: AdminListPort,
): ConfigureAdminToolUseCase {
    override fun invoke(adminIdList: List<String>): EmptyResult<AdminError> {
        val result = adminListPort.save(adminIdList)
            .onSuccess { Napier.i(tag = TAG) { "${adminIdList.size} admins" } }
            .onError { error -> Napier.e(tag = TAG) { error.toString() } }
        return result
    }


    private companion object {
        const val TAG = "ConfigureAdminToolService"
    }
}
