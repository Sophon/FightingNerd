package io.github.sophon.wikiSuperCombo.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterDbAdapter
import io.github.sophon.core.wiki.data.MoveDbAdapter
import io.github.sophon.core.wiki.data.fromDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiSuperCombo.data.SuperComboDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class SuperComboCharacterDbAdapter(
    private val db: SuperComboDB,
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

    override fun getLastUpdateTimestamp(): Instant? {
        val millis = queries.selectLastInsertedAtForGame(gameId).executeAsOne().lastInsertedAt
        val timestamp = millis?.let { Instant.fromEpochMilliseconds(it) }
        return timestamp
    }
}

@OptIn(ExperimentalTime::class)
internal class SuperComboMoveDbAdapter(
    private val db: SuperComboDB,
    private val game: Game,
) : MoveDbAdapter {
    private val queries = db.moveQueries
    private val sf6Queries = db.sF6MoveQueries
    private val mk1Queries = db.mK1MoveQueries
    private val avlQueries = db.aVLMoveQueries

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
            Game.StreetFighter6 -> {
                val p = move.sf6Properties
                sf6Queries.insertSF6Move(
                    moveId = move.id,
                    type = p?.type?.name,
                    images = p?.images?.fromDomain(),
                    chip = p?.chip,
                    dmgScaling = p?.dmgScaling,
                    total = p?.total,
                    hitConfirm = p?.hitConfirm,
                    punishAdv = p?.punishAdv,
                    perfParryAdv = p?.perfParryAdv,
                    DRcOH = p?.DRcOH,
                    DRcOB = p?.DRcOB,
                    DROH = p?.DROH,
                    DROB = p?.DROB,
                    hitStun = p?.hitStun,
                    blockStun = p?.blockStun,
                    hitStop = p?.hitStop,
                    driveDmgOnBlock = p?.driveDmgOnBlock,
                    driveDmgOnHit = p?.driveDmgOnHit,
                    driveGain = p?.driveGain,
                    superGainOnHit = p?.superGainOnHit,
                    superGainOnBlock = p?.superGainOnBlock,
                    armor = p?.armor,
                    airborne = p?.airborne,
                    jugStart = p?.jugStart,
                    jugIncrease = p?.jugIncrease,
                    jugLimit = p?.jugLimit,
                    projectileSpeed = p?.projectileSpeed,
                    attackRange = p?.attackRange,
                )
            }
            Game.MK1 -> {
                val p = move.mkProperties
                mk1Queries.insertMK1Move(
                    moveId = move.id,
                    moveType = p?.moveType,
                    cost = p?.cost.orEmpty().fromDomain(),
                    chip = p?.chip,
                    flawlessBlockAdv = p?.flawlessBlockAdv,
                    hitCancelAdv = p?.hitCancelAdv,
                    blockCancelAdv = p?.blockCancelAdv,
                    punish = p?.punish,
                )
            }
            Game.AVL -> {
                val p = move.avlProperties
                avlQueries.insertAVLMove(
                    moveId = move.id,
                    chiDamage = p?.chiDamage,
                    flow = p?.flow,
                )
            }
            else -> error("${game.id} is not supported by SuperCombo MoveDbAdapter")
        }
    }

    override fun selectMovesFlow(characterId: String): Flow<List<Move>> {
        val flow = when (game) {
            Game.StreetFighter6 -> queries.selectSF6ByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.MK1 -> queries.selectMK1ByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            Game.AVL -> queries.selectAVLByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            else -> error("${game.id} is not supported by SuperCombo MoveDbAdapter")
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
