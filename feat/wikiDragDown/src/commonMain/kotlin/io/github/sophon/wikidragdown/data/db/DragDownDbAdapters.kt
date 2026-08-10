package io.github.sophon.wikidragdown.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterDbAdapter
import io.github.sophon.core.wiki.data.MoveDbAdapter
import io.github.sophon.core.wiki.data.fromDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikidragdown.data.DragDownDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DragDownCharacterDbAdapter(
    private val db: DragDownDB,
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

internal class DragDownMoveDbAdapter(
    private val db: DragDownDB,
    private val game: Game,
) : MoveDbAdapter {
    private val queries = db.moveQueries
    private val roa2Queries = db.rOA2MoveQueries

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
            Game.ROA2 -> {
                val p = move.roa2Properties
                roa2Queries.insertROA2Move(
                    moveId = move.id,
                    mode = p?.mode,
                    caption = p?.caption?.fromDomain(),
                    hitboxCaption = p?.hitboxCaption?.fromDomain(),
                    startupNotes = p?.startupNotes,
                    totalActiveNotes = p?.totalActiveNotes,
                    endlagNotes = p?.endlagNotes,
                    cancelNotes = p?.cancelNotes?.fromDomain(),
                    landingLag = p?.landingLag,
                    landingLagNotes = p?.landingLagNotes,
                    iasa = p?.iasa,
                    iasaNotes = p?.iasaNotes,
                    totalDuration = p?.totalDuration,
                    totalDurationNotes = p?.totalDurationNotes,
                    ledgeGrabFrame = p?.ledgeGrabFrame,
                    ledgeGrabFrameNotes = p?.ledgeGrabFrameNotes,
                    hitID = p?.hitID?.fromDomain(),
                    hitMoveID = p?.hitMoveID?.fromDomain(),
                    hitName = p?.hitName?.fromDomain(),
                    hitActive = p?.hitActive?.fromDomain(),
                    customShieldSafety = p?.customShieldSafety?.fromDomain(),
                    uniqueField = p?.uniqueField?.fromDomain(),
                    articleID = p?.articleID?.fromDomain(),
                    roa2Notes = p?.notes,
                    advNotes = p?.advNotes,
                )
            }
            else -> error("${game.id} is not supported by DragDown MoveDbAdapter")
        }
    }

    override fun selectMovesFlow(characterId: String): Flow<List<Move>> {
        val flow = when (game) {
            Game.ROA2 -> queries.selectROA2ByCharacter(characterId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { rows -> rows.map { it.toDomain() } }
            else -> error("${game.id} is not supported by DragDown MoveDbAdapter")
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
