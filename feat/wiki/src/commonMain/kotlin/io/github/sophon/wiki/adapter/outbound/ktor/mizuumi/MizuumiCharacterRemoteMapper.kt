package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterGameProperties
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.game.Uni2CharProperties
import io.github.sophon.wiki.model.wiki.Game

/**
 * `displayName` is the move table's `chara` verbatim, so it doubles as [Character.remoteQueryId].
 */
internal fun MizuumiCharacterJsonResponseDto.toDomain(game: Game): List<Character> {
    val characterList = characters.map { dto ->
        val character = Character(
            id = CharacterId(game, dto.displayName),
            displayName = dto.displayName,
            remoteQueryId = dto.displayName,
            wikiUrl = "${game.wikiUrl}/${dto.displayName.replace(" ", "_")}",
            aliasList = dto.aliasList,
            images = Character.Images(
                portraitId = dto.images?.portrait?.substringAfterLast('/'),
                portrait = dto.images?.portrait,
                full = dto.images?.full,
            ),
        )
        character
    }
    return characterList
}

internal fun MizuumiCharacterListResponseDto.toDomain(
    game: Game,
    iconUrlMap: Map<String, String>,
): List<Character> {
    val characterList = cargoquery.map { characterTitle ->
        val dto = characterTitle.title

        val character = Character(
            id = CharacterId(game, dto.chara),
            displayName = dto.chara,
            remoteQueryId = dto.chara,
            wikiUrl = "${game.wikiUrl}/${dto.chara}",
            images = Character.Images(
                portraitId = dto.chara,
                portrait = iconUrlMap[dto.chara],
            ),
            hp = dto.health,
            gameProperties = dto.toGameProperties(game),
        )
        character
    }
    return characterList
}

private fun UniCharacterDto.toGameProperties(game: Game): CharacterGameProperties? {
    val properties = when (game) {
        Game.Uni2 -> Uni2CharProperties(
            smartSteer = smartSteer,
            fWalkSpeed = fWalkSpeed?.cleanHtmlOrNull(),
            fWalkSpeedNote = fWalkSpeedNote?.cleanHtmlOrNull(),
            bWalkSpeed = bWalkSpeed?.cleanHtmlOrNull(),
            bWalkSpeedNote = bWalkSpeedNote?.cleanHtmlOrNull(),
            jumpStartup = jumpStartup?.cleanHtmlOrNull(),
            jumpDuration = jumpDuration?.cleanHtmlOrNull(),
            jumpDurationNote = jumpDurationNote?.cleanHtmlOrNull(),
            dashStartup = dashStartup?.cleanHtmlOrNull(),
            iDashSpeed = iDashSpeed?.cleanHtmlOrNull(),
            iDashSpeedNote = iDashSpeedNote?.cleanHtmlOrNull(),
            dashAccel = dashAccel?.cleanHtmlOrNull(),
            dashAccelNote = dashAccelNote?.cleanHtmlOrNull(),
            maxDashSpeed = maxDashSpeed?.cleanHtmlOrNull(),
            bDashStartup = bDashStartup?.cleanHtmlOrNull(),
            bDashDuration = bDashDuration?.cleanHtmlOrNull(),
            bDashDurationNote = bDashDurationNote?.cleanHtmlOrNull(),
            bDashDistance = bDashDistance?.cleanHtmlOrNull(),
            bDashDistanceNote = bDashDistanceNote?.cleanHtmlOrNull(),
            bDashFullInvulStart = bDashFullInvulStart?.cleanHtmlOrNull(),
            bDashFullInvulEnd = bDashFullInvulEnd?.cleanHtmlOrNull(),
            bDashThrowInvulStart = bDashThrowInvulStart?.cleanHtmlOrNull(),
            bDashThrowInvulEnd = bDashThrowInvulEnd?.cleanHtmlOrNull(),
            throwWidth = throwWidth?.cleanHtmlOrNull(),
            throwRange = throwRange?.cleanHtmlOrNull(),
            trait = trait?.cleanHtmlOrNull()?.formatBulletPoints(),
            vorpalTrait = vorpalTrait?.cleanHtmlOrNull()?.formatBulletPoints(),
        )

        else -> null
    }
    return properties
}

private fun String.formatBulletPoints(): String {
    val formatted = replace("*", "- ")
    return formatted
}
