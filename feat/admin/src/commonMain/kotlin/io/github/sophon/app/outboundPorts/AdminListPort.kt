package io.github.sophon.app.outboundPorts

import io.github.sophon.app.model.AdminError
import io.github.sophon.core.architecture.EmptyResult

internal interface AdminListPort {
    fun load(): List<String>
    fun save(adminIdList: List<String>): EmptyResult<AdminError>
}
