package io.github.sophon.wikidustloop.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.createAliasesFromSlash
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
            charName = dto.chara.orEmpty().cleanHtml(),
            id = dto.input.formMoveId(dto.chara),
            name = dto.name?.cleanHtml(),

            input = dto.input
                .orDash()
                .replace(" ", "")
                .lowercase(),
            damage = dto.damage?.cleanHtml(),
            startup = dto.startup?.cleanHtml(),
            onBlock = dto.onBlock?.cleanHtml(),
            onHit = dto.onHit?.cleanHtml(),
            onCH = dto.counter?.cleanHtml(),
            active = dto.active?.cleanHtml(),
            cancel = dto.cancel?.cleanHtml(),
            recovery = dto.recovery?.cleanHtml(),
            guard = dto.guard?.cleanHtml(),
            invulnerability = dto.invuln?.cleanHtml(),
            aliases = dto.input.formAliases(gameId),

            notes = dto.notes.formNotes(),

            urls = Move.Urls(
                characterImage = characterData.imageUrl,
                hitboxImageList = dto.hitboxes
                    .orEmpty()
                    .split(";", "\\")
                    .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
                moveImageList = dto.images
                    .orEmpty()
                    .split(";", "\\")
                    .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
                wikiUrl = formMoveWikiUrl(gameId, dto),
            ),

            ggstProperties = Move.GGSTProperties(
                chara = dto.chara,
                name = dto.name,
                input = dto.input,
                damage = dto.damage,
                type = dto.type,
                riscGain = dto.riscGain,
                riscLoss = dto.riscLoss,
                wallDamage = dto.wallDamage,
                inputTension = dto.inputTension,
                chipRatio = dto.chipRatio,
                otgType = dto.OTGType,
                prorate = dto.prorate,
                cancel = dto.cancel,
                level = dto.level,
            ),
            dbfzProperties = Move.DBFZProperties(
                attribute = dto.attribute,
                smash = dto.smash,
                kiGain = dto.kigain,
                prorate = dto.prorate,
                blockStun = dto.blockstun,
                groundHit = dto.groundHit,
                airHit = dto.airHit,
                type = dto.type,
                level = dto.level,
            ),
            gbvsrProperties = Move.GBVSRProperties(
                meter = dto.meter,
                level = dto.level,
                cooldown = dto.cooldown,
                cls = dto.cls,
                type = dto.type,
            ),
            bbProperties = Move.BBProperties(
                onODR = dto.onODR,
                attribute = dto.attribute,
                p1 = dto.p1,
                p2 = dto.p2,
                starter = dto.starter,
                level = dto.level,
                blockstun = dto.blockstun,
                groundHit = dto.groundHit,
                airHit = dto.airHit,
                groundCH = dto.groundCH,
                airCH = dto.airCH,
                blockstop = dto.blockstop,
                hitstop = dto.hitstop,
                chStop = dto.CHstop,
                cancelTiming = dto.cancelTiming,
                type = dto.type,
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
    val moveId = this
        .orEmpty()
        .lowercase()
        .replace(" ", "")

    return "${charNameId}_$moveId"
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

internal fun String?.formAliases(gameId: String): List<String> {
    if (this == null) return emptyList()

    return when (Game.fromId(gameId)) {
        Game.GBVSR -> createNarmayaStanceAliases()
        else -> createAliasesFromSlash()
    }
}

private fun String.createNarmayaStanceAliases(): List<String> {
    val regex = """^(.+)\[([^]]+)]$""".toRegex()
    val match = regex.find(this) ?: return emptyList()
    val (base, suffix) = match.destructured

    return listOf("${suffix.lowercase()}.${base.lowercase()}")
}
