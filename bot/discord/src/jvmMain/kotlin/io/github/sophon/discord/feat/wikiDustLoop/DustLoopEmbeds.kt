package io.github.sophon.discord.feat.wikiDustLoop

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.toColumns
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.hitboxImages
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.moveEmbedDescription
import io.github.sophon.discord.util.optionalField
import io.github.sophon.wikidustloop.integration.getLevel
import io.github.sophon.wikidustloop.integration.model.BBMoveProperties
import io.github.sophon.wikidustloop.integration.model.BBProperties
import io.github.sophon.wikidustloop.integration.model.DBFZMoveProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRMoveProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRProperties
import io.github.sophon.wikidustloop.integration.model.GGSTMoveProperties
import io.github.sophon.wikidustloop.integration.model.GGSTProperties
import io.github.sophon.wikidustloop.integration.model.MTFSProperties

internal fun charEmbedBuilder(
    game: Game,
    character: Character,
    fastestMoveList: List<Move>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoChar(character)
    generalPropertiesChar(character, fastestMoveList)

    when (game) {
        Game.GGST -> charDetailsGG(character)
        Game.GBVSR -> charDetailsGB(character)
        Game.BBCF -> charDetailsBB(character)
        Game.MTFS -> charDetailsMT(character)
        else -> {}
    }

    featureFooter(featureInfo)
}

internal fun moveEmbedBuilder(
    game: Game,
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoMove(character, move, displayHitboxes = false)

    when (game) {
        Game.DBFZ -> movePropertiesDB(move)
        Game.GBVSR -> movePropertiesGB(move)
        else -> generalPropertiesMove(move)
    }

    featureFooter(featureInfo)
}

internal fun detailedMoveEmbedBuilder(
    game: Game,
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    moveEmbedBuilder(game, character, move, featureInfo).invoke(this)

    when (game) {
        Game.GGST -> moveDetailedEmbedBuilderGG(move)
        Game.BBCF -> moveDetailedEmbedBuilderBB(move)
        else -> {}
    }
}

internal fun dustLoopMoveListEmbedBuilder(
    charName: String,
    category: String,
    moveList: List<Move>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    color = Color(RED)

    val formatted = moveList
        .mapNotNull { move ->
            val startup = move.startup
                ?.takeWhile { it.isDigit() }
                ?.toIntOrNull()
            startup?.let { it to move }
        }
        .sortedBy { (startup, _) -> startup }
        .groupBy(
            keySelector = { (startup, _) -> startup },
            valueTransform = { (_, move) -> move }
        )
        .map { (startup, moveList) ->
            "- **${startup}f**: " + moveList.joinToString(", ") {
                it.input
            }
        }

    mandatoryField(
        name = "$charName $category moves",
        value = "",
        inline = false,
    )

    formatted
        .toColumns()
        .forEach { moveList ->
            val text = moveList.joinToString("\n")
            mandatoryField(
                name = "",
                value = text,
            )
        }

    featureFooter(featureInfo)
}


private fun EmbedBuilder.generalInfoChar(character: Character) {
    title = character.displayName
    url = character.wikiUrl
    color = Color(RED)

    character.images?.iconUrl?.let { iconUrl ->
        thumbnail { url = iconUrl }
    }
}

private fun EmbedBuilder.generalPropertiesChar(
    character: Character,
    fastestMoveList: List<Move>,
) {
    mandatoryField(
        name = "",
        value = character.aliasList.joinToString(", "),
        inline = false,
    )

    optionalField(
        name = "HP",
        value = character.hp,
    )

    if (character.umo.isEmpty().not()) {
        optionalField(
            name = "UMO",
            value = character.umo.joinToString(", "),
        )
    }

    val moves = fastestMoveList.joinToString(", ") { it.input }
    val startup = fastestMoveList.first().startup.orDash()
    mandatoryField(
        name = "Fastest normal",
        value = "$startup: $moves",
        inline = false,
    )
}

private fun EmbedBuilder.charDetailsGG(character: Character) {
    val properties = (character.gameProperties as? GGSTProperties) ?: return

    mandatoryField(
        name = "⭐️ CORE",
        value = buildList {
            add("* **Defense →** ${properties?.defense}")
            add("* **Guts →** ${properties?.guts}")
            add("* **Guard balance →** ${properties?.guardBalance}")
            add("* **Boost ATT | DEF** → ${properties?.boostAttack} | ${properties?.boostDefense}")
        }.joinToString("\n"),
    )

    mandatoryField(
        name = "👟 MOVEMENT",
        value = buildList {
            character.umo.takeIf { it.isNotEmpty() }?.let { umo ->
                if (umo.size == 1) {
                    add("* **Unique movement →** ${umo.first()}")
                } else {
                    add("* **Unique movement →** ")
                    umo.forEach { add("   * $it") }
                }
            }
            add("* **Backdash →** ${properties?.bwdDash}")
            add("   * **Distance →** ${properties?.bwdDashDist}")
            add("   * **Duration →** ${properties?.bwdDashDuration}")
            add("   * **Invulnerability →** ${properties?.bwdDashInvulnerability}")
            properties?.fwdDash?.let { add("* **Forward dash →** $it") }
            add("* **Initial speed →** ${properties?.dashInitialSpd}")
            properties?.dashAcceleration?.let { add("* **Acceleration →** $it") }
            properties?.movementTension?.let { add("* **Tension →** $it") }
            properties?.dashFriction?.let { add("* **Friction →** $it") }
            add("* **Walk →** ← ${properties?.walkSpd} | ${properties?.bwdWalkSpd} →")
        }.joinToString("\n"),
    )

    mandatoryField(
        name = "🦘 JUMP",
        value = buildList {
            add("* **Prejump →** ${properties?.prejump}")
            add("* **Height (high) →** ${properties?.jumpHeight} (${properties?.highJumpHeight})")
            add("* **Duration (high) →** ${properties?.jumpDuration} (${properties?.highJumpDuration})")
            add("* **Gravity (high) →** ${properties?.jumpGravity} (${properties?.highJumpGravity})")
            properties?.jumpTension?.let { add("* **Tension →** $it") }
        }.joinToString("\n"),
    )

    mandatoryField(
        name = "💨 AIRDASH",
        value = buildList {
            add("* **IAD →** ${properties?.earliestIAD}")
            add("* **Distance | Duration →** ${properties?.adDist} | ${properties?.adDuration}")
            add("* **B Distance | Duration →** ${properties?.abdDist} | ${properties?.abdDuration}")
            properties?.airDashTension?.let { add("* **Tension →** $it") }
        }.joinToString("\n"),
    )
}

private fun EmbedBuilder.charDetailsGB(character: Character) {
    val properties = (character.gameProperties as? GBVSRProperties) ?: return

    optionalField(name = "Prejump", value = properties.jump?.pre)
    optionalField(name = "Backdash", value = properties.backdash)
}

private fun EmbedBuilder.charDetailsBB(character: Character) {
    val properties = (character.gameProperties as? BBProperties) ?: return

    mandatoryField(
        name = "Dash",
        value = "Forward: ${properties.forwardDash}\n" +
                "Back: ${properties.backDash}"
    )

    mandatoryField(name = "Prejump", value = properties.preJump)
}

private fun EmbedBuilder.charDetailsMT(character: Character) {
    val properties = (character.gameProperties as? MTFSProperties) ?: return

    optionalField(name = "Team", value = properties.team)
    optionalField(name = "Prejump", value = properties.prejump)
    optionalField(name = "Backdash", value = properties.backdash)
}


private fun EmbedBuilder.generalInfoMove(
    character: Character,
    move: Move,
    displayHitboxes: Boolean = true,
) {
    title = move.input
    url = move.urls.wikiUrl
    moveEmbedDescription(character, move)
    this@generalInfoMove.color = Color(RED)
    character.images?.iconUrl?.let { thumbnail { url = it } }

    if (displayHitboxes) {
        val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
            ?: emptyList()
        images
            .takeIf { it.size == 1 }
            ?.let { image = it.first() }
    }
}

private fun EmbedBuilder.generalPropertiesMove(move: Move) {
    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Hit", value = move.onHit)
    mandatoryField(name = "Block", value = move.onBlock)
    mandatoryField(name = "Active", value = move.active)
    mandatoryField(name = "Guard", value = move.guard)
    mandatoryField(name = "Recovery", value = move.recovery)

    optionalField(name = "Damage", value = move.damage, escapeAsterisks = true)
    optionalField(name = "Inv", value = move.invulnerability)
    optionalField(name = "Counter", value = move.onCH)

    optionalField(name = "Level", value = move.getLevel())
}

private fun EmbedBuilder.moveNotes(move: Move) = optionalField(
    name = "📝 NOTES",
    value = move.notes
        .joinToString(separator = "") { note -> "* $note\n" },
    inline = false,
)

private fun EmbedBuilder.moveDetailedEmbedBuilderGG(move: Move) {
    val properties = (move.gameProperties as? GGSTMoveProperties) ?: return

    optionalField(name = "Risc gain", value = properties.riscGain)
    optionalField(name = "Risc loss", value = properties.riscLoss)
    optionalField(name = "Cancel", value = move.cancel)
    optionalField(name = "Prorate", value = properties.prorate)
    optionalField(name = "Input tension", value = properties.inputTension)
    optionalField(name = "Chip", value = properties.chipRatio)

    hitboxImages(move.urls).invoke(this)

    moveNotes(move)
}

private fun EmbedBuilder.moveDetailedEmbedBuilderBB(move: Move) {
    (move.gameProperties as? BBMoveProperties)?.apply {
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

    hitboxImages(move.urls).invoke(this)

    moveNotes(move)
}

private fun EmbedBuilder.movePropertiesDB(move: Move) {
    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Active", value = move.active)
    mandatoryField(name = "OB", value = move.onBlock)
    mandatoryField(name = "Guard", value = move.guard)

    mandatoryField(name = "Invul", value = move.invulnerability)
    mandatoryField(name = "Smash", value = (move.gameProperties as? DBFZMoveProperties)?.smash)

    moveNotes(move)
}

private fun EmbedBuilder.movePropertiesGB(move: Move) {
    generalPropertiesMove(move)

    val properties = (move.gameProperties as? GBVSRMoveProperties) ?: return
    optionalField(name = "Meter", value = properties.meter)
    optionalField(name = "LVL", value = properties.level)
    optionalField(name = "CD", value = properties.cooldown)
    optionalField(name = "CLS", value = properties.cls)
    optionalField(name = "Type", value = properties.type)

    moveNotes(move)
}


private const val RED = 0x00950117