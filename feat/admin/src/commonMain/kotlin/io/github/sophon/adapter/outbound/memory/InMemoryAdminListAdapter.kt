package io.github.sophon.adapter.outbound.memory

import io.github.sophon.app.outPort.AdminListPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.AdminError
import kotlinx.coroutines.flow.MutableStateFlow

internal class InMemoryAdminListAdapter : AdminListPort {
    private val adminIdList = MutableStateFlow<List<String>>(emptyList())

    override fun isAdmin(userId: String): Boolean {
        val isAdmin = adminIdList.value.contains(userId)
        return isAdmin
    }

    override fun save(adminIdList: List<String>): EmptyResult<AdminError> {
        this.adminIdList.value = adminIdList
        return Result.Success(Unit)
    }
}
