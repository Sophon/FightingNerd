package io.github.sophon.discord.feat.core.usecase

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.EMBED_LIST_MIN_COLUMN
import io.github.sophon.discord.EMBED_LIST_PER_COLUMN
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import kotlinx.coroutines.flow.first

@ExcludeFromCoverage("UI")
internal class CreateCharacterAliasesEmbedUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        featureInfo: FeatureInfo,
        colorCode: Int,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        val embed = wiki.subscribeToCharacterList()
            .first()
            .let { characterList ->
                createAliasesEmbed(characterList, featureInfo, colorCode)
            }
        return Result.Success(embed)
    }

    private fun createAliasesEmbed(
        characterList: List<Character>,
        featureInfo: FeatureInfo,
        colorCode: Int,
    ): EmbedBuilder.() -> Unit = {
        color = Color(colorCode)

        val aliasList = characterList
            .filter { it.aliasList.isNotEmpty() }
            .sortedBy { it.displayName }
            .mapIndexed { index, character ->
                val aliases = character.aliasList.joinToString(", ")
                "${index + 1}. **${character.displayName}** → $aliases"
            }

        val embedData = when (aliasList.size) {
            in 0..EMBED_LIST_MIN_COLUMN -> {
                listOf(aliasList.joinToString("\n"))
            }
            in EMBED_LIST_MIN_COLUMN..EMBED_LIST_PER_COLUMN -> {
                val midpoint = (aliasList.size + 1) / 2
                listOf(
                    aliasList.take(midpoint).joinToString("\n"),
                    aliasList.drop(midpoint).joinToString("\n")
                )
            }
            else -> {
                val midpoint = (aliasList.size + 1) / 2
                val column1 = aliasList.take(midpoint).chunked(EMBED_LIST_PER_COLUMN)
                val column2 = aliasList.drop(midpoint).chunked(EMBED_LIST_PER_COLUMN)

                // Interleave chunks: [col1_chunk1, col2_chunk1, col1_chunk2, col2_chunk2, ...]
                column1.zip(column2).flatMap { (col1, col2) ->
                    listOf(col1.joinToString("\n"), col2.joinToString("\n"))
                }
            }
        }

        embedData.forEachIndexed { index, data ->
            mandatoryField(
                name = if (index == 0) "🥸 CHARACTER ALIASES" else "_",
                value = data,
                inline = when {
                    aliasList.size <= 10 -> false
                    else -> true
                }
            )
        }

        featureFooter(featureInfo)
    }
}