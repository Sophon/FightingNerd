package io.github.sophon.app.outboundPorts

import io.github.sophon.model.Ban
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult

internal interface BanPort {
    suspend fun ban(ban: Ban): EmptyResult<DataError>
    suspend fun unban(offenderId: String): EmptyResult<DataError>
}
