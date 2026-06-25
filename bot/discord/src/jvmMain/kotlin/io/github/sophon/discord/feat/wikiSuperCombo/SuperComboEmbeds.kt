package io.github.sophon.discord.feat.wikiSuperCombo

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.domain.model.Emoji
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.discord.util.separator

internal fun superComboMoveEmbed(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = move.input
    url = move.urls.wikiUrl
    description = if (move.name.isNullOrBlank()) {
        "**${character.displayName}**"
    } else {
        "**${character.displayName}**: ${move.name.orEmpty()}"
    }
    color = Color(WHITE)
    character.images?.iconUrl?.let { thumbnail { url = it } }

    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Hit", value = move.onHit)
    mandatoryField(name = "Block", value = move.onBlock)
    mandatoryField(name = "Active", value = move.active)
    mandatoryField(name = "Guard", value = move.guard)
    mandatoryField(name = "Recovery", value = move.recovery)

    optionalField(name = "Damage", value = move.damage)
    optionalField(name = "Invul", value = move.invulnerability)

    featureFooter(featureInfo)
}

internal fun superComboMoveDetailedEmbed(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    superComboMoveEmbed(character, move, featureInfo).invoke(this)

    val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
        ?: emptyList()
    images
        .takeIf { it.size == 1 }
        ?.let { image = it.first() }

    sf6Fields(move)
    mk1Fields(move)

    separator()

    createNotes(move)
    createDetails(move)
}

internal fun superComboCharacterEmbed(
    character: Character,
    fastestMoveList: List<Move>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = character.displayName
    url = character.wikiUrl
    color = Color(WHITE)

    character.images?.iconUrl?.let { iconUrl ->
        thumbnail { url = iconUrl }
    }

    character.sf6Properties?.let { properties ->
        val moves = fastestMoveList.joinToString(", ") { move ->
            move.input
        }
        val startup = fastestMoveList.first().startup.orDash()

        mandatoryField(
            name = "BASIC",
            value = listOf(
                "* **Fastest normal ($startup)**: $moves",
                "* ❤️ **HP**: ${character.hp}",
                "* 🤝 **Throw range | hurtbox**: ${properties.throwRange} | ${properties.throwHurtbox}",
            ).joinToString("\n"),
            inline = false
        )

        mandatoryField(
            name = "DRIVE",
            value = buildString {
                appendLine("* **DR distance min**: ${properties.dRushMin}")
                appendLine("* **DR distance block**: ${properties.dRushBlock}")
                appendLine("* **DR distance max**: ${properties.dRushMax}")
            },
            inline = false,
        )

        mandatoryField(
            name = "MOVEMENT",
            value = buildString {
                appendLine("* **Walk speed**: ←${properties.bwdWalkSpd} | ${properties.fwdWalkSpd}→")
                appendLine("* **Dash speed**: ←${properties.bwdDashSpd} | ${properties.fwdDashSpd}→")
                appendLine("* **Dash distance**: ←${properties.bwdDashDist} | ${properties.fwdDashDist}→")
                appendLine("* **Jump distance**: ↖ ${properties.bwdJumpDist} | ${properties.fwdJumpDist} ↗")
                appendLine("* **Jump apex | speed**: ${properties.jumpApex} | ${properties.jumpSpd}")
            },
            inline = false,
        )
    }

    featureFooter(featureInfo)
}

private fun EmbedBuilder.sf6Fields(move: Move) {
    optionalField(name = "JUG start", value = move.sf6Properties?.jugStart)
    optionalField(name = "JUG limit", value = move.sf6Properties?.jugLimit)
    optionalField(name = "JUG inc", value = move.sf6Properties?.jugIncrease)

    optionalField(name = "Cancel", move.cancel)
    optionalField(name = "Range", move.sf6Properties?.attackRange)
    optionalField(name = "Proj spd", move.sf6Properties?.projectileSpeed)
}

private fun EmbedBuilder.mk1Fields(move: Move) {
    optionalField(
        name = "Cost",
        value = move.mkProperties?.cost?.joinToString("; ")
    )
    optionalField(
        name = "Chip",
        value = move.mkProperties?.chip,
    )
    optionalField(
        name = "Flawless block",
        value = move.mkProperties?.flawlessBlockAdv,
    )

    if (move.mkProperties?.hitCancelAdv != null || move.mkProperties?.blockCancelAdv != null) {
        optionalField(
            name = "Cancel hit | block",
            value = "${move.mkProperties?.hitCancelAdv} | ${move.mkProperties?.blockCancelAdv}",
        )
    }
    optionalField(
        name = "Punish",
        value = move.mkProperties?.punish,
    )
}

private fun EmbedBuilder.createNotes(move: Move) {
    return optionalField(
        name = "",
        value = move.notes
            .emojify()
            .joinToString(separator = "") { note -> "* $note\n" },
    )
}

private fun EmbedBuilder.createDetails(move: Move) {
    val properties = move.sf6Properties ?: return

    val hasDetails = properties.run {
        DROH != null || DROB != null
                || DRcOH != null || DRcOB != null
                || driveDmgOnHit != null || driveDmgOnBlock != null
                || driveGain != null
                || superGainOnHit != null || superGainOnBlock != null
    }
    if (hasDetails.not()) return

    mandatoryField(
        name = "",
        value = buildString {
            if (properties.DROH != null || properties.DROB != null) {
                appendLine("* **DR (OH | OB)**: ${properties.DROH.orDash()} | ${properties.DROB.orDash()}")
            }

            if (properties.DRcOH != null || properties.DRcOB != null) {
                appendLine("* **DRc (OH | OB)**: ${properties.DRcOH.orDash()} | ${properties.DRcOB.orDash()}")
            }

            if (properties.driveDmgOnHit != null || properties.driveDmgOnBlock != null) {
                appendLine("* **Drive damage (OH | OB)**: ${properties.driveDmgOnHit.orDash()} | ${properties.driveDmgOnBlock.orDash()}")
            }

            if (properties.driveGain != null) {
                appendLine("* **Drive gain**: ${properties.driveGain}")
            }

            if (properties.superGainOnHit != null || properties.superGainOnBlock != null) {
                append("* **SUP gain (OH | OB)**: ${properties.superGainOnHit.orDash()} | ${properties.superGainOnBlock.orDash()}")
            }
        }.trim(),
        inline = true,
    )
}

private fun List<String>.emojify(): List<String> {
    return buildList {
        this@emojify.forEach { note ->
            val emojified = buildString {
                if (note.contains("invincibility", ignoreCase = true)) append("🛡️ ")
                if (note.contains("juggle", ignoreCase = true)) append("🤹 ")
                if (note.contains("rhythm", ignoreCase = true)) append("🥁 ")
                if (note.contains("scaling", ignoreCase = true)) append("📉 ")
                if (note.contains("shimmy", ignoreCase = true)) append("💃 ")
                if (note.contains("throw", ignoreCase = true)) append(Emoji.THROW)
                append(note)
            }
            add(emojified)
        }
    }
}


private const val WHITE = 0x00FFFFFF