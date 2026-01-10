package io.github.sophon.discord.featureRegistry.wikiMizuumi

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField

class CreateMizuumiMoveEmbedUseCase {
    private val TEAL = 0x0007A9F5

    fun invoke(
        move: Move,
        game: Game,
        featureInfo: FeatureInfo,
    ): Pair<EmbedBuilder.() -> Unit, (EmbedBuilder.() -> Unit)?> {
        return when (game) {
            Game.MBTL -> {
                createPrimaryEmbedBuilder(move, featureInfo) to null
            }
            Game.Uni2 -> {
                val primary = createPrimaryEmbedBuilder(move, featureInfo)
                primary to null
            }
            else -> {
                val empty: EmbedBuilder.() -> Unit = {}
                empty to null
            }
        }
    }


    private fun createPrimaryEmbedBuilder(
        move: Move,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        generalInfo(move).invoke(this)

        mandatoryField(name = "Startup", value = move.startup)
        mandatoryField(name = "Cancel", value = move.cancel)
        mandatoryField(name = "Block", value = move.onBlock)
        mandatoryField(name = "Active", value = move.active)
        mandatoryField(name = "Guard", value = move.guard)
        mandatoryField(name = "Recovery", value = move.recovery)

        optionalField(name = "Damage", value = move.damage, escapeAsterisks = true)
        optionalField(name = "Invul", value = move.invulnerability)

        move.mbProperties?.apply {
            optionalField(name = "Attribute", value = attribute)
            optionalField(name = "Property", value = property)
            optionalField(name = "Cost", value = move.mbProperties?.cost)
        }

        move.uni2Properties?.apply {
            optionalField(name = "Attribute", value = attribute)
            optionalField(name = "Property", value = property)
            optionalField(name = "Cost", value = move.mbProperties?.cost)
            optionalField(name = "Ass advantage", value = assaultAdv)
        }

        featureFooter(featureInfo)
    }

    @Deprecated("most data isn't filled anyway")
    private fun createFullBBEmbedBuilder(
        move: Move,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        createPrimaryEmbedBuilder(move, featureInfo).invoke(this)

        move.uni2Properties?.apply {
            if (airCH != null || groundCH != null || CHstop != null) {
                optionalField(name = "CH a-g-s", value = "$airCH - $groundCH - $CHstop")
            }
            if (airHit != null || groundHit != null || hitstop != null) {
                optionalField(name = "Hit a-g-s", value = "$airHit - $groundHit - $hitstop")
            }
            optionalField(name = "Cancel window", value = cancelWindow)
            optionalField(name = "Landing", value = landing)
            optionalField(name = "Proration", value = proration)
            if (comboP1 != null || comboP2 != null) {
                optionalField(name = "Combo P1-P2", value = "$comboP1 - $comboP2")
            }
        }
    }

    private fun generalInfo(move: Move): EmbedBuilder.() -> Unit = {
        title = move.input
        url = move.urls.wikiUrl
        description = when {
            move.charName.isBlank() -> "Move data"
            move.name.isNullOrBlank() -> "**${move.charName}**"
            else -> "**${move.charName}**: ${move.name}"
        }
        color = Color(TEAL)
        move.urls.characterImage?.let { thumbnail { url = it } }

        val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
            ?: emptyList()

        images
            .takeIf { it.size == 1 }
            ?.let { image = it.first() }
    }
}