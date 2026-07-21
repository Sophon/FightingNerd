package io.github.sophon.fightingnerd.core.data.db

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.db.move.MoveDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.time.Clock

internal class SqlMoveDB(
    db: MoveDatabase,
    private val gameId: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): MoveListDB {
    private val queries = db.moveEntityQueries

    override suspend fun fetchMoveListFor(characterId: String): Result<List<Move>, WikiError> {
        val result = dbCall {
            val all = when (gameId) {
                Game.Tekken8.id -> queries.selectAllT8().executeAsList().map { it.toDomain() }
                Game.StreetFighter6.id -> queries.selectAllSf6().executeAsList().map { it.toDomain() }
                Game.KoFXV.id -> queries.selectAllKof15().executeAsList().map { it.toDomain() }
                Game.COTW.id -> queries.selectAllCotw().executeAsList().map { it.toDomain() }
                Game.GGST.id -> queries.selectAllGgst().executeAsList().map { it.toDomain() }
                Game.DBFZ.id -> queries.selectAllDbfz().executeAsList().map { it.toDomain() }
                Game.GBVSR.id -> queries.selectAllGbvsr().executeAsList().map { it.toDomain() }
                Game.MK1.id -> queries.selectAllMk().executeAsList().map { it.toDomain() }
                Game.MBTL.id -> queries.selectAllMb().executeAsList().map { it.toDomain() }
                Game.BBCF.id -> queries.selectAllBb().executeAsList().map { it.toDomain() }
                Game.Uni2.id -> queries.selectAllUni2().executeAsList().map { it.toDomain() }
                Game.VSAV.id -> queries.selectAllVsav().executeAsList().map { it.toDomain() }
                Game.AVL.id -> queries.selectAllAvl().executeAsList().map { it.toDomain() }
                else -> queries.selectAllCommon().executeAsList().map { it.toDomain() }
            }
            val filtered = all.filter { it.characterId == characterId }
            filtered
        }
        return result
    }

    override suspend fun hasMovesCachedFor(characterId: String): Result<Boolean, WikiError> {
        val result = dbCall {
            queries.transactionWithResult {
                queries.hasMovesForCharacter(characterId).executeAsOne()
            }
        }
        return result
    }

    override suspend fun fetchMoveDataFor(
        characterId: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return Result.Error(WikiError.DatabaseError("fetchMoveDataFor not yet implemented"))
    }

    override suspend fun insertMoveList(
        game: Game?,
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        val result = dbCall {
            val insertedAt = Clock.System.now().toEpochMilliseconds()
            queries.transaction {
                moveList.forEach { move ->
                    insertCommon(move, insertedAt)
                    insertProps(move)
                }
            }
        }
        return result
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        val result = dbCall {
            queries.transaction {
                deleteProps()
                queries.deleteAllMoves()
            }
        }
        return result
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
        val result = dbCall {
            val row = queries.selectLastInsertTimestamp().executeAsOne()
            val millis = row.lastInsertedAt
            millis?.let { Instant.fromEpochMilliseconds(it) }
        }
        return result
    }


    private fun insertProps(move: Move) {
        when (gameId) {
            Game.Tekken8.id -> insertT8Props(move)
            Game.StreetFighter6.id -> insertSf6Props(move)
            Game.KoFXV.id -> insertKof15Props(move)
            Game.COTW.id -> insertCotwProps(move)
            Game.GGST.id -> insertGgstProps(move)
            Game.DBFZ.id -> insertDbfzProps(move)
            Game.GBVSR.id -> insertGbvsrProps(move)
            Game.MK1.id -> insertMkProps(move)
            Game.MBTL.id -> insertMbProps(move)
            Game.BBCF.id -> insertBbProps(move)
            Game.Uni2.id -> insertUni2Props(move)
            Game.VSAV.id -> insertVsavProps(move)
            Game.AVL.id -> insertAvlProps(move)
            else -> Unit
        }
    }

    private fun insertCommon(move: Move, insertedAt: Long) {
        queries.insertMove(
            id = move.id,
            characterId = move.characterId,
            name = move.name,
            input = move.input,
            damage = move.damage,
            startup = move.startup,
            onBlock = move.onBlock,
            onHit = move.onHit,
            onCH = move.onCH,
            active = move.active,
            cancel = move.cancel,
            recovery = move.recovery,
            guard = move.guard,
            invulnerability = move.invulnerability,
            isThrow = move.isThrow,
            notes = move.notes.fromDomain(),
            aliases = move.aliases.fromDomain(),
            urlsVideoId = move.urls.videoId,
            urlsHitboxImageList = move.urls.hitboxImageList.fromDomain(),
            urlsMoveImageList = move.urls.moveImageList.fromDomain(),
            urlsWikiUrl = move.urls.wikiUrl,
            insertedAt = insertedAt,
        )
    }

    private fun deleteProps() {
        when (gameId) {
            Game.Tekken8.id -> queries.deleteAllT8Props()
            Game.StreetFighter6.id -> queries.deleteAllSf6Props()
            Game.KoFXV.id -> queries.deleteAllKof15Props()
            Game.COTW.id -> queries.deleteAllCotwProps()
            Game.GGST.id -> queries.deleteAllGgstProps()
            Game.DBFZ.id -> queries.deleteAllDbfzProps()
            Game.GBVSR.id -> queries.deleteAllGbvsrProps()
            Game.MK1.id -> queries.deleteAllMkProps()
            Game.MBTL.id -> queries.deleteAllMbProps()
            Game.BBCF.id -> queries.deleteAllBbProps()
            Game.Uni2.id -> queries.deleteAllUni2Props()
            Game.VSAV.id -> queries.deleteAllVsavProps()
            Game.AVL.id -> queries.deleteAllAvlProps()
            else -> Unit
        }
    }

    private fun insertT8Props(move: Move) {
        val props = move.t8Properties ?: return
        queries.insertT8Props(
            moveId = move.id,
            isHeat = props.isHeat,
            isHoming = props.isHoming,
            stance = props.stance,
            isPowerCrush = props.isPowerCrush,
            isHighCrush = props.isHighCrush,
            isLowCrush = props.isLowCrush,
            hasWallInteraction = props.hasWallInteraction,
            hasFloorInteraction = props.hasFloorInteraction,
        )
    }

    private fun insertSf6Props(move: Move) {
        val props = move.sf6Properties ?: return
        queries.insertSf6Props(
            moveId = move.id,
            type = props.type?.name,
            images = props.images?.fromDomain(),
            chip = props.chip,
            dmgScaling = props.dmgScaling,
            total = props.total,
            hitConfirm = props.hitConfirm,
            punishAdv = props.punishAdv,
            perfParryAdv = props.perfParryAdv,
            DRcOH = props.DRcOH,
            DRcOB = props.DRcOB,
            DROH = props.DROH,
            DROB = props.DROB,
            hitStun = props.hitStun,
            blockStun = props.blockStun,
            hitStop = props.hitStop,
            driveDmgOnBlock = props.driveDmgOnBlock,
            driveDmgOnHit = props.driveDmgOnHit,
            driveGain = props.driveGain,
            superGainOnHit = props.superGainOnHit,
            superGainOnBlock = props.superGainOnBlock,
            armor = props.armor,
            airborne = props.airborne,
            jugStart = props.jugStart,
            jugIncrease = props.jugIncrease,
            jugLimit = props.jugLimit,
            projectileSpeed = props.projectileSpeed,
            attackRange = props.attackRange,
        )
    }

    private fun insertKof15Props(move: Move) {
        val props = move.koF15Properties ?: return
        queries.insertKof15Props(
            moveId = move.id,
            stun = props.stun,
        )
    }

    private fun insertCotwProps(move: Move) {
        val props = move.cotwProperties ?: return
        queries.insertCotwProps(
            moveId = move.id,
            revDamage = props.revDamage,
        )
    }

    private fun insertGgstProps(move: Move) {
        val props = move.ggstProperties ?: return
        queries.insertGgstProps(
            moveId = move.id,
            type = props.type,
            riscGain = props.riscGain,
            riscLoss = props.riscLoss,
            wallDamage = props.wallDamage,
            inputTension = props.inputTension,
            chipRatio = props.chipRatio,
            otgType = props.otgType,
            prorate = props.prorate,
            level = props.level,
        )
    }

    private fun insertDbfzProps(move: Move) {
        val props = move.dbfzProperties ?: return
        queries.insertDbfzProps(
            moveId = move.id,
            attribute = props.attribute,
            smash = props.smash,
            kiGain = props.kiGain,
            prorate = props.prorate,
            blockStun = props.blockStun,
            groundHit = props.groundHit,
            airHit = props.airHit,
            type = props.type,
            level = props.level,
        )
    }

    private fun insertGbvsrProps(move: Move) {
        val props = move.gbvsrProperties ?: return
        queries.insertGbvsrProps(
            moveId = move.id,
            meter = props.meter,
            level = props.level,
            cooldown = props.cooldown,
            cls = props.cls,
            type = props.type,
        )
    }

    private fun insertMkProps(move: Move) {
        val props = move.mkProperties ?: return
        queries.insertMkProps(
            moveId = move.id,
            moveType = props.moveType,
            cost = props.cost.fromDomain(),
            chip = props.chip,
            flawlessBlockAdv = props.flawlessBlockAdv,
            hitCancelAdv = props.hitCancelAdv,
            blockCancelAdv = props.blockCancelAdv,
            punish = props.punish,
        )
    }

    private fun insertMbProps(move: Move) {
        val props = move.mbProperties ?: return
        queries.insertMbProps(
            moveId = move.id,
            inputInfo = props.inputInfo,
            subtitle = props.subtitle,
            minDamage = props.minDamage,
            property_ = props.property,
            cost = props.cost,
            attribute = props.attribute,
            landing = props.landing,
            overall = props.overall,
        )
    }

    private fun insertBbProps(move: Move) {
        val props = move.bbProperties ?: return
        queries.insertBbProps(
            moveId = move.id,
            onODR = props.onODR,
            attribute = props.attribute,
            p1 = props.p1,
            p2 = props.p2,
            starter = props.starter,
            level = props.level,
            blockstun = props.blockstun,
            groundHit = props.groundHit,
            airHit = props.airHit,
            groundCH = props.groundCH,
            airCH = props.airCH,
            blockstop = props.blockstop,
            hitstop = props.hitstop,
            chStop = props.chStop,
            cancelTiming = props.cancelTiming,
            type = props.type,
        )
    }

    private fun insertUni2Props(move: Move) {
        val props = move.uni2Properties ?: return
        queries.insertUni2Props(
            moveId = move.id,
            inputInfo = props.inputInfo,
            subtitle = props.subtitle,
            minDamage = props.minDamage,
            type = props.type,
            cancelWindow = props.cancelWindow,
            property_ = props.property,
            cost = props.cost,
            attribute = props.attribute,
            landing = props.landing,
            overall = props.overall,
            assaultAdv = props.assaultAdv,
            blockstun = props.blockstun,
            groundHit = props.groundHit,
            airHit = props.airHit,
            groundCH = props.groundCH,
            airCH = props.airCH,
            hitstop = props.hitstop,
            CHstop = props.CHstop,
            proration = props.proration,
            comboP1 = props.comboP1,
            comboP2 = props.comboP2,
        )
    }

    private fun insertVsavProps(move: Move) {
        val props = move.vsavProperties ?: return
        queries.insertVsavProps(
            moveId = move.id,
            inputInfo = props.inputInfo,
            subtitle = props.subtitle,
            whiteDmg = props.whiteDmg,
            renda = props.renda,
            meter = props.meter,
            reaction = props.reaction,
            curseTime = props.curseTime,
        )
    }

    private fun insertAvlProps(move: Move) {
        val props = move.avlProperties ?: return
        queries.insertAvlProps(
            moveId = move.id,
            chiDamage = props.chiDamage,
            flow = props.flow,
        )
    }


    private suspend fun <T> dbCall(block: () -> T): Result<T, WikiError> {
        return withContext(dispatcher) {
            try {
                val data = block()
                Result.Success(data)
            } catch (e: Exception) {
                Result.Error(WikiError.DatabaseError(e.message ?: "Unknown database error"))
            }
        }
    }
}