package io.github.sophon.discord.usecase

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.chunkByNewLines
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.discord.BotError
import io.github.sophon.discord.MAX_LENGTH_EMBED
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField

internal class CreateCharacterAliasesEmbedUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        featureInfo: FeatureInfo,
        colorCode: Int,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return wiki.fetchCharacterList()
            .mapError { it.toDomainError() }
            .map { characterList ->
                createAliasesEmbed(characterList, featureInfo, colorCode)
            }
    }

    private fun createAliasesEmbed(
        characterList: List<Character>,
        featureInfo: FeatureInfo,
        colorCode: Int,
    ): EmbedBuilder.() -> Unit = {
        color = Color(colorCode)

        val string = buildString {
            characterList
                .filter { it.aliasList.isNotEmpty() }
                .sortedBy { it.displayName }
                .forEach { character ->
                    val aliases = character.aliasList.joinToString(", ")
                    append("- **${character.displayName}** → $aliases\n")
                }
        }
        val embedData = string.chunkByNewLines(delimiter = "\n", maxLength = MAX_LENGTH_EMBED)

        embedData.forEachIndexed { index, data ->
            mandatoryField(
                name = if (index == 0) "🥸 CHARACTER ALIASES" else "",
                value = data,
            )
        }

        featureFooter(featureInfo)
    }
}