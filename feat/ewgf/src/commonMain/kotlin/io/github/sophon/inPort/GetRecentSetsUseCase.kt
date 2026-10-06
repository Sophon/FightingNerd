package io.github.sophon.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.model.BattleSet
import io.github.sophon.model.EwgfError

interface GetRecentSetsUseCase {
    suspend operator fun invoke(discordId: String): Result<List<BattleSet>, EwgfError>
}
