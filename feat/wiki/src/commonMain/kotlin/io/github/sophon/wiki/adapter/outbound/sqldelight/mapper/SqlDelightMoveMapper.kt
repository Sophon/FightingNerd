package io.github.sophon.wiki.adapter.outbound.sqldelight.mapper

import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.MoveGameProperties
import io.github.sophon.wiki.data.Move as MoveEntity

internal fun MoveEntity.toDomain(
    aliases: List<String>,
    gameProperties: MoveGameProperties?,
): Move {
    val move = Move(
        input = input,
        remoteId = remote_id,
        name = name,
        damage = damage,
        startup = startup,
        onBlock = on_block,
        onHit = on_hit,
        onCH = on_ch,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        isThrow = is_throw,
        type = type,
        notes = notes,
        aliases = aliases,
        urls = Move.Urls(
            wikiUrl = wiki_url,
            videoId = video_id,
            videoUrl = video_url,
            hitboxImageList = hitbox_image_list,
            moveImageList = move_image_list,
        ),
        gameProperties = gameProperties,
    )
    return move
}
