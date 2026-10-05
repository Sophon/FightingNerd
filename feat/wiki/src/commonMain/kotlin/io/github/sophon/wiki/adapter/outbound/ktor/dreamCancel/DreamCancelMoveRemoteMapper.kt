package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.wiki.adapter.outbound.ktor.findMoveIdPrefix
import io.github.sophon.wiki.adapter.outbound.ktor.removeMoveIdPrefix
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.MoveGameProperties
import io.github.sophon.wiki.model.game.COTWMoveProperties
import io.github.sophon.wiki.model.game.KOF15MoveProperties
import io.github.sophon.wiki.model.wiki.Game

/**
 * Bulk - the whole move table, grouped into characters. `chara` spellings that form the same grouping key are one character.
 * The move ID prefix is found per character.
 */
internal fun DreamCancelMoveListResponseDto.toDomainAll(
    game: Game,
    iconUrlMap: Map<String, String>,
    hitboxUrlMap: Map<String, String>,
): List<Pair<Character, List<Move>>> {
    val characterWithMovesList = cargoQuery
        .groupBy { query -> query.title.chara.formGroupingKey() }
        .map { (_, queryList) ->
            val dtoList = queryList.map { query -> query.title }
            val character = dtoList.first().chara.toCharacter(game, iconUrlMap)
            val moveIdPrefix = dtoList.map { dto -> dto.moveId }.findMoveIdPrefix()
            val moveList = dtoList.map { dto -> dto.toDomain(game, moveIdPrefix, hitboxUrlMap) }
            character to moveList
        }
    return characterWithMovesList
}

/**
 * Only cleaning - input normalization and aliases are done by the service.
 * The input comes from the move ID - the wiki's `input` doesn't tell versions apart (`rock_214a` / `rock_214a_install`
 * are both `214A`), so it's kept as an alias.
 */
private fun MoveDto.toDomain(
    game: Game,
    moveIdPrefix: String,
    hitboxUrlMap: Map<String, String>,
): Move {
    val move = Move(
        input = moveId.removeMoveIdPrefix(moveIdPrefix),
        remoteId = moveId,
        aliases = listOfNotNull(input?.decodeHtmlEntities()?.takeIf { it.isNotBlank() }),
        name = name?.cleanHtml(),
        damage = damage?.cleanHtml(),
        startup = startup?.cleanHtml(),
        onBlock = blockAdv?.cleanHtml(),
        onHit = hitAdv?.cleanHtml(),
        recovery = recovery?.cleanHtml(),
        active = active?.cleanHtml(),
        urls = Move.Urls(
            hitboxImageList = hitboxes.toImageUrlList(hitboxUrlMap),
            moveImageList = images.toImageUrlList(hitboxUrlMap),
            wikiUrl = "$WIKI_BASE_URL/${game.id}/${chara.createQueryName()}",
        ),
        gameProperties = toGameProperties(game),
    )
    return move
}

private fun MoveDto.toGameProperties(game: Game): MoveGameProperties? {
    val properties = when (game) {
        Game.KoFXV -> KOF15MoveProperties(stun = stun?.cleanHtml())
        Game.COTW -> COTWMoveProperties(revDamage = revDamage?.cleanHtml())
        else -> null
    }
    return properties
}

private fun String?.toImageUrlList(imageUrlMap: Map<String, String>): List<String> {
    val imageUrlList = this
        .orEmpty()
        .split(",")
        .mapNotNull { fileName -> imageUrlMap[fileName.trim()] }
    return imageUrlList
}
