package io.github.sophon.wiki.application.domain.util

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.wiki.model.Move

/**
 * The id is the character's id and the normalized input.
 */
internal fun Move.normalizeDustLoop(game: Game): Move {
    val normalizedInput = input.normalize2dInputs()
    val normalizedAliases = (aliases + normalizedInput.formAliases(game, characterId)).distinct()

    val normalized = copy(
        id = "${characterId}_$normalizedInput",
        input = normalizedInput,
        aliases = normalizedAliases,
    )
    return normalized
}

private fun String.formAliases(
    game: Game,
    characterId: String,
): List<String> {
    val aliases = when {
        characterId == NAGORIYUKI_ID -> formNagoriyukiAliases()
        game == Game.GBVSR -> createGbvsAliases()
        else -> create2dAliases(isPartial = false)
    }
        .addAliasForReleaseNotation(this)
        .distinct()
    return aliases
}

/**
 * Nagoriyuki's moves carry a blood level - `2hlevel1`, `2slevel3`, `2hlevelbr`.
 */
private fun String.formNagoriyukiAliases(): List<String> {
    val aliases = when {
        contains("levelbr", ignoreCase = true) -> listOf(replace("levelbr", "b", ignoreCase = true).lowercase())
        contains("level1", ignoreCase = true) -> listOf(replace("level1", "", ignoreCase = true).lowercase())
        contains("level", ignoreCase = true) -> listOf(replace("level", "", ignoreCase = true).lowercase())
        else -> emptyList()
    }
    return aliases
}

private fun String.createGbvsAliases(): List<String> {
    val aliases = buildList {
        addAll(create2dAliases(isPartial = true))
        addAll(createNarmayaStanceAliases())
    }
    return aliases
}

/**
 * Narmaya's bracketed stance suffix also becomes a prefix - `fh` in K stance gets `k.fh`.
 */
private fun String.createNarmayaStanceAliases(): List<String> {
    val regex = """^(.+)\[([^]]+)]$""".toRegex()
    val match = regex.find(this) ?: return emptyList()
    val (base, suffix) = match.destructured

    val aliases = listOf("${suffix.lowercase()}.${base.lowercase()}")
    return aliases
}

/**
 * Release inputs (`214]p[`) also match without the brackets (`214p`).
 */
private fun List<String>.addAliasForReleaseNotation(input: String): List<String> {
    val regex = Regex("""\]([a-zA-Z])\[""")

    val aliases = if (regex.containsMatchIn(input)) {
        this + regex.replace(input, "$1")
    } else {
        this
    }
    return aliases
}


private const val NAGORIYUKI_ID = "nagoriyuki"
