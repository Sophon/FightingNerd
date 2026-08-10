package io.github.sophon.wikidustloop.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterDbAdapter
import io.github.sophon.core.wiki.data.MoveDbAdapter
import io.github.sophon.core.wiki.data.fromDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikidustloop.data.DustLoopDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DustLoopCharacterDbAdapter(
    private val db: DustLoopDB,
    private val gameId: String,
) : CharacterDbAdapter {
    private val queries = db.characterQueries

    override fun insert(character: Character) {
        queries.insertCharacter(
            id = character.id,
            gameId = gameId,
            remoteQueryId = character.remoteQueryId,
            wikiUrl = character.wikiUrl,
            displayName = character.displayName,
            aliases = character.aliasList.fromDomain(),
            hp = character.hp,
            umo = character.umo.fromDomain(),
            imagesIconUrl = character.images?.iconUrl,
            imagesBannerUrl = character.images?.bannerUrl,
        )
    }

    override fun selectAllFlow(): Flow<List<Character>> {
        val flow = queries.selectAllForGame(gameId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                val characters = rows.map { it.toDomain() }
                characters
            }
        return flow
    }

    override fun incrementFailureCountForAbsent(remoteIds: List<String>) {
        queries.incrementFailureCountForAbsentInGame(gameId, remoteIds)
    }

    override fun deleteExceededThreshold(threshold: Long) {
        queries.deleteExceededThresholdForGame(gameId, threshold)
    }

    override fun deleteAll() {
        queries.deleteAllForGame(gameId)
    }

    override fun transaction(block: () -> Unit) {
        db.transaction { block() }
    }
}

internal class DustLoopMoveDbAdapter(
    private val db: DustLoopDB,
    private val game: Game,
) : MoveDbAdapter {
    private val queries = db.moveQueries
    private val ggstQueries = db.gGSTMoveQueries
    private val dbfzQueries = db.dBFZMoveQueries
    private val gbvsrQueries = db.gBVSRMoveQueries
    private val bbcfQueries = db.bBCFMoveQueries
    private val mtfsQueries = db.mTFSMoveQueries

    override fun insert(move: Move) {
        queries.insertMove(
            id = move.id,
            characterId = move.characterId,
            gameId = game.id,
            name = move.name,
            input = move.input,
            damage = move.damage,
            startup = move.startup,
            active = move.active,
            recovery = move.recovery,
            onBlock = move.onBlock,
            onHit = move.onHit,
            onCH = move.onCH,
            guard = move.guard,
            cancel = move.cancel,
            invulnerability = move.invulnerability,
            isThrow = move.isThrow,
            notes = move.notes.fromDomain(),
            aliases = move.aliases.fromDomain(),
            urlsWikiUrl = move.urls.wikiUrl,
            urlsVideoId = move.urls.videoId,
            urlsHitboxImageList = move.urls.hitboxImageList.fromDomain(),
            urlsMoveImageList = move.urls.moveImageList.fromDomain(),
        )
        insertProperties(move)
    }

    private fun insertProperties(move: Move) {
        when (game) {
            Game.GGST -> {
                val p = move.ggstProperties
                ggstQueries.insertGGSTMove(
                    moveId = move.id,
                    type = p?.type,
                    riscGain = p?.riscGain,
                    riscLoss = p?.riscLoss,
                    wallDamage = p?.wallDamage,
                    inputTension = p?.inputTension,
                    chipRatio = p?.chipRatio,
                    otgType = p?.otgType,
                    prorate = p?.prorate,
                    level = p?.level,
                )
            }
            Game.DBFZ -> {
                val p = move.dbfzProperties
                dbfzQueries.insertDBFZMove(
                    moveId = move.id,
                    attribute = p?.attribute,
                    smash = p?.smash,
                    kiGain = p?.kiGain,
                    prorate = p?.prorate,
                    blockStun = p?.blockStun,
                    groundHit = p?.groundHit,
                    airHit = p?.airHit,
                    type = p?.type,
                    level = p?.level,
                )
            }
            Game.GBVSR -> {
                val p = move.gbvsrProperties
                gbvsrQueries.insertGBVSRMove(
                    moveId = move.id,
                    meter = p?.meter,
                    level = p?.level,
                    cooldown = p?.cooldown,
                    cls = p?.cls,
                    type = p?.type,
                )
            }
            Game.BBCF -> {
                val p = move.bbProperties
                bbcfQueries.insertBBCFMove(
                    moveId = move.id,
                    onODR = p?.onODR,
                    attribute = p?.attribute,
                    p1 = p?.p1,
                    p2 = p?.p2,
                    starter = p?.starter,
                    level = p?.level,
                    blockstun = p?.blockstun,
                    groundHit = p?.groundHit,
                    airHit = p?.airHit,
                    groundCH = p?.groundCH,
                    airCH = p?.airCH,
                    blockstop = p?.blockstop,
                    hitstop = p?.hitstop,
                    chStop = p?.chStop,
                    cancelTiming = p?.cancelTiming,
                    type = p?.type,
                )
            }
            Game.MTFS -> {
                val p = move.mtfsProperties
                mtfsQueries.insertMTFSMove(
                    moveId = move.id,
                    simpleInput = p?.simpleInput,
                    type = p?.type,
                    level = p?.level,
                    prorate = p?.prorate,
                    meterGain = p?.meterGain,
                    untechAmount = p?.untechAmount,
                    hitboxCaption = p?.hitboxCaption,
                )
            }
            else -> error("${game.id} is not supported by DustLoop MoveDbAdapter")
        }
    }

    override fun selectMovesFlow(characterId: String): Flow<List<Move>> {
        val flow = when (game) {
            Game.GGST -> queries.selectGGSTByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.DBFZ -> queries.selectDBFZByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.GBVSR -> queries.selectGBVSRByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.BBCF -> queries.selectBBCFByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.MTFS -> queries.selectMTFSByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            else -> error("${game.id} is not supported by DustLoop MoveDbAdapter")
        }
        return flow
    }

    override fun incrementFailureCountForAbsent(characterId: String, remoteIds: List<String>) {
        queries.incrementFailureCountForAbsent(characterId, remoteIds)
    }

    override fun deleteExceededThreshold(characterId: String, threshold: Long) {
        queries.deleteExceededThreshold(characterId, threshold)
    }

    override fun deleteAll() {
        queries.deleteAllForGame(game.id)
    }

    override fun transaction(block: () -> Unit) {
        db.transaction { block() }
    }
}
