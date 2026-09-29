package io.github.sophon.wiki.application.domain.util

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId

/**
 * The id is the lowercase `remoteQueryId` with spaces as `_` (`Armor King` → `armor_king`).
 * The display name becomes a lowercase alias too; the display name itself stays for the UI.
 */
internal fun Character.normalize(): Character {
    val normalizedId = remoteQueryId
        .trim()
        .replace(" ", "_")
        .lowercase()
    val normalizedAliases = (aliasList + displayName)
        .map { alias -> alias.trim().lowercase() }
        .filter { alias -> alias.isNotEmpty() }
        .distinct()

    val normalized = copy(
        id = CharacterId(normalizedId),
        aliasList = normalizedAliases,
    )
    return normalized
}
