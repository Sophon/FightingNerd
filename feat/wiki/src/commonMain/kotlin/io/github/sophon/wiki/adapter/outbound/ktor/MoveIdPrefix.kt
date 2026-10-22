package io.github.sophon.wiki.adapter.outbound.ktor

/**
 * The character part of one character's move IDs - the longest `_`-terminated prefix shared by more than half of them
 * and by at least two, ignoring case: `general_shao_d2` → `general_shao_`, `LE_214HP` → `le_`.
 * A prefix with a digit is part of the move, not the character - `ak_5b_close` / `ak_5b_far` keep `5b_`.
 * Empty when there's none.
 */
internal fun List<String>.findMoveIdPrefix(): String {
    val prefixCountMap = this
        .flatMap { moveId -> moveId.lowercase().underscorePrefixes() }
        .filter { prefix -> prefix.none { it.isDigit() } }
        .groupingBy { it }
        .eachCount()
    val prefix = prefixCountMap
        .filterValues { count -> (count >= MIN_PREFIX_COUNT) && ((count * 2) > size) }
        .keys
        .maxByOrNull { it.length }
        .orEmpty()
    return prefix
}

/**
 * An ID without the prefix stays whole - Lei-Lei's `tenraiha_anvil` among her `LE_` moves.
 */
internal fun String.removeMoveIdPrefix(prefix: String): String {
    val isPrefixed = (prefix.isNotEmpty() && (length > prefix.length) && startsWith(prefix, ignoreCase = true))
    val moveIdWithoutPrefix = if (isPrefixed) {
        drop(prefix.length)
    } else {
        this
    }
    return moveIdWithoutPrefix
}

private fun String.underscorePrefixes(): List<String> {
    val prefixes = indices
        .filter { index -> this[index] == '_' }
        .map { index -> substring(0, index + 1) }
    return prefixes
}


private const val MIN_PREFIX_COUNT = 2
