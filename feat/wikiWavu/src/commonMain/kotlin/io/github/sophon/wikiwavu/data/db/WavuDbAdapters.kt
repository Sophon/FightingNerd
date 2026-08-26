package io.github.sophon.wikiwavu.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.wiki.data.CharacterDbAdapter
import io.github.sophon.core.wiki.data.MoveDbAdapter
import io.github.sophon.core.wiki.data.fromDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.data.WavuDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class WavuCharacterDbAdapter(
    private val db: WavuDB,
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
        val flow = queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                val characters = rows.map { it.toDomain() }
                characters
            }
        return flow
    }

    override fun incrementFailureCountForAbsent(remoteIds: List<String>) {
        queries.incrementFailureCountForAbsent(remoteIds)
    }

    override fun deleteExceededThreshold(threshold: Long) {
        queries.deleteExceededThreshold(threshold)
    }

    override fun deleteAll() {
        queries.deleteAll()
    }

    override fun transaction(block: () -> Unit) {
        db.transaction { block() }
    }

    override fun getLastUpdateTimestamp(): Instant? {
        val millis = queries.selectLastInsertedAt().executeAsOne().lastInsertedAt
        val timestamp = millis?.let { Instant.fromEpochMilliseconds(it) }
        return timestamp
    }
}

@OptIn(ExperimentalTime::class)
internal class WavuMoveDbAdapter(
    private val db: WavuDB,
) : MoveDbAdapter {
    private val moveQueries = db.moveQueries
    private val tekkenQueries = db.tekkenMoveQueries

    override fun insert(move: Move) {
        moveQueries.insertMove(
            id = move.id,
            characterId = move.characterId,
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
            urlsVideoUrl = move.urls.videoUrl,
            urlsHitboxImageList = move.urls.hitboxImageList.fromDomain(),
            urlsMoveImageList = move.urls.moveImageList.fromDomain(),
        )
        val t8 = move.t8Properties
        tekkenQueries.insertTekkenMove(
            moveId = move.id,
            isHeat = t8?.isHeat ?: false,
            isHoming = t8?.isHoming ?: false,
            stance = t8?.stance,
            isPowerCrush = t8?.isPowerCrush ?: false,
            isHighCrush = t8?.isHighCrush ?: false,
            isLowCrush = t8?.isLowCrush ?: false,
            hasWallInteraction = t8?.hasWallInteraction ?: false,
            hasFloorInteraction = t8?.hasFloorInteraction ?: false,
        )
    }

    override fun selectMovesFlow(characterId: String): Flow<List<Move>> {
        val flow = moveQueries.selectTekkenByCharacter(characterId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                val moves = rows.map { it.toDomain() }
                moves
            }
        return flow
    }

    override fun incrementFailureCountForAbsent(characterId: String, remoteIds: List<String>) {
        moveQueries.incrementFailureCountForAbsent(characterId, remoteIds)
    }

    override fun deleteExceededThreshold(characterId: String, threshold: Long) {
        moveQueries.deleteExceededThreshold(characterId, threshold)
    }

    override fun deleteAll() {
        moveQueries.deleteAll()
    }

    override fun transaction(block: () -> Unit) {
        db.transaction { block() }
    }

    override fun getLastUpdateTimestamp(): Instant? {
        val millis = moveQueries.selectLastInsertedAt().executeAsOne().lastInsertedAt
        val timestamp = millis?.let { Instant.fromEpochMilliseconds(it) }
        return timestamp
    }
}
