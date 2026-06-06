package io.github.sophon.fightingnerd.core.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.fightingnerd.db.character.CharacterDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class SqlCharacterDB(
    db: CharacterDatabase,
    private val gameId: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): CharacterListDB {
    private val queries = db.characterQueries

    override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
        val result = dbCall {
            queries.transaction {
                characterList.forEach { character ->
                    insertCommon(character)
                    insertProps(character)
                }
            }
        }
        return result
    }

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        val result = dbCall {
            val list = when (gameId) {
                Game.StreetFighter6.id -> queries.selectAllSf6().executeAsList().map { it.toDomain() }
                Game.GGST.id -> queries.selectAllGgst().executeAsList().map { it.toDomain() }
                Game.DBFZ.id -> queries.selectAllDbfz().executeAsList().map { it.toDomain() }
                Game.GBVSR.id -> queries.selectAllGbvsr().executeAsList().map { it.toDomain() }
                Game.MK1.id -> queries.selectAllMk1().executeAsList().map { it.toDomain() }
                Game.BBCF.id -> queries.selectAllBb().executeAsList().map { it.toDomain() }
                Game.Uni2.id -> queries.selectAllUni2().executeAsList().map { it.toDomain() }
                else -> queries.selectAllCommon().executeAsList().map { it.toDomain() }
            }
            list
        }
        return result
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        val result = dbCall {
            queries.transaction {
                deleteProps()
                queries.deleteAllCharacters()
            }
        }
        return result
    }

    override suspend fun fetchCharacterDataFor(characterQuery: String): Result<Character, WikiError> {
        return when (val result = fetchCharacterList()) {
            is Result.Success -> {
                val character = result.data.firstOrNull { it.id == characterQuery }
                if (character == null) {
                    Result.Error(WikiError.DatabaseError("No character found for id=$characterQuery"))
                } else {
                    Result.Success(character)
                }
            }
            is Result.Error -> result
        }
    }


    private fun insertCommon(character: Character) {
        queries.insertCharacter(
            id = character.id,
            displayName = character.displayName,
            queryName = character.remoteQueryId,
            wikiUrl = character.wikiUrl,
            aliasList = character.aliasList.fromDomain(),
            imageIconUrl = character.images?.iconUrl,
            imageBannerUrl = character.images?.bannerUrl,
            hp = character.hp,
            umo = character.umo.fromDomain(),
        )
    }

    private fun insertProps(character: Character) {
        when (gameId) {
            Game.StreetFighter6.id -> insertSf6Props(character)
            Game.GGST.id -> insertGGSTProps(character)
            Game.DBFZ.id -> insertDbfzProps(character)
            Game.GBVSR.id -> insertGbvsrProps(character)
            Game.MK1.id -> insertMk1Props(character)
            Game.BBCF.id -> insertBBCFProps(character)
            Game.Uni2.id -> insertUni2Props(character)
            else -> Unit // common-only games have no props table
        }
    }

    private fun insertSf6Props(character: Character) {
        val props = character.sf6Properties ?: return
        queries.insertSf6Props(
            characterId = character.id,
            fwdWalkSpd = props.fwdWalkSpd,
            bwdWalkSpd = props.bwdWalkSpd,
            fwdDashSpd = props.fwdDashSpd,
            bwdDashSpd = props.bwdDashSpd,
            fwdDashDist = props.fwdDashDist,
            bwdDashDist = props.bwdDashDist,
            dRushMin = props.dRushMin,
            dRushBlock = props.dRushBlock,
            dRushMax = props.dRushMax,
            throwRange = props.throwRange,
            throwHurtbox = props.throwHurtbox,
            jumpSpd = props.jumpSpd,
            jumpApex = props.jumpApex,
            fwdJumpDist = props.fwdJumpDist,
            bwdJumpDist = props.bwdJumpDist,
        )
    }

    private fun insertGGSTProps(character: Character) {
        val props = character.ggstProperties ?: return
        queries.insertGgstProps(
            characterId = character.id,
            defense = props.defense,
            guts = props.guts,
            guardBalance = props.guardBalance,
            prejump = props.prejump,
            bwdDash = props.bwdDash,
            bwdDashDuration = props.bwdDashDuration,
            bwdDashInvulnerability = props.bwdDashInvulnerability,
            bwdDashAirborne = props.bwdDashAirborne,
            bwdDashDist = props.bwdDashDist,
            fwdDash = props.fwdDash,
            jumpDuration = props.jumpDuration,
            highJumpDuration = props.highJumpDuration,
            jumpHeight = props.jumpHeight,
            highJumpHeight = props.highJumpHeight,
            earliestIAD = props.earliestIAD,
            adDuration = props.adDuration,
            abdDuration = props.abdDuration,
            adDist = props.adDist,
            abdDist = props.abdDist,
            movementTension = props.movementTension,
            jumpTension = props.jumpTension,
            airDashTension = props.airDashTension,
            walkSpd = props.walkSpd,
            bwdWalkSpd = props.bwdWalkSpd,
            dashInitialSpd = props.dashInitialSpd,
            dashAcceleration = props.dashAcceleration,
            dashFriction = props.dashFriction,
            jumpGravity = props.jumpGravity,
            highJumpGravity = props.highJumpGravity,
            boostAttack = props.boostAttack,
            boostDefense = props.boostDefense,
        )
    }

    private fun insertDbfzProps(character: Character) {
        val props = character.dbfzProperties ?: return
        queries.insertDbfzProps(
            characterId = character.id,
            kiMod = props.kiMod,
        )
    }

    private fun insertGbvsrProps(character: Character) {
        val props = character.gbvsrProperties ?: return
        queries.insertGbvsrProps(
            characterId = character.id,
            backdash = props.backdash,
            walkSpeed = props.walkSpeed,
            walkSpeedBack = props.walkSpeedBack,
            dashInitial = props.dashInitial,
            dashAcceleration = props.dashAcceleration,
            jumpPre = props.jump?.pre,
            jumpForwardDistance = props.jump?.forwardDistance,
            jumpSuperForwardDistance = props.jump?.superForwardDistance,
            jumpBackDistance = props.jump?.backDistance,
            jumpSuperBackDistance = props.jump?.superBackDistance,
            jumpGravity = props.jump?.gravity,
            jumpSuperGravity = props.jump?.superGravity,
            jumpSuperHeight = props.jump?.superHeight,
            closeRangeL = props.closeRange?.l,
            closeRangeM = props.closeRange?.m,
            closeRangeH = props.closeRange?.h,
        )
    }

    private fun insertMk1Props(character: Character) {
        val props = character.mkProperties ?: return
        queries.insertMk1Props(
            characterId = character.id,
            hpMod = props.hpMod,
            throwDmg = props.throwDmg,
        )
    }

    private fun insertBBCFProps(character: Character) {
        val props = character.bbProperties ?: return
        queries.insertBbProps(
            characterId = character.id,
            preJump = props.preJump,
            backDash = props.backDash,
            forwardDash = props.forwardDash,
        )
    }

    private fun insertUni2Props(character: Character) {
        val props = character.uni2Properties ?: return
        queries.insertUni2Props(
            characterId = character.id,
            smartSteer = props.smartSteer,
            fWalkSpeed = props.fWalkSpeed,
            fWalkSpeedNote = props.fWalkSpeedNote,
            bWalkSpeed = props.bWalkSpeed,
            bWalkSpeedNote = props.bWalkSpeedNote,
            jumpStartup = props.jumpStartup,
            jumpDuration = props.jumpDuration,
            jumpDurationNote = props.jumpDurationNote,
            dashStartup = props.dashStartup,
            iDashSpeed = props.iDashSpeed,
            iDashSpeedNote = props.iDashSpeedNote,
            dashAccel = props.dashAccel,
            dashAccelNote = props.dashAccelNote,
            maxDashSpeed = props.maxDashSpeed,
            bDashStartup = props.bDashStartup,
            bDashDuration = props.bDashDuration,
            bDashDurationNote = props.bDashDurationNote,
            bDashDistance = props.bDashDistance,
            bDashDistanceNote = props.bDashDistanceNote,
            bDashFullInvulStart = props.bDashFullInvulStart,
            bDashFullInvulEnd = props.bDashFullInvulEnd,
            bDashThrowInvulStart = props.bDashThrowInvulStart,
            bDashThrowInvulEnd = props.bDashThrowInvulEnd,
            throwWidth = props.throwWidth,
            throwRange = props.throwRange,
            trait = props.trait,
            vorpalTrait = props.vorpalTrait,
        )
    }

    private fun deleteProps() {
        when (gameId) {
            Game.StreetFighter6.id -> queries.deleteAllSf6Props()
            Game.GGST.id -> queries.deleteAllGgstProps()
            Game.DBFZ.id -> queries.deleteAllDbfzProps()
            Game.GBVSR.id -> queries.deleteAllGbvsrProps()
            Game.MK1.id -> queries.deleteAllMk1Props()
            Game.BBCF.id -> queries.deleteAllBbProps()
            Game.Uni2.id -> queries.deleteAllUni2Props()
            else -> Unit
        }
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
