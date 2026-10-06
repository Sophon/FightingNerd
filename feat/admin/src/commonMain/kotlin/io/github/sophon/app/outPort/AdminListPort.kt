package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.AdminError

internal interface AdminListPort {
    fun isAdmin(userId: String): Boolean
    fun save(adminIdList: List<String>): EmptyResult<AdminError>
}
