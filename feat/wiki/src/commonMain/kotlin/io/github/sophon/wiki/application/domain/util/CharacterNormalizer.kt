package io.github.sophon.wiki.application.domain.util

import io.github.sophon.wiki.application.domain.model.Character

/**
 * The natural id is the lowercase `remoteQueryId` with spaces as `_` (`Armor King` → `armor_king`); the game stays.
 * The display name becomes a lowercase alias too; the display name itself stays for the UI.
 */
internal fun Character.normalize(): Character {
    val normalizedNaturalId = remoteQueryId
        .trim()
        .replace(" ", "_")
        .lowercase()
    val normalizedAliases = (aliasList + displayName)
        .map { alias -> alias.trim().lowercase() }
        .filter { alias -> alias.isNotEmpty() }
        .distinct()

    val normalized = copy(
        id = id.copy(naturalId = normalizedNaturalId),
        aliasList = normalizedAliases,
    )
    return normalized
}
