package io.github.sophon.usecase

import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.core.domain.Result
import io.github.sophon.integration.model.Battle
import io.github.sophon.integration.model.BattleSet
import io.github.sophon.integration.model.BattleType
import io.github.sophon.integration.model.EwgfError
import io.github.sophon.integration.model.Score
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.math.abs
import kotlin.time.ExperimentalTime

@ExcludeFromCoverage("to be implemented")
@OptIn(ExperimentalTime::class)
internal class GroupBySetUseCase {
    fun invoke(battleList: List<Battle>): Result<List<BattleSet>, EwgfError> {
        if (battleList.isEmpty()) return Result.Success(emptyList())

        val setList = mutableListOf<BattleSet>()

        var currentBattle: Battle? = null
        val currentSet = mutableListOf<Battle>()

        battleList.forEach { battle ->
            when {
                (currentBattle == null) -> {
                    currentBattle = battle
                    currentSet.add(battle)
                }
                (currentBattle.isSameSetAs(battle)) -> {
                    currentSet.add(battle)
                    currentBattle = battle
                }
                else -> {
                    val finishedSet = BattleSet(
                        battleList = currentSet.toList().reversed(),
                        player = currentBattle.player,
                        opponent = currentBattle.opponent,
                        score = currentSet.calculateScore(),
                        battleType = currentBattle.battleType,
                        date = currentBattle.date,
                        version = currentBattle.version,
                        stageId = currentBattle.stageId,
                    )
                    setList.add(finishedSet)

                    currentBattle = battle
                    currentSet.apply {
                        clear()
                        add(battle)
                    }
                }
            }
        }

        currentBattle?.let { last ->
            setList.add(
                BattleSet(
                    battleList = currentSet.toList(),
                    player = last.player,
                    opponent = last.opponent,
                    score = currentSet.calculateScore(),
                    battleType = last.battleType,
                    date = last.date,
                    version = last.version,
                    stageId = last.stageId,
                )
            )
        }

        return Result.Success(setList.toList())
    }


    private fun Battle.isSameSetAs(battle: Battle): Boolean {
        val isSimilarTime = abs(
            this.date.toInstant(TimeZone.UTC).epochSeconds - battle.date.toInstant(TimeZone.UTC).epochSeconds
        ) <= 300
        val isRankedSameMap = (battle.battleType != BattleType.RANKED)
                || this.stageId == battle.stageId

        return this.opponent.polarisId == battle.opponent.polarisId
                && this.opponent.character == battle.opponent.character
                && this.battleType == battle.battleType
                && this.version == battle.version
                && isRankedSameMap
                && isSimilarTime
    }

    private fun List<Battle>.calculateScore(): Score {
        return Score(
            player = count { it.score.outcome == Score.Outcome.WIN },
            opponent = count { it.score.outcome == Score.Outcome.LOSE },
        )
    }
}
