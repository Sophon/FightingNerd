package io.github.sophon.adapter.outbound.sqldelight

import io.github.sophon.app.domain.model.Ban
import io.github.sophon.app.domain.model.ModerationRequest
import io.github.sophon.app.port.outbound.BanPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result

internal class SqlDelightAdapter: BanPort {
    override suspend fun ban(moderationRequest: ModerationRequest): Result<Ban, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun unban(moderationRequest: ModerationRequest): Result<Ban, DataError> {
        TODO("Not yet implemented")
    }
}
