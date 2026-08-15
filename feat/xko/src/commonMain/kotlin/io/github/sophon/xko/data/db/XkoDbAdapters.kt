package io.github.sophon.xko.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.sophon.core.wiki.data.CharacterDbAdapter
import io.github.sophon.core.wiki.data.MoveDbAdapter
import io.github.sophon.core.wiki.data.fromDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.xko.data.XkoDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class XkoCharacterDbAdapter(
    private val db: XkoDB,
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
internal class XkoMoveDbAdapter(
    private val db: XkoDB,
    private val gameId: String,
) : MoveDbAdapter {
    private val queries = db.moveQueries

    override fun insert(move: Move) {
        queries.insertMove(
            id = move.id,
            characterId = move.characterId,
            gameId = gameId,
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
    }

    override fun selectMovesFlow(characterId: String): Flow<List<Move>> {
        val flow = queries.selectByCharacter(characterId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }
        return flow
    }

    override fun incrementFailureCountForAbsent(characterId: String, remoteIds: List<String>) {
        queries.incrementFailureCountForAbsent(characterId, remoteIds)
    }

    override fun deleteExceededThreshold(characterId: String, threshold: Long) {
        queries.deleteExceededThreshold(characterId, threshold)
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
