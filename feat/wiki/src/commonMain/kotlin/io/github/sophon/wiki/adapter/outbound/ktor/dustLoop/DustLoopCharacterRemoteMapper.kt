package io.github.sophon.wiki.adapter.outbound.ktor.dustLoop

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.toClickable
import io.github.sophon.core.util.urlEncode
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.gameProperties.BBCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.DBFZCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GBVSRCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GGCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MTFSCharProperties
import io.github.sophon.wiki.application.domain.model.wiki.Game

internal fun DustLoopCharacterListResponseDto.toDomain(
    game: Game,
    imageUrlMap: Map<String, String>,
): List<Character> {
    val characterList = cargoQuery
        .filterAltModeCharacters(game)
        .map { query -> query.title.toDomain(game, imageUrlMap) }
    return characterList
}

private fun CharacterDto.toDomain(
    game: Game,
    imageUrlMap: Map<String, String>,
): Character {
    val queryName = name?.cleanHtml().formCharacterQueryName(game)

    val character = Character(
        id = CharacterId(game, queryName),
        displayName = name?.cleanHtml().orEmpty(),
        remoteQueryId = queryName,
        wikiUrl = queryName.formWikiUrl(game),
        aliasList = name.createAliases(game, aliases),
        images = Character.Images(
            iconId = icon,
            iconUrl = icon.let { imageUrlMap[it] },
            bannerUrl = portrait.let { imageUrlMap[it] },
        ),
        hp = health?.cleanHtml(),
        umo = umo.formUmo(),
        gameProperties = toGameProperties(game),
    )
    return character
}

@Suppress("LongMethod")
private fun CharacterDto.toGameProperties(game: Game): CharacterGameProperties? {
    val properties = when (game) {
        Game.GGST -> GGCharProperties(
            defense = defense,
            guts = guts,
            guardBalance = guardBalance,
            prejump = prejump,
            bwdDash = backdash,
            bwdDashDuration = backdashDuration,
            bwdDashInvulnerability = backdashInvuln,
            bwdDashAirborne = backdashAirborne,
            bwdDashDist = backdashDistance,
            fwdDash = forwardDash,
            jumpDuration = jumpDuration,
            highJumpDuration = highJumpDuration,
            jumpHeight = jumpHeight,
            highJumpHeight = highJumpHeight,
            earliestIAD = earliestIad,
            adDuration = adDuration,
            abdDuration = abdDuration,
            adDist = adDistance,
            abdDist = abdDistance,
            movementTension = movementTension,
            jumpTension = jumpTension,
            airDashTension = airDashTension,
            walkSpd = walkSpeed,
            bwdWalkSpd = backWalkSpeed,
            dashInitialSpd = dashInitialSpeed,
            dashAcceleration = dashAcceleration,
            dashFriction = dashFriction,
            jumpGravity = jumpGravity,
            highJumpGravity = highJumpGravity,
            boostAttack = boostAttack,
            boostDefense = boostDefense,
        )

        Game.BBCF -> BBCharProperties(
            preJump = prejump?.cleanHtml(),
            backDash = backdash?.cleanHtml(),
            forwardDash = forwardDash?.cleanHtml(),
        )

        Game.MTFS -> MTFSCharProperties(
            prejump = prejump?.cleanHtml(),
            backdash = backdash?.cleanHtml(),
            team = team?.cleanHtml(),
        )

        Game.GBVSR -> GBVSRCharProperties(
            jump = GBVSRCharProperties.Jump(
                pre = prejump,
                forwardDistance = f_jump_distance?.toString(),
                superForwardDistance = f_superjump_distance?.toString(),
                backDistance = b_jump_distance?.toString(),
                superBackDistance = b_superjump_distance?.toString(),
                gravity = jumpGravity,
                superGravity = superjump_gravity?.toString(),
                superHeight = superjump_height?.toString(),
            ),
            backdash = backdash?.cleanHtml(),
            walkSpeed = walk_speed?.toString(),
            walkSpeedBack = backwalk_speed?.toString(),
            dashInitial = dash_initial_speed?.toString(),
            dashAcceleration = dash_acceleration,
            closeRange = GBVSRCharProperties.CloseRange(
                l = close_l_range?.toString(),
                m = close_m_range?.toString(),
                h = close_h_range?.toString(),
            ),
        )

        Game.DBFZ -> DBFZCharProperties(
            kiMod = kimod?.cleanHtml(),
        )

        else -> null
    }
    return properties
}

private fun String?.formCharacterQueryName(game: Game): String {
    val queryName = this
        .orEmpty()
        .replace("?", "")
        .let { if (game == Game.BBCF) it else it.replace("'", "") }
    return queryName
}

private fun String.formWikiUrl(game: Game): String {
    val formatted = this
        .replace(" ", "_")
        .decodeHtmlEntities()
        .urlEncode()
    val url = "${game.wikiUrl}/$formatted"
    return url
}

private fun String?.createAliases(
    game: Game,
    dtoAliases: String?,
): List<String> {
    if (this == null) return listOf()

    val aliases = if (dtoAliases == null) {
        when (game) {
            Game.GGST -> this.createGGAliases()
            Game.GBVSR -> listOf(this.substringBefore(" ").lowercase())
            Game.BBCF -> this.createBBAliases()
            else -> listOf()
        }
    } else {
        dtoAliases
            .split(";", ",")
            .map { it.replace(" ", "").lowercase() }
    }
    return aliases
}

private fun String.createGGAliases(): List<String> {
    val original = this.lowercase()
    if (original.isBlank()) return emptyList()

    val aliases = buildList {
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
        if (words.size > 1) add(words.joinToString("") { it.first().toString() })
    }.distinct()
    return aliases
}

private fun String.createBBAliases(): List<String> {
    if (this.isBlank()) return emptyList()

    val fullName = this
        .decodeHtmlEntities()
        .replace("'", "")
        .lowercase()
    val words = fullName.split(' ', '-')
    val firstName = words.first()
    val code = bbCodeMap[fullName]

    val aliases = buildList {
        if (words.size > 1) add(firstName)
        code?.let { addAll(it) }
    }.distinct()
    return aliases
}

private fun String?.formUmo(): List<String> {
    if (isNullOrBlank()) return listOf()

    val umoList = split(Regex(""",|<br\s*/?>"""))
        .map { it.trim() }
        .map { part ->
            when {
                part.startsWith("<span") -> part.extractTooltipLabel()
                part.startsWith("[[") -> part.toClickable(WIKI_BASE_URL).orEmpty()
                else -> part
            }
        }
        .map { it.cleanHtml() }
        .filter { it.isNotBlank() }
    return umoList
}

private fun String.extractTooltipLabel(): String {
    val regex = """<span class="tooltip">([^<]+)""".toRegex()
    val label = regex.find(this)?.groupValues?.get(1).orEmpty()
    return label
}

/**
 * GGST lists alternate modes (`A.B.A (Jealous Rage)`) as separate characters.
 */
private fun List<CargoQueryItem>.filterAltModeCharacters(game: Game): List<CargoQueryItem> {
    val filtered = when (game) {
        Game.GGST,
        Game.GBVSR,
            -> this.filterNot { it.title.name.orEmpty().contains("(") }

        else -> this
    }
    return filtered
}


private val bbCodeMap = mapOf(
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
