package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.Ban

internal interface BanPort {
    suspend fun ban(ban: Ban): EmptyResult<DataError>
    suspend fun unban(offenderId: String): EmptyResult<DataError>
    suspend fun getBan(offenderId: String): Result<Ban?, DataError>
}
