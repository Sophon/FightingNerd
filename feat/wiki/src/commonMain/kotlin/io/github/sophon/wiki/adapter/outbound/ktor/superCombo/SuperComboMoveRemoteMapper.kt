package io.github.sophon.wiki.adapter.outbound.ktor.superCombo

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.wiki.adapter.outbound.ktor.findMoveIdPrefix
import io.github.sophon.wiki.adapter.outbound.ktor.removeMoveIdPrefix
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.MoveGameProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.AVLMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MKMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.SF6MoveProperties

/**
 * One character's move list - the move ID prefix is that character's.
 */
internal fun SuperComboMoveListResponseDto.toDomain(
    game: Game,
    imageUrlMap: Map<String, String>,
): List<Move> {
    val dtoList = cargoQuery.map { query -> query.title }
    val moveIdPrefix = dtoList.map { dto -> dto.moveId }.findMoveIdPrefix()
    val moveList = dtoList.map { dto -> dto.toDomain(game, moveIdPrefix, imageUrlMap) }
    return moveList
}

/**
 * Only cleaning - the input and the aliases are normalized by the service.
 * The input comes from the move ID - the wiki's `input` doesn't tell versions apart (`mai_214hp` / `mai_214hp_flame`
 * are both `214HP`), so it's kept as an alias.
 */
private fun MoveDto.toDomain(
    game: Game,
    moveIdPrefix: String,
    imageUrlMap: Map<String, String>,
): Move {
    val move = Move(
        name = name.ignoreImageNames(),
        input = moveId.removeMoveIdPrefix(moveIdPrefix),
        remoteId = moveId,
        aliases = listOfNotNull(input.takeIf { it.isNotBlank() }),
        damage = damage.takeIfNotTemplate()?.cleanHtml(),
        startup = startup.takeIfNotTemplate(),
        onBlock = (blockAdv ?: onBlock).takeIfNotTemplate()?.cleanHtml(),
        onHit = (hitAdv ?: onHit).takeIfNotTemplate()?.cleanHtml(),
        recovery = recovery.takeIfNotTemplate()?.cleanHtml(),
        active = active.takeIfNotTemplate()?.cleanHtml(),
        guard = guard.takeIfNotTemplate(),
        cancel = cancel.takeIfNotTemplate(),
        invulnerability = invuln
            .takeIfNotTemplate()
            ?.cleanHtml()
            ?.takeIf { it.isBlank().not() },
        type = toType(game),
        notes = (notes ?: properties)
            .takeIfNotTemplate()
            ?.cleanHtml()
            .extractNotes(),
        urls = Move.Urls(
            moveImageList = images.toImageUrlList(imageUrlMap),
            hitboxImageList = hitboxes.toImageUrlList(imageUrlMap),
            wikiUrl = formMoveWikiUrl(game),
        ),
        gameProperties = toGameProperties(game),
    )
    return move
}

private fun MoveDto.toType(game: Game): String? {
    val type = when (game) {
        Game.StreetFighter6 -> {
            val lowercaseType = moveType.lowercase()
            lowercaseType.takeIf { it in sf6MoveTypeList }
        }

        else -> moveType.takeIfNotTemplate()
    }
    return type
}

@Suppress("LongMethod")
private fun MoveDto.toGameProperties(game: Game): MoveGameProperties? {
    val properties = when (game) {
        Game.StreetFighter6 -> SF6MoveProperties(
            images = images.takeIfNotTemplate()
                ?.split(",")
                ?.map { it.trim() },
            chip = chip.takeIfNotTemplate(),
            dmgScaling = dmgScaling.takeIfNotTemplate(),
            total = total.takeIfNotTemplate(),
            hitConfirm = hitconfirm.takeIfNotTemplate(),
            punishAdv = punishAdv.takeIfNotTemplate()?.cleanHtml(),
            perfParryAdv = perfParryAdv.takeIfNotTemplate()?.cleanHtml(),
            DRcOH = DRcancelHit.takeIfNotTemplate()?.cleanHtml(),
            DRcOB = DRcancelBlk.takeIfNotTemplate()?.cleanHtml(),
            DROH = afterDRHit.takeIfNotTemplate()?.cleanHtml(),
            DROB = afterDRBlk.takeIfNotTemplate()?.cleanHtml(),
            hitStun = hitstun.takeIfNotTemplate()?.cleanHtml(),
            blockStun = blockstun.takeIfNotTemplate()?.cleanHtml(),
            hitStop = hitstop.takeIfNotTemplate()?.cleanHtml(),
            driveDmgOnBlock = driveDmgBlk.takeIfNotTemplate(),
            driveDmgOnHit = driveDmgHit.takeIfNotTemplate(),
            driveGain = driveGain.takeIfNotTemplate(),
            superGainOnHit = superGainHit.takeIfNotTemplate(),
            superGainOnBlock = superGainBlk.takeIfNotTemplate(),
            armor = armor.takeIfNotTemplate(),
            jugStart = jugStart.takeIfNotTemplate()?.cleanHtml(),
            jugIncrease = jugIncrease.takeIfNotTemplate()?.cleanHtml(),
            jugLimit = jugLimit.takeIfNotTemplate(),
            projectileSpeed = projSpeed.takeIfNotTemplate(),
            attackRange = atkRange.takeIfNotTemplate(),
        )

        Game.MK1 -> MKMoveProperties(
            cost = moveType
                .split(",")
                .filterNot { it.takeIfNotTemplate() == null },
        )

        Game.AVL -> AVLMoveProperties(
            chiDamage = flowDamage,
            flow = flow,
        )

        else -> null
    }
    return properties
}

/**
 * Inputs by direction (`Any Direction + P`) get an empty url.
 */
private fun MoveDto.formMoveWikiUrl(game: Game): String {
    if (input.contains("direction", ignoreCase = true)) return ""

    val inputAnchor = input.replace(" ", "_")
    val moveAnchor = if (name.isNullOrBlank()) {
        inputAnchor
    } else {
        "${name.replace(" ", "_")}_($inputAnchor)"
    }
    val charaPath = chara.replace(" ", "_")

    val url = when (game) {
        Game.StreetFighter6 -> "$WIKI_BASE_URL/${game.id}/$charaPath#$moveAnchor"
        Game.MK1 -> "$WIKI_BASE_URL/${game.id}/$charaPath/Data#$inputAnchor"
        else -> WIKI_BASE_URL
    }
    return url
}

/**
 * Template placeholders (`{{{field}}}`) and `-` mean no value.
 */
private fun String?.takeIfNotTemplate(): String? {
    val value = this?.takeUnless { it.matches(templateRegex) || it == "-" }
    return value
}

private fun String?.extractNotes(): List<String> {
    val noteList = this
        ?.split(";")
        ?.map { it.trim() }
        .orEmpty()
    return noteList
}

private fun String?.toImageUrlList(imageUrlMap: Map<String, String>): List<String> {
    val imageUrlList = this
        .orEmpty()
        .split(",")
        .mapNotNull { fileName -> imageUrlMap[fileName.trim()] }
    return imageUrlList
}

/**
 * Some names are image links (`[[File:...png]]`) - those aren't names.
 */
private fun String?.ignoreImageNames(): String? {
    val name = this?.takeUnless { it.contains("[[") || it.contains("]]") || it.contains(".png") }
    return name
}


private val templateRegex = Regex("\\{\\{\\{.+\\}\\}\\}")

private val sf6MoveTypeList = listOf("ground_normal", "air_normal", "special", "super", "throw", "drive", "taunt")
