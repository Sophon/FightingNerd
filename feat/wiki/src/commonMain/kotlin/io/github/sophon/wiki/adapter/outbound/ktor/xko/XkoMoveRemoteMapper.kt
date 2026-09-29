package io.github.sophon.wiki.adapter.outbound.ktor.xko

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.orDash
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Move

/**
 * Bulk - the whole move bucket, grouped into characters by page.
 */
internal fun XkoMoveListResponseDto.toDomainAll(game: Game): List<Pair<Character, List<Move>>> {
    val characterWithMovesList = bucket
        .filterNot { dto -> dto.pageName.isExcludedPage() }
        .groupBy { dto -> dto.pageName }
        .map { (pageName, dtoList) ->
            val character = pageName.toCharacter(game)
            val moveList = dtoList.map { dto -> dto.toDomain() }
            character to moveList
        }
    return characterWithMovesList
}

/**
 * Only cleaning - the input and the aliases are normalized by the service.
 */
private fun MoveDto.toDomain(): Move {
    val cleanedInput = input.orDash()

    val move = Move(
        input = cleanedInput,
        damage = damage?.ifEmpty { null },
        startup = startup,
        onBlock = onBlock?.ifEmpty { null },
        recovery = recovery,
        active = active?.ifEmpty { null },
        cancel = cancel?.ifEmpty { null },
        guard = guard?.ifEmpty { null },
        invulnerability = invuln?.ifEmpty { null },
        urls = Move.Urls(
            hitboxImageList = listOf("$IMAGE_URL/${pageName}_${input}_Hitbox.png"),
            moveImageList = listOf("$IMAGE_URL/${pageName}_$input.png"),
            wikiUrl = "$WIKI_BASE_URL/$pageName#$input",
        ),
    )
    return move
}

/**
 * Drops namespaced pages (`Template:...`) and ` POC` pages.
 */
private fun String.isExcludedPage(): Boolean {
    val lowercasePageName = lowercase()
    val isExcluded = (lowercasePageName.contains(":") || lowercasePageName.contains(" poc"))
    return isExcluded
}


private const val IMAGE_URL = "$WIKI_BASE_URL/images"
