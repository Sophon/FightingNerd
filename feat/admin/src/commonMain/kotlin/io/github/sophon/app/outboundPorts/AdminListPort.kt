package io.github.sophon.app.outboundPorts

import io.github.sophon.model.AdminError
import io.github.sophon.core.architecture.EmptyResult

internal interface AdminListPort {
    fun isAdmin(userId: String): Boolean
    fun save(adminIdList: List<String>): EmptyResult<AdminError>
}
