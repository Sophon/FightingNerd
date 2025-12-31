package io.github.sophon.discord.usecase

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.util.mandatoryField

internal class CreateCharacterAliasesEmbedUseCase {
    suspend fun invoke(wiki: WikiClient): Result<EmbedBuilder.() -> Unit, BotError> {
        return wiki.fetchCharacterList()
            .mapError { it.toDomainError() }
            .map { characterList ->
                createAliasesEmbed(characterList)
            }
    }

    private fun createAliasesEmbed(
        characterList: List<Character>,
    ): EmbedBuilder.() -> Unit = {
        val string = buildString {
            characterList
                .filter { it.aliasList.isNotEmpty() }
                .forEach { character ->
                    val aliases = character.aliasList.joinToString(", ")
                    append("- **${character.displayName}** → $aliases\n")
                }
        }

        mandatoryField(
            name = "🥸 CHARACTER ALIASES",
            value = string,
        )
    }
}