package io.github.sophon.discord.featureRegistry.wikiDustLoop

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField

internal class CreateCharacterEmbedUseCase {
    private val red = 0x00950117

    fun invoke(
        character: Character,
        fastestMoveList: List<Move>,
        game: Game,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        when (game) {
            Game.GGST -> createGGEmbed(character, fastestMoveList, featureInfo)
            Game.DBFZ -> createDBEmbed(character, fastestMoveList, featureInfo)
            Game.GBVSR -> createGBEmbed(character, fastestMoveList, featureInfo)
            Game.BBCF -> createBBEmbed(character, fastestMoveList, featureInfo)
            else -> {}
        }
    }

    private fun EmbedBuilder.createGGEmbed(
        character: Character,
        fastestMoveList: List<Move>,
        featureInfo: FeatureInfo,
    ) {
        generalInfo(character)

        val properties = character.ggstProperties

        generalProperties(character, fastestMoveList, null)

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

    private fun EmbedBuilder.createDBEmbed(
        character: Character,
        fastestMoveList: List<Move>,
        featureInfo: FeatureInfo,
    ) {
        generalInfo(character)
        generalProperties(
            character,
            fastestMoveList,
            character.umo,
        )

        featureFooter(featureInfo)
    }

    private fun EmbedBuilder.createGBEmbed(
        character: Character,
        fastestMoveList: List<Move>,
        featureInfo: FeatureInfo,
    ) {
        generalInfo(character)
        generalProperties(
            character,
            fastestMoveList,
            character.umo,
        )

        character.gbvsrProperties?.apply {
            optionalField(name = "Prejump", value = prejump)
            optionalField(name = "Backdash", value = backdash)
            optionalField(name = "F Walk", value = "$walkSpeed ($walkSpeedRelative)")
            optionalField(name = "B Walk", value = "$walkSpeedBack ($walkSpeedBackRelative)")
            optionalField(name = "Dash", value = "$dashInitial ($dashInitialRelative")
            optionalField(name = "Dash Acc", value = "$dashAcceleration ($dashAccelerationRelative)")
        }

        featureFooter(featureInfo)
    }

    private fun EmbedBuilder.createBBEmbed(
        character: Character,
        fastestMoveList: List<Move>,
        featureInfo: FeatureInfo,
    ) {
        generalInfo(character)
        generalProperties(
            character,
            fastestMoveList,
            character.umo,
        )

        character.bbProperties?.apply {
            mandatoryField(
                name = "HP",
                value = hp,
            )

            mandatoryField(
                name = "Dash",
                value = "Forward: $forwardDash\n" +
                        "Back: $backDash"
            )

            mandatoryField(name = "Prejump", value = preJump)
        }

        featureFooter(featureInfo)
    }

    private fun EmbedBuilder.generalInfo(character: Character) {
        title = character.displayName
        url = character.wikiUrl
        color = Color(red)

        character.images?.iconUrl?.let { iconUrl ->
            thumbnail { url = iconUrl }
        }
    }

    private fun EmbedBuilder.generalProperties(
        character: Character,
        fastestMoveList: List<Move>,
        umo: List<String>?,
    ) {
        val moves = fastestMoveList.joinToString(", ") { it.input }
        val startup = fastestMoveList.first().startup.orDash()

        mandatoryField(
            name = "",
            value = character.aliasList.joinToString(", "),
            inline = false,
        )

        if (umo.isNullOrEmpty().not()) {
            optionalField(
                name = "UMO",
                value = umo.joinToString(", "),
            )
        }

        mandatoryField(
            name = "Fastest normal",
            value = "$startup: $moves"
        )
    }
}