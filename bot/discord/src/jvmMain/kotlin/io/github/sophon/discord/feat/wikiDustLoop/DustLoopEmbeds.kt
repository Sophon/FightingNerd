package io.github.sophon.discord.feat.wikiDustLoop

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.util.getLevel
import io.github.sophon.discord.EMBED_LIST_PER_COLUMN
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.hitboxImages
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.moveEmbedDescription
import io.github.sophon.discord.util.optionalField

internal fun charEmbedBuilderGG(
    character: Character,
    fastestMoveList: List<Move>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoChar(character)

    val properties = character.ggstProperties

    generalPropertiesChar(character, fastestMoveList, null)

    mandatoryField(
        name = "⭐️ CORE",
        value = buildList {
            add("* **Defense →** ${properties?.defense}")
            add("* **Guts →** ${properties?.guts}")
            add("* **Guard balance →** ${properties?.guardBalance}")
            add("* **Boost ATT | DEF** → ${properties?.boostAttack} | ${properties?.boostDefense}")
        }.joinToString("\n"),
        inline = false,
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
        inline = false,
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
        inline = false,
    )

    mandatoryField(
        name = "💨 AIRDASH",
        value = buildList {
            add("* **IAD →** ${properties?.earliestIAD}")
            add("* **Distance | Duration →** ${properties?.adDist} | ${properties?.adDuration}")
            add("* **B Distance | Duration →** ${properties?.abdDist} | ${properties?.abdDuration}")
            properties?.airDashTension?.let { add("* **Tension →** $it") }
        }.joinToString("\n"),
        inline = false,
    )

    featureFooter(featureInfo)
}

internal fun charEmbedBuilderDB(
    character: Character,
    fastestMoveList: List<Move>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoChar(character)
    generalPropertiesChar(
        character,
        fastestMoveList,
        character.umo,
    )

    featureFooter(featureInfo)
}

internal fun charEmbedBuilderGB(
    character: Character,
    fastestMoveList: List<Move>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoChar(character)
    generalPropertiesChar(
        character,
        fastestMoveList,
        character.umo,
    )

    character.gbvsrProperties?.apply {
        optionalField(name = "Prejump", value = jump?.pre)
        optionalField(name = "Backdash", value = backdash)
    }

    featureFooter(featureInfo)
}

internal fun charEmbedBuilderBB(
    character: Character,
    fastestMoveList: List<Move>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoChar(character)
    generalPropertiesChar(
        character,
        fastestMoveList,
        character.umo,
    )

    character.bbProperties?.apply {
        mandatoryField(
            name = "Dash",
            value = "Forward: $forwardDash\n" +
                    "Back: $backDash"
        )

        mandatoryField(name = "Prejump", value = preJump)
    }

    featureFooter(featureInfo)
}

internal fun moveEmbedBuilderGG(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoMove(character, move, displayHitboxes = false)
    generalPropertiesMove(move)

    featureFooter(featureInfo)
}

internal fun moveDetailedEmbedBuilderGG(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    moveEmbedBuilderGG(character, move, featureInfo).invoke(this)

    optionalField(name = "Risc gain", value = move.ggstProperties?.riscGain)
    optionalField(name = "Risc loss", value = move.ggstProperties?.riscLoss)
    optionalField(name = "Cancel", value = move.cancel)
    optionalField(name = "Prorate", value = move.ggstProperties?.prorate)
    optionalField(name = "Input tension", value = move.ggstProperties?.inputTension)
    optionalField(name = "Chip", value = move.ggstProperties?.chipRatio)

    hitboxImages(move.urls).invoke(this)

    moveNotes(move)
}

internal fun moveEmbedBuilderDB(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoMove(character, move)

    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Active", value = move.active)
    mandatoryField(name = "OB", value = move.onBlock)
    mandatoryField(name = "Guard", value = move.guard)

    mandatoryField(name = "Invul", value = move.invulnerability)
    mandatoryField(name = "Smash", value = move.dbfzProperties?.smash)

    moveNotes(move)

    featureFooter(featureInfo)
}

internal fun moveEmbedBuilderGB(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoMove(character, move)
    generalPropertiesMove(move)

    optionalField(name = "Meter", value = move.gbvsrProperties?.meter)
    optionalField(name = "LVL", value = move.gbvsrProperties?.level)
    optionalField(name = "CD", value = move.gbvsrProperties?.cooldown)
    optionalField(name = "CLS", value = move.gbvsrProperties?.cls)
    optionalField(name = "Type", value = move.gbvsrProperties?.type)

    moveNotes(move)

    featureFooter(featureInfo)
}

internal fun moveEmbedBuilderBB(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    generalInfoMove(character, move, displayHitboxes = false)
    generalPropertiesMove(move)

    featureFooter(featureInfo)
}

internal fun moveDetailedEmbedBuilderBB(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    moveEmbedBuilderBB(character, move, featureInfo).invoke(this)

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

    hitboxImages(move.urls).invoke(this)

    moveNotes(move)
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

    formatted
        .chunked(EMBED_LIST_PER_COLUMN)
        .forEachIndexed { index, moveList ->
            val text = moveList.joinToString("\n")
            val name = if (index == 0 ) "$charName $category moves" else "_"
            mandatoryField(
                name = name,
                value = text,
                inline = false,
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
    umo: List<String>?,
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

    if (umo.isNullOrEmpty().not()) {
        optionalField(
            name = "UMO",
            value = umo.joinToString(", "),
        )
    }

    val moves = fastestMoveList.joinToString(", ") { it.input }
    val startup = fastestMoveList.first().startup.orDash()
    mandatoryField(
        name = "Fastest normal",
        value = "$startup: $moves"
    )
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


private const val RED = 0x00950117