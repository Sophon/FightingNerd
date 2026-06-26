package io.github.sophon.wikidustloop.data

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.urlEncode
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikidustloop.domain.BASE_URL
import io.github.sophon.wikidustloop.util.toClickable

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
    val characterList = cargoQuery
        .filterAltModeCharacters()
        .map { query ->
            val dto = query.title
            val id = dto.name?.cleanHtml().formCharacterId()
            val displayName = dto.name?.cleanHtml().orEmpty()
            val queryName = dto.name
                ?.cleanHtml()
                .formCharacterQueryName(gameId)

            val character = Character(
                id = id,
                displayName = displayName,
                remoteQueryId = queryName,
                wikiUrl = queryName.formWikiUrl(gameId),
                aliasList = dto.name.createAliases(gameId, dto.aliases),
                images = Character.Images(
                    iconUrl = dto.icon.let { imageUrlMap[it] },
                    bannerUrl = dto.portrait.let { imageUrlMap[it] },
                ),
                hp = dto.health?.cleanHtml(),
                umo = dto.umo?.cleanHtml().formUmo(),
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
                    kiMod = dto.kimod?.cleanHtml(),
                ),
                gbvsrProperties = Character.GBVSRProperties(
                    jump = Character.GBVSRProperties.Jump(
                        pre = dto.prejump,
                        forwardDistance = dto.f_jump_distance?.toString(),
                        superForwardDistance = dto.f_superjump_distance?.toString(),
                        backDistance = dto.b_jump_distance?.toString(),
                        superBackDistance = dto.b_superjump_distance?.toString(),
                        gravity = dto.jumpGravity,
                        superGravity = dto.superjump_gravity?.toString(),
                        superHeight = dto.superjump_height?.toString(),
                    ),
                    backdash = dto.backdash?.cleanHtml(),
                    walkSpeed = dto.walk_speed.toString(),
                    walkSpeedBack = dto.backwalk_speed.toString(),
                    dashInitial = dto.dash_initial_speed.toString(),
                    dashAcceleration = dto.dash_acceleration,
                    closeRange = Character.GBVSRProperties.CloseRange(
                        l = dto.close_l_range?.toString(),
                        m = dto.close_m_range?.toString(),
                        h = dto.close_h_range?.toString(),
                    )
                ),
                bbProperties = Character.BBProperties(
                    preJump = dto.prejump?.cleanHtml(),
                    backDash = dto.backdash?.cleanHtml(),
                    forwardDash = dto.forwardDash?.cleanHtml(),
                ),
            )

            character
        }
    return characterList
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

internal fun String?.formCharacterQueryName(gameId: String): String {
    val game = Game.fromId(gameId)

    val query = this
        .orEmpty()
        .replace("?", "")
        .let { if (game != Game.BBCF) it.replace("'", "") else it }

    return query
}

internal fun String?.formWikiUrl(gameId: String): String {
    val formatted = this.orEmpty()
        .replace(" ", "_")
        .decodeHtmlEntities()
        .urlEncode()
    val game = Game.fromId(gameId)
    val url = "${game?.wikiUrl ?: BASE_URL}/$formatted"

    return url
}

internal fun String?.createAliases(
    gameId: String,
    dtoAliases: String? = null
): List<String> {
    if (this == null) return listOf()

    dtoAliases?.let { aliases ->
        return aliases
            .split(";", ",")
            .map { it.replace(" ", "").lowercase() }
    }

    val game = Game.fromId(gameId)

    return when (game) {
        Game.GGST -> this.createGGAliases()
        Game.GBVSR -> listOf(this.substringBefore(" ").lowercase())
        Game.BBCF -> this.createBBAliases()
        else -> listOf()
    }
}

private fun String?.createGGAliases(): List<String> {
    return buildList {
        val original = this@createGGAliases.orEmpty().lowercase()
        if (original.isBlank()) return@buildList

        // Twitter hashtag approach
        add(
            when {
                original == "jack-o" -> "jc"
                original.contains(".") -> original.replace(".", "").take(2)
                else -> original.take(2)
            }
        )

        // Handle hyphen-number pattern (e.g., "Zato-1")
        if (original.contains(Regex("-\\d+"))) {
            val baseName = original.substringBeforeLast("-")
            add(baseName)
        }

        val cleaned = original
            .replace("-", "")
            .replace(".", "")
        val words = cleaned
            .split(" ")
            .filter { it.isNotBlank() }

        if (words.first().length > 2 && words.first() != original) add(words.first())

        // Create initials from all words
        if (words.size <= 1) return@buildList
        add(words.joinToString("") { it.first().toString() })
    }.distinct()
}

internal fun String?.createBBAliases(): List<String> {
    if (this.isNullOrBlank()) return emptyList()

    val bbCodeMap = mapOf(
        "amane nishiki" to listOf("amane", "am"),
        "arakune" to listOf("ar", "kune"),
        "azrael" to listOf("az"),
        "bang shishigami" to listOf("bang", "bn"),
        "bullet" to listOf("bl"),
        "carl clover" to listOf("carl", "ca"),
        "celica a. mercury" to listOf("celica", "ce"),
        "es" to listOf(),
        "hakumen" to listOf("ha"),
        "hazama" to listOf("hz"),
        "hibiki kohaku" to listOf("hibiki", "hb"),
        "iron tager" to listOf("tager", "iron", "tg"),
        "izanami" to listOf("mi"),
        "izayoi" to listOf("iz"),
        "jin kisaragi" to listOf("jin", "jn"),
        "jubei" to listOf("jb"),
        "kagura mutsuki" to listOf("kagura", "kg"),
        "kokonoe" to listOf("kk"),
        "lambda-11" to listOf("lambda", "rm"),
        "litchi faye ling" to listOf("litchi", "lc"),
        "mai natsume" to listOf("mai", "ma"),
        "makoto nanaya" to listOf("makoto", "mk"),
        "mu-12" to listOf("mu"),
        "naoto kurogane" to listOf("naoto", "nt"),
        "nine the phantom" to listOf("nine", "ph"),
        "noel vermillion" to listOf("noel", "no"),
        "nu-13" to listOf("nu", "ny"),
        "platinum the trinity" to listOf("plat", "platinum", "pt"),
        "rachel alucard" to listOf("rachel", "rc"),
        "ragna the bloodedge" to listOf("ragna", "rg"),
        "relius clover" to listOf("relius", "rl"),
        "susanoo" to listOf("susano", "susanoo", "su"),
        "taokaka" to listOf("tao", "tk"),
        "tsubaki yayoi" to listOf("tsubaki", "tb"),
        "valkenhayn r. hellsing" to listOf("valk", "valkenhayn", "vh"),
        "yuuki terumi" to listOf("terumi", "yuuki", "tm"),
    )

    val fullName = this
        .decodeHtmlEntities()
        .replace("'", "")
        .lowercase()
    val words = fullName.split(' ', '-')
    val firstName = words.first()

    val code = bbCodeMap[fullName]

    return buildList {
        if (words.size > 1) add(firstName)
        code?.let { addAll(it) }
    }.distinct()
}

internal fun String?.formUmo(): List<String> {
    if (isNullOrBlank()) return listOf()

    return orEmpty().split(",").mapNotNull {
        it.trim().toClickable()
    }
}

private fun List<CargoQueryItem>.filterAltModeCharacters(): List<CargoQueryItem> {
    val filtered = this.filterNot { it.title.name.orEmpty().contains("(") }
    return filtered
}
