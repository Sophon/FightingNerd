package io.github.sophon

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.BattleSet
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player

interface GetRecentSetsUseCase {
    suspend operator fun invoke(discordId: String): Result<List<BattleSet>, EwgfError>
}

interface RegisterPlayerUseCase {
    //upsert
    suspend operator fun invoke(player: Player): EmptyResult<EwgfError>
}

interface UnregisterPlayerUseCase {
    suspend operator fun invoke(discordId: String): EmptyResult<EwgfError>
}

interface UpdatePolarisIdUseCase {
    suspend operator fun invoke(player: Player): EmptyResult<EwgfError>
}
