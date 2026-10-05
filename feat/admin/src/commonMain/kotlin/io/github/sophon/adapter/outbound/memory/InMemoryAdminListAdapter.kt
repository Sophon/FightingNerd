package io.github.sophon.adapter.outbound.memory

import io.github.sophon.app.domain.model.AdminError
import io.github.sophon.app.port.outbound.AdminListPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import kotlinx.coroutines.flow.MutableStateFlow

internal class InMemoryAdminListAdapter : AdminListPort {
    private val adminIdList = MutableStateFlow<List<String>>(emptyList())

    override fun load(): List<String> {
        return adminIdList.value
    }

    override fun save(adminIdList: List<String>): EmptyResult<AdminError> {
        this.adminIdList.value = adminIdList
        return Result.Success(Unit)
    }
}
