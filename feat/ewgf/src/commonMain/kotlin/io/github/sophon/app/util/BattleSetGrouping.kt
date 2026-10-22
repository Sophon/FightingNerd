package io.github.sophon.app.util

import io.github.sophon.model.Battle
import io.github.sophon.model.BattleSet
import io.github.sophon.model.BattleType
import io.github.sophon.model.Score
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.math.abs
import kotlin.time.ExperimentalTime

internal fun List<Battle>.groupIntoSets(): List<BattleSet> {
    if (isEmpty()) return emptyList()

    val setList = mutableListOf<BattleSet>()

    var currentBattle: Battle? = null
    val currentSet = mutableListOf<Battle>()

    forEach { battle ->
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
                battleList = currentSet.toList().reversed(),
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

    val battleSetList = setList.toList()
    return battleSetList
}


@OptIn(ExperimentalTime::class)
private fun Battle.isSameSetAs(battle: Battle): Boolean {
    val isSimilarTime = abs(
        this.date.toInstant(TimeZone.UTC).epochSeconds - battle.date.toInstant(TimeZone.UTC).epochSeconds
    ) <= SAME_SET_MAX_GAP_SECONDS
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
    val score = Score(
        player = count { it.score.outcome == Score.Outcome.WIN },
        opponent = count { it.score.outcome == Score.Outcome.LOSE },
    )
    return score
}


private const val SAME_SET_MAX_GAP_SECONDS = 300
