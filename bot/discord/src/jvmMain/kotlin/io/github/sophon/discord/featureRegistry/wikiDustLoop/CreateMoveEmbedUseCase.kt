package io.github.sophon.discord.featureRegistry.wikiDustLoop

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.util.getLevel
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField

internal class CreateMoveEmbedUseCase {
    private val red = 0x00950117

    fun invoke(
        move: Move,
        game: Game,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        when (game) {
            Game.GGST -> createGGEmbed(move, featureInfo)
            Game.DBFZ -> createDBEmbed(move, featureInfo)
            Game.GBVSR -> createGBEmbed(move, featureInfo)
            Game.BBCF -> createBBEmbed(move, featureInfo)
            else -> {}
        }
    }

    private fun EmbedBuilder.createGGEmbed(move: Move, featureInfo: FeatureInfo) {
        generalInfo(move)
        generalProperties(move)

        optionalField(name = "Risc gain", value = move.ggstProperties?.riscGain)
        optionalField(name = "Risc loss", value = move.ggstProperties?.riscLoss)
        optionalField(name = "Cancel", value = move.ggstProperties?.cancel)
        optionalField(name = "Prorate", value = move.ggstProperties?.prorate)
        optionalField(name = "Input tension", value = move.ggstProperties?.inputTension)
        optionalField(name = "Chip", value = move.ggstProperties?.chipRatio)

        createNotes(move)

        featureFooter(featureInfo)
    }

    private fun EmbedBuilder.createDBEmbed(move: Move, featureInfo: FeatureInfo) {
        generalInfo(move)

        mandatoryField(name = "Startup", value = move.startup)
        mandatoryField(name = "Active", value = move.active)
        mandatoryField(name = "OB", value = move.onBlock)
        mandatoryField(name = "Guard", value = move.guard)

        mandatoryField(name = "Invul", value = move.invulnerability)
        mandatoryField(name = "Smash", value = move.dbfzProperties?.smash)

        createNotes(move)

        featureFooter(featureInfo)
    }

    private fun EmbedBuilder.createGBEmbed(move: Move, featureInfo: FeatureInfo) {
        generalInfo(move)
        generalProperties(move)

        optionalField(name = "Meter", value = move.gbvsrProperties?.meter)
        optionalField(name = "LVL", value = move.gbvsrProperties?.level)
        optionalField(name = "CD", value = move.gbvsrProperties?.cooldown)
        optionalField(name = "CLS", value = move.gbvsrProperties?.cls)
        optionalField(name = "Type", value = move.gbvsrProperties?.type)

        createNotes(move)

        featureFooter(featureInfo)
    }

    private fun EmbedBuilder.createBBEmbed(move: Move, featureInfo: FeatureInfo) {
        generalInfo(move)
        generalProperties(move)

        move.bbProperties?.apply {
            if (p1 != null || p2 != null) {
                mandatoryField(
                    name = "Prorate",
                    value = "$p1 - $p2"
                )
            }
            optionalField(name = "OD", value = onODR)
            optionalField(
                name = "Hit",
                value = "gnd; air; stp\n" +
                        "$groundHit; $airHit; $hitstop"
            )
            optionalField(
                name = "CH",
                value = "gnd; air; stp\n" +
                        "$groundCH; $airCH; $chStop"
            )
            optionalField(name = "Attribute", value = attribute)

            if (blockstun != null || blockstop != null) {
                optionalField(
                    name = "Block",
                    value = "stn: $blockstun | stp: $blockstop",
                )
            }
        }

        createNotes(move)

        featureFooter(featureInfo)
    }

    private fun EmbedBuilder.generalProperties(move: Move) {
        mandatoryField(name = "Startup", value = move.startup)
        mandatoryField(name = "Hit", value = move.onHit)
        mandatoryField(name = "Block", value = move.onBlock)
        mandatoryField(name = "Active", value = move.active)
        mandatoryField(name = "Guard", value = move.guard)
        mandatoryField(name = "Recovery", value = move.recovery)

        optionalField(name = "Damage", value = move.damage, escapeAsterisks = true)
        optionalField(name = "Invulnerability", value = move.invulnerability)
        optionalField(name = "Counter", value = move.onCH)

        optionalField(name = "Level", value = move.getLevel())
    }

    private fun EmbedBuilder.generalInfo(move: Move) {
        title = move.input
        url = move.urls.wikiUrl
        description = if (move.name.isNullOrBlank()) {
            "**${move.charName}**"
        } else {
            "**${move.charName}**: ${move.name.orEmpty()}"
        }
        this@generalInfo.color = Color(this@CreateMoveEmbedUseCase.red)
        move.urls.characterImage?.let { thumbnail { url = it } }

        val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
            ?: emptyList()

        images
            .takeIf { it.size == 1 }
            ?.let { image = it.first() }
    }

    private fun EmbedBuilder.createNotes(move: Move) = optionalField(
        name = "📝 NOTES",
        value = move.notes
            .joinToString(separator = "") { note -> "* $note\n" },
        inline = false,
    )
}