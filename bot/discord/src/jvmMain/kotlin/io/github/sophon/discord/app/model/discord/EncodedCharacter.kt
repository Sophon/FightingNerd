package io.github.sophon.discord.app.model.discord

import io.github.sophon.discord.AUTOCOMPLETE_VALUE_DELIMITER

data class EncodedCharacter(
    val characterId: String,
    val gameId: String,
) {
    fun encode(): String {
        val encoded = "$characterId$AUTOCOMPLETE_VALUE_DELIMITER$gameId"
        return encoded
    }

    companion object {
        /**
         * Null for a plain query - the character wasn't picked from autocomplete.
         */
        fun decode(value: String): EncodedCharacter? {
            val (characterId, gameId) = value
                .split(AUTOCOMPLETE_VALUE_DELIMITER, limit = 2)
                .takeIf { it.size == 2 }
                ?: return null

            val choiceValue = EncodedCharacter(characterId = characterId, gameId = gameId)
            return choiceValue
        }
    }
}
