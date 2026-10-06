package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.Battle

internal interface FetchBattleListPort {
    suspend fun fetch(polarisId: String): Result<List<Battle>, DataError.Remote>
}
