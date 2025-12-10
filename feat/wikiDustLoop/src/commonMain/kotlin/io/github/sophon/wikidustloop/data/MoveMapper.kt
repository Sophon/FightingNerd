package io.github.sophon.wikidustloop.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    characterData: DownloadMoveListUseCase.CharacterData,
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
            damage = dto.damage?.cleanHtml(),
            startup = dto.startup?.cleanHtml(),
            onBlock = dto.onBlock?.cleanHtml(),
            onHit = dto.onHit?.cleanHtml(),
            onCH = dto.counter?.cleanHtml(),
            active = dto.active?.cleanHtml(),
            cancel = dto.cancel?.cleanHtml(),
            recovery = dto.recovery?.cleanHtml(),
            guard = dto.level?.cleanHtml(),
            invulnerability = dto.invuln?.cleanHtml(),

            notes = dto.notes.formNotes(),

            urls = Move.Urls(
                characterImage = characterData.imageUrl,
                hitboxImageList = dto.hitboxes
                    .orEmpty()
                    .split(";")
                    .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
                wikiUrl = formMoveWikiUrl(gameId, dto),
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
        ?.cleanHtml()
        ?.split(";")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        ?: emptyList()
}

internal fun formMoveWikiUrl(gameId: String, dto: MoveDto): String {
    return "${dto.chara.formWikiUrl(gameId)}#${dto.input}"
}