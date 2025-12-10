package io.github.sophon.wikidustloop.data

import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikidustloop.WIKI_BASE_URL

/**
 * TODO: don't filter out junk characters
 *  they are actually extra data we need to include to the root char
 *  1. create a list of every char
 *  2. if CHARACTER (something) exists
 *      1. see the differences between CHARACTER and CHARACTER (something)
 *      2. take the (something) version of the different field and transform it into String
 *      3. add to the root CHARACTER's Notes
 */
internal fun CharacterListResponseDto.toDomain(
    imageUrlMap: Map<String, String>,
    gameId: String,
): List<Character> {
    return cargoQuery
//        .filterOutJunkCharacters()
        .map { query ->
            val dto = query.title
            val queryName = dto.name.formCharacterQueryName()

            Character(
                id = dto.name.formCharacterId(),
                displayName = dto.name.orEmpty(),
                queryName = queryName,
                wikiUrl = queryName.formWikiUrl(gameId),
                aliasList = dto.name.createAliases(),
                images = Character.Images(
                    iconUrl = dto.icon.let { imageUrlMap[it] },
                    bannerUrl = dto.portrait.let { imageUrlMap[it] },
                ),
                airDashProperties = Character.AirDashProperties(
                    defense = dto.defense,
                    guts = dto.guts,
                    guardBalance = dto.guardBalance,
                    prejump = dto.prejump,
                    bwdDash = dto.backdash,
                    bwdDashDuration = dto.backdashDuration,
                    bwdDashInvulnerability = dto.backdashInvuln,
                    bwdDashAirborne = dto.backdashAirborne,
                    bwdDashDist = dto.backdashDistance,
                    fwdDash = dto.forwardDash,
                    umo = dto.umo.toClickable(),
                    jumpDuration = dto.jumpDuration,
                    highJumpDuration = dto.highJumpDuration,
                    jumpHeight = dto.jumpHeight,
                    highJumpHeight = dto.highJumpHeight,
                    earliestIAD = dto.earliestIad,
                    adDuration = dto.adDuration,
                    abdDuration = dto.abdDuration,
                    adDist = dto.adDistance,
                    abdDist = dto.abdDistance,
                    movementTension = dto.movementTension,
                    jumpTension = dto.jumpTension,
                    airDashTension = dto.airDashTension,
                    walkSpd = dto.walkSpeed,
                    bwdWalkSpd = dto.backWalkSpeed,
                    dashInitialSpd = dto.dashInitialSpeed,
                    dashAcceleration = dto.dashAcceleration,
                    dashFriction = dto.dashFriction,
                    jumpGravity = dto.jumpGravity,
                    highJumpGravity = dto.highJumpGravity,
                    boostAttack = dto.boostAttack,
                    boostDefense = dto.boostDefense,
                ),
            )
        }
}

internal fun String?.formCharacterId(): String {
    val charId = this
        .orEmpty()
        .replace(".", "")
        .replace("?", "")
        .replace("'", "")
        .replace("-", "")
        .split(" ")
        .joinToString("_") { it.lowercase() }

    return charId
}

internal fun String?.formCharacterQueryName(): String {
    val query = this
        .orEmpty()
        .replace("?", "")
        .replace("'", "")

    return query
}

internal fun String?.formWikiUrl(gameId: String): String {
    val formatted = this.orEmpty().replace(" ", "_")
    return "$WIKI_BASE_URL/$gameId/$formatted"
}

internal fun String?.createAliases(): List<String> {
    return buildList {
        val original = this@createAliases.orEmpty()
        if (original.contains(Regex("-\\d+"))) {
            val baseName = original.substringBeforeLast("-")
            add(baseName.lowercase())
        }

        this@createAliases
            .orEmpty()
            .replace("-", "")
            .replace(".", "")
            .split(" ")
            .filter { it.isNotBlank() }
            .takeIf { it.size > 1 }
            ?.apply {
                add(joinToString("") { it.first().lowercase() })
            }
            ?.let { words ->
                words.forEach { word ->
                    if (word.length > 1) {
                        add(word.lowercase())
                    }
                }
            }
    }
}

/**
 * [[GGST/Baiken#Kabari|[H] Kabari follow-up]] -> [[H] Kabari follow-up](https://www.dustloop.com/w/GGST/Baiken#Kabari)
 * Step-Dash (15F), [[GGST/Johnny#Mist Finer Stance|Mist Finer Dash]], [[GGST/Johnny#Vault|Vault]] ->
 *  * Step-Dash (15F)
 *  * [Mist Finer Dash](https://www.dustloop.com/w/GGST/Johnny#Mist_Finer_Stance)
 *  * [Vault](https://www.dustloop.com/w/GGST/Johnny#Vault)
 */
internal fun String?.toClickable(): List<String> {
    if (isNullOrBlank()) return listOf()

    return orEmpty().split(",").map {
        val option = it.trim()
        if (option.startsWith("[[") && option.endsWith("]]") && option.contains("|")) {
            val fields = option
                .substringAfter("[[")
                .substringBefore("]]")
                .split("|")
            val title = fields.lastOrNull()?.trim() ?: ""
            val partialUrl = (fields.firstOrNull() ?: "")
                .replace(" ", "_")
                .trim()
            "[$title](${WIKI_BASE_URL}/$partialUrl)"
        } else {
            option
        }
    }
}
