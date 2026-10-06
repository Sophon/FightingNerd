package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.outPort.FetchBattleListPort
import io.github.sophon.app.outPort.LoadPlayerPort
import io.github.sophon.app.util.groupIntoSets
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.inPort.GetRecentSetsUseCase
import io.github.sophon.model.Battle
import io.github.sophon.model.BattleSet
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player

internal class GetRecentSetsService(
    private val loadPlayerPort: LoadPlayerPort,
    private val fetchBattleListPort: FetchBattleListPort,
): GetRecentSetsUseCase {
    override suspend fun invoke(discordId: String): Result<List<BattleSet>, EwgfError> {
        val result = loadPlayerPort.get(discordId)
            .mapError { error -> EwgfError.Database(error) }
            .flatMap { player -> fetchBattleList(player) }
            .map { battleList -> battleList.groupIntoSets() }
            .onSuccess { setList ->
                val battleAmount = setList.sumOf { set -> set.battleList.size }
                Napier.d(tag = TAG) { "$discordId: ${setList.size} sets, $battleAmount battles" }
            }
            .onError { error -> Napier.e(tag = TAG) { "$discordId: $error" } }
        return result
    }

    private suspend fun fetchBattleList(
        player: Player?,
    ): Result<List<Battle>, EwgfError> {
        if (player == null) return Result.Error(EwgfError.PlayerNotRegistered)

        val battleListResult = fetchBattleListPort.fetch(player.polarisId)
            .mapError { error -> EwgfError.Download(error) }
        return battleListResult
    }


    private companion object {
        const val TAG = "GetRecentSetsService"
    }
}
