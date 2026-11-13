package io.github.sophon.wiki.adapter.outbound.ktor.xko

import io.github.sophon.core.util.orDash
import io.github.sophon.wiki.model.Move

internal fun XkoMoveListResponseDto.toDomain(): List<Move> {
    val moveList = bucket.map { dto -> dto.toDomain().normalizeXko() }
    return moveList
}

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

private const val IMAGE_URL = "$WIKI_BASE_URL/images"
