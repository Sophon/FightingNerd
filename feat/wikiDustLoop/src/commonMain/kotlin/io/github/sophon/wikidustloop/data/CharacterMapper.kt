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
                aliasList = dto.name.createAliases(dto.aliases),
                images = Character.Images(
                    iconUrl = dto.icon.let { imageUrlMap[it] },
                    bannerUrl = dto.portrait.let { imageUrlMap[it] },
                ),
                ggstProperties = Character.GGSTProperties(
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
                dbfzProperties = Character.DBFZProperties(
                    kiMod = dto.kimod,
                    umo = dto.umo,
                ),
                gbvsrProperties = Character.GBVSRProperties(
                    prejump = dto.prejump,
                    backdash = dto.backdash,
                    umo = dto.umo,
                    walkSpeed = dto.walkSpeed,
                    walkSpeedRelative = dto.relative_walk_speed.toString(),
                    walkSpeedBack = dto.backwalk_speed.toString(),
                    walkSpeedBackRelative = dto.relative_backwalk_speed.toString(),
                    dashInitial = dto.dash_initial_speed.toString(),
                    dashInitialRelative = dto.relative_dash_initial_speed.toString(),
                    dashAcceleration = dto.dash_acceleration,
                    dashAccelerationRelative = dto.relative_dash_acceleration.toString(),
                )
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

internal fun String?.createAliases(dtoAliases: String?): List<String> {
    return buildList {
        dtoAliases?.let { aliases ->
            aliases.split(";").forEach { add(it.lowercase()) }
        }

        val original = this@createAliases.orEmpty()
        if (original.isBlank()) return@buildList

        // Handle hyphen-number pattern (e.g., "Zato-1")
        if (original.contains(Regex("-\\d+"))) {
            val baseName = original.substringBeforeLast("-")
            add(baseName.lowercase())
            return@buildList
        }

        // Check for parenthesis
        val hasParenthesis = original.contains("(") && original.contains(")")

        if (hasParenthesis) {
            val mainPart = original.substringBefore("(").trim()
            val variantPart = original.substringAfter("(").substringBefore(")").trim()

            val cleanedMain = mainPart.replace("-", "").replace(".", "")
            val mainWords = cleanedMain.split(" ").filter { it.isNotBlank() }

            val cleanedVariant = variantPart.replace("-", "").replace(".", "")
            val variantWords = cleanedVariant.split(" ").filter { it.isNotBlank() }

            // Extract number from main part
            val numberWord = mainWords.firstOrNull { it.any { char -> char.isDigit() } }
            val number = numberWord?.filter { it.isDigit() }

            if (number != null) {
                // "Android 21 (Lab Coat)" → ["a21", "a21lc"]
                val firstWordInitial = mainWords.first().first().lowercase()
                add(firstWordInitial + number)

                if (variantWords.isNotEmpty()) {
                    val variantInitials = variantWords.joinToString("") { it.first().lowercase() }
                    add(firstWordInitial + number + variantInitials)
                }
            } else if (mainWords.size == 1) {
                if (variantWords.size == 1) {
                    // "Gogeta (SSGSS)" → "gogetassgss"
                    add(mainWords.first().lowercase() + variantWords.first().lowercase())
                } else if (variantWords.size > 1) {
                    // "Goku (Super Saiyan)" → "gokuss"
                    val variantInitials = variantWords.joinToString("") { it.first().lowercase() }
                    add(mainWords.first().lowercase() + variantInitials)
                }
            }
            return@buildList
        }

        // No parenthesis - check for numbers first
        val cleaned = original.replace("-", "").replace(".", "")
        val words = cleaned.split(" ").filter { it.isNotBlank() }

        if (words.size <= 1) return@buildList

        // Check if any word contains a number
        val numberWord = words.firstOrNull { it.any { char -> char.isDigit() } }
        if (numberWord != null) {
            // "Android 16" → "a16"
            val number = numberWord.filter { it.isDigit() }
            val firstWordInitial = words.first().first().lowercase()
            add(firstWordInitial + number)
            return@buildList
        }

        // Regular processing for multi-word names without numbers
        // Create initials from all words
        add(words.joinToString("") { it.first().lowercase() })

        // Add individual words with length > 1
        words.forEach { word ->
            if (word.length > 1) {
                add(word.lowercase())
            }
        }
    }
}

internal fun String?.createAliases2(): List<String> {
    return buildList {
        val original = this@createAliases2.orEmpty()
        if (original.contains(Regex("-\\d+"))) {
            val baseName = original.substringBeforeLast("-")
            add(baseName.lowercase())
        }

        this@createAliases2
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
