package io.github.sophon.wikimizuumi.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterDbAdapter
import io.github.sophon.core.wiki.data.MoveDbAdapter
import io.github.sophon.core.wiki.data.fromDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikimizuumi.data.MizuumiDB
import io.github.sophon.wikimizuumi.integration.model.Uni2Properties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class MizuumiCharacterDbAdapter(
    private val db: MizuumiDB,
    private val game: Game,
) : CharacterDbAdapter {
    private val queries = db.characterQueries
    private val uni2Queries = db.uni2CharacterQueries

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
            Game.Uni2 -> {
                val p = character.gameProperties as? Uni2Properties ?: return
                uni2Queries.insertUni2Character(
                    characterId = character.id,
                    smartSteer = p.smartSteer,
                    fWalkSpeed = p.fWalkSpeed,
                    fWalkSpeedNote = p.fWalkSpeedNote,
                    bWalkSpeed = p.bWalkSpeed,
                    bWalkSpeedNote = p.bWalkSpeedNote,
                    jumpStartup = p.jumpStartup,
                    jumpDuration = p.jumpDuration,
                    jumpDurationNote = p.jumpDurationNote,
                    dashStartup = p.dashStartup,
                    iDashSpeed = p.iDashSpeed,
                    iDashSpeedNote = p.iDashSpeedNote,
                    dashAccel = p.dashAccel,
                    dashAccelNote = p.dashAccelNote,
                    maxDashSpeed = p.maxDashSpeed,
                    bDashStartup = p.bDashStartup,
                    bDashDuration = p.bDashDuration,
                    bDashDurationNote = p.bDashDurationNote,
                    bDashDistance = p.bDashDistance,
                    bDashDistanceNote = p.bDashDistanceNote,
                    bDashFullInvulStart = p.bDashFullInvulStart,
                    bDashFullInvulEnd = p.bDashFullInvulEnd,
                    bDashThrowInvulStart = p.bDashThrowInvulStart,
                    bDashThrowInvulEnd = p.bDashThrowInvulEnd,
                    throwWidth = p.throwWidth,
                    throwRange = p.throwRange,
                    trait = p.trait,
                    vorpalTrait = p.vorpalTrait,
                )
            }
            else -> Unit
        }
    }

    override fun selectAllFlow(): Flow<List<Character>> {
        val flow = when (game) {
            Game.Uni2 -> queries.selectUni2ForGame(game.id)
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
internal class MizuumiMoveDbAdapter(
    private val db: MizuumiDB,
    private val game: Game,
) : MoveDbAdapter {
    private val queries = db.moveQueries
    private val mbtlQueries = db.mBTLMoveQueries
    private val uni2Queries = db.uni2MoveQueries
    private val vsavQueries = db.vSAVMoveQueries

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
            Game.MBTL -> {
                val p = move.mbProperties
                mbtlQueries.insertMBTLMove(
                    moveId = move.id,
                    inputInfo = p?.inputInfo,
                    subtitle = p?.subtitle,
                    minDamage = p?.minDamage,
                    property_ = p?.property,
                    cost = p?.cost,
                    attribute = p?.attribute,
                    landing = p?.landing,
                    overall = p?.overall,
                )
            }
            Game.Uni2 -> {
                val p = move.uni2Properties
                uni2Queries.insertUni2Move(
                    moveId = move.id,
                    inputInfo = p?.inputInfo,
                    subtitle = p?.subtitle,
                    minDamage = p?.minDamage,
                    cancelWindow = p?.cancelWindow,
                    property_ = p?.property,
                    cost = p?.cost,
                    attribute = p?.attribute,
                    landing = p?.landing,
                    overall = p?.overall,
                    assaultAdv = p?.assaultAdv,
                    blockstun = p?.blockstun,
                    groundHit = p?.groundHit,
                    airHit = p?.airHit,
                    groundCH = p?.groundCH,
                    airCH = p?.airCH,
                    hitstop = p?.hitstop,
                    CHstop = p?.CHstop,
                    proration = p?.proration,
                    comboP1 = p?.comboP1,
                    comboP2 = p?.comboP2,
                )
            }
            Game.VSAV -> {
                val p = move.vsavProperties
                vsavQueries.insertVSAVMove(
                    moveId = move.id,
                    inputInfo = p?.inputInfo,
                    subtitle = p?.subtitle,
                    whiteDmg = p?.whiteDmg,
                    renda = p?.renda,
                    meter = p?.meter,
                    reaction = p?.reaction,
                    curseTime = p?.curseTime,
                )
            }
            else -> error("${game.id} is not supported by Mizuumi MoveDbAdapter")
        }
    }

    override fun selectMovesFlow(characterId: String): Flow<List<Move>> {
        val flow = when (game) {
            Game.MBTL -> queries.selectMBTLByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.Uni2 -> queries.selectUni2ByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.VSAV -> queries.selectVSAVByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            else -> error("${game.id} is not supported by Mizuumi MoveDbAdapter")
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
