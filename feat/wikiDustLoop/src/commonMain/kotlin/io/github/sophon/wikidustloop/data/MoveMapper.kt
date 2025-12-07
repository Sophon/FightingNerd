package io.github.sophon.wikidustloop.data

import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Move

internal fun MoveListResponseDto.toDomain(
    imageUrlMap: Map<String, String>,
): List<Move> {
    return cargoQuery.map { wrapper ->
        val dto = wrapper.title

        Move(
            charName = dto.chara.orEmpty(),
            id = dto.input.formMoveId(dto.chara),
            name = dto.name,

            input = dto.input
                .orDash()
                .lowercase(),
            damage = dto.damage,
            startup = dto.startup,
            onBlock = dto.onBlock,
            onHit = dto.onHit,
            onCH = dto.counter,
            active = dto.active,
            cancel = dto.cancel,
            recovery = dto.recovery,
            guard = dto.level,
            invulnerability = dto.invuln,

            notes = dto.notes.formNotes(),

            urls = Move.Urls(
                hitboxImage = dto.hitboxes
                    ?.split(",")
                    .orEmpty()
                    .firstOrNull()
                    ?.let { imageUrlMap[it] }
            ),

            airDashProperties = Move.AirDashProperties(
                chara = dto.chara,
                name = dto.name,
                input = dto.input,
                damage = dto.damage,
                level = dto.level,
                type = dto.type,
                riscGain = dto.riscGain,
                riscLoss = dto.riscLoss,
                wallDamage = dto.wallDamage,
                inputTension = dto.inputTension,
                chipRatio = dto.chipRatio,
                otgType = dto.OTGType,
                prorate = dto.prorate,
                invuln = dto.invuln,
                cancel = dto.cancel,
            )
        )
    }
}

internal fun String?.formMoveId(charName: String?): String {
    val charNameId = charName.orEmpty()
        .replace("'", "")
        .replace(".", "")
        .replace("?", "")
        .split(" ")
        .joinToString("_") { it.lowercase() }

    return "${charNameId}_${this.orEmpty().lowercase()}"
}

internal fun String?.formNotes(): List<String> {
    return this
        ?.split(";")
        ?.map { it.trim() }
        ?: emptyList()
}