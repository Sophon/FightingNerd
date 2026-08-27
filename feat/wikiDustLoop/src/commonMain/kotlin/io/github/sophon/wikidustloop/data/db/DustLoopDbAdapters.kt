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
import io.github.sophon.wikidustloop.integration.model.BBMoveProperties
import io.github.sophon.wikidustloop.integration.model.BBProperties
import io.github.sophon.wikidustloop.integration.model.DBFZMoveProperties
import io.github.sophon.wikidustloop.integration.model.DBFZProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRMoveProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRProperties
import io.github.sophon.wikidustloop.integration.model.GGSTMoveProperties
import io.github.sophon.wikidustloop.integration.model.GGSTProperties
import io.github.sophon.wikidustloop.integration.model.MTFSMoveProperties
import io.github.sophon.wikidustloop.integration.model.MTFSProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class DustLoopCharacterDbAdapter(
    private val db: DustLoopDB,
    private val game: Game,
) : CharacterDbAdapter {
    private val queries = db.characterQueries
    private val ggstQueries = db.gGSTCharacterQueries
    private val bbQueries = db.bBCharacterQueries
    private val mtfsQueries = db.mTFSCharacterQueries
    private val dbfzQueries = db.dBFZCharacterQueries
    private val gbvsrQueries = db.gBVSRCharacterQueries

    override fun insert(character: Character) {
        queries.insertCharacter(
            id = character.id,
            gameId = game.id,
            remoteQueryId = character.remoteQueryId,
            wikiUrl = character.wikiUrl,
            displayName = character.displayName,
            aliases = character.aliasList.fromDomain(),
            hp = character.hp,
            umo = character.umo.fromDomain(),
            imagesIconUrl = character.images?.iconUrl,
            imagesBannerUrl = character.images?.bannerUrl,
        )
        insertProperties(character)
    }

    private fun insertProperties(character: Character) {
        when (game) {
            Game.GGST -> {
                val p = character.gameProperties as? GGSTProperties ?: return
                ggstQueries.insertGGSTCharacter(
                    characterId = character.id,
                    defense = p.defense,
                    guts = p.guts,
                    guardBalance = p.guardBalance,
                    prejump = p.prejump,
                    bwdDash = p.bwdDash,
                    bwdDashDuration = p.bwdDashDuration,
                    bwdDashInvulnerability = p.bwdDashInvulnerability,
                    bwdDashAirborne = p.bwdDashAirborne,
                    bwdDashDist = p.bwdDashDist,
                    fwdDash = p.fwdDash,
                    jumpDuration = p.jumpDuration,
                    highJumpDuration = p.highJumpDuration,
                    jumpHeight = p.jumpHeight,
                    highJumpHeight = p.highJumpHeight,
                    earliestIAD = p.earliestIAD,
                    adDuration = p.adDuration,
                    abdDuration = p.abdDuration,
                    adDist = p.adDist,
                    abdDist = p.abdDist,
                    movementTension = p.movementTension,
                    jumpTension = p.jumpTension,
                    airDashTension = p.airDashTension,
                    walkSpd = p.walkSpd,
                    bwdWalkSpd = p.bwdWalkSpd,
                    dashInitialSpd = p.dashInitialSpd,
                    dashAcceleration = p.dashAcceleration,
                    dashFriction = p.dashFriction,
                    jumpGravity = p.jumpGravity,
                    highJumpGravity = p.highJumpGravity,
                    boostAttack = p.boostAttack,
                    boostDefense = p.boostDefense,
                )
            }
            Game.BBCF -> {
                val p = character.gameProperties as? BBProperties ?: return
                bbQueries.insertBBCharacter(
                    characterId = character.id,
                    preJump = p.preJump,
                    backDash = p.backDash,
                    forwardDash = p.forwardDash,
                )
            }
            Game.MTFS -> {
                val p = character.gameProperties as? MTFSProperties ?: return
                mtfsQueries.insertMTFSCharacter(
                    characterId = character.id,
                    prejump = p.prejump,
                    backdash = p.backdash,
                    team = p.team,
                )
            }
            Game.DBFZ -> {
                val p = character.gameProperties as? DBFZProperties ?: return
                dbfzQueries.insertDBFZCharacter(
                    characterId = character.id,
                    kiMod = p.kiMod,
                )
            }
            Game.GBVSR -> {
                val p = character.gameProperties as? GBVSRProperties ?: return
                gbvsrQueries.insertGBVSRCharacter(
                    characterId = character.id,
                    backdash = p.backdash,
                    walkSpeed = p.walkSpeed,
                    walkSpeedBack = p.walkSpeedBack,
                    dashInitial = p.dashInitial,
                    dashAcceleration = p.dashAcceleration,
                    jumpPre = p.jump?.pre,
                    jumpForwardDistance = p.jump?.forwardDistance,
                    jumpSuperForwardDistance = p.jump?.superForwardDistance,
                    jumpBackDistance = p.jump?.backDistance,
                    jumpSuperBackDistance = p.jump?.superBackDistance,
                    jumpGravity = p.jump?.gravity,
                    jumpSuperGravity = p.jump?.superGravity,
                    jumpSuperHeight = p.jump?.superHeight,
                    closeRangeL = p.closeRange?.l,
                    closeRangeM = p.closeRange?.m,
                    closeRangeH = p.closeRange?.h,
                )
            }
            else -> Unit
        }
    }

    override fun selectAllFlow(): Flow<List<Character>> {
        val flow = when (game) {
            Game.GGST -> queries.selectGGSTForGame(game.id)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.BBCF -> queries.selectBBForGame(game.id)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.MTFS -> queries.selectMTFSForGame(game.id)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.DBFZ -> queries.selectDBFZForGame(game.id)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.GBVSR -> queries.selectGBVSRForGame(game.id)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            else -> queries.selectAllForGame(game.id)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
        }
        return flow
    }

    override fun incrementFailureCountForAbsent(remoteIds: List<String>) {
        queries.incrementFailureCountForAbsentInGame(game.id, remoteIds)
    }

    override fun deleteExceededThreshold(threshold: Long) {
        queries.deleteExceededThresholdForGame(game.id, threshold)
    }

    override fun deleteAll() {
        queries.deleteAllForGame(game.id)
    }

    override fun transaction(block: () -> Unit) {
        db.transaction { block() }
    }

    override fun getLastUpdateTimestamp(): Instant? {
        val millis = queries.selectLastInsertedAtForGame(game.id).executeAsOne().lastInsertedAt
        val timestamp = millis?.let { Instant.fromEpochMilliseconds(it) }
        return timestamp
    }
}

@OptIn(ExperimentalTime::class)
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
            type = move.type,
            notes = move.notes.fromDomain(),
            aliases = move.aliases.fromDomain(),
            urlsWikiUrl = move.urls.wikiUrl,
            urlsVideoId = move.urls.videoId,
            urlsVideoUrl = move.urls.videoUrl,
            urlsHitboxImageList = move.urls.hitboxImageList.fromDomain(),
            urlsMoveImageList = move.urls.moveImageList.fromDomain(),
        )
        insertProperties(move)
    }

    private fun insertProperties(move: Move) {
        when (game) {
            Game.GGST -> {
                val p = move.gameProperties as? GGSTMoveProperties
                ggstQueries.insertGGSTMove(
                    moveId = move.id,
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
                val p = move.gameProperties as? DBFZMoveProperties
                dbfzQueries.insertDBFZMove(
                    moveId = move.id,
                    attribute = p?.attribute,
                    smash = p?.smash,
                    kiGain = p?.kiGain,
                    prorate = p?.prorate,
                    blockStun = p?.blockStun,
                    groundHit = p?.groundHit,
                    airHit = p?.airHit,
                    level = p?.level,
                )
            }
            Game.GBVSR -> {
                val p = move.gameProperties as? GBVSRMoveProperties
                gbvsrQueries.insertGBVSRMove(
                    moveId = move.id,
                    meter = p?.meter,
                    level = p?.level,
                    cooldown = p?.cooldown,
                    cls = p?.cls,
                )
            }
            Game.BBCF -> {
                val p = move.gameProperties as? BBMoveProperties
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
                )
            }
            Game.MTFS -> {
                val p = move.gameProperties as? MTFSMoveProperties
                mtfsQueries.insertMTFSMove(
                    moveId = move.id,
                    simpleInput = p?.simpleInput,
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

    override fun getLastUpdateTimestamp(): Instant? {
        val millis = queries.selectLastInsertedAtForGame(game.id).executeAsOne().lastInsertedAt
        val timestamp = millis?.let { Instant.fromEpochMilliseconds(it) }
        return timestamp
    }
}
