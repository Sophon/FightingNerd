package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.MoveId
import io.github.sophon.discord.app.domain.model.MoveType
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Filter
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.MoveGameProperties
import io.github.sophon.wiki.application.domain.model.WavuFilters
import io.github.sophon.wiki.application.domain.model.gameProperties.AVLMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.BBMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.COTWMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.DBFZMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GBVSRMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GGMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.KOF15MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MBTLMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MKMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MTFSMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.SF6MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.T8Properties
import io.github.sophon.wiki.application.domain.model.gameProperties.Uni2MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.VSAVMoveProperties
import io.github.sophon.wiki.application.domain.model.wiki.Game

internal fun Move.toDomain(character: Character): BotResponse.MoveResponse {
    val isCollapsedByDefault: Boolean = when (character.id.game) {
        Game.GGST,
        Game.BBCF,
        Game.GBVSR,
        Game.AVL,
        Game.StreetFighter6 -> true

        else -> false
    }

    val secondaryFields = toSecondaryFields()
    val buttonSet = toButtonList(
        character = character,
        hasDetails = (isCollapsedByDefault && secondaryFields.isNotEmpty()),
    )
        .takeIf { it.isNotEmpty() }
        ?.let { buttonList -> BotResponse.ButtonSet(buttonList = buttonList) }

    val moveResponse = BotResponse.MoveResponse(
        input = input,
        url = urls.wikiUrl,
        characterName = character.displayName,
        moveName = name,
        characterImageUrl = character.images?.iconUrl,
        primaryFields = toPrimaryFields(),
        dataSource = character.toDataSource(),
        isCollapsedByDefault = isCollapsedByDefault,
        secondaryFields = secondaryFields,
        noteList = notes,
        aliasList = aliases,
        hitboxImageList = urls.hitboxImageList,
        stance = (gameProperties as? T8Properties)?.stance,
        buttonSet = buttonSet,
    )

    return moveResponse
}

internal fun MoveType.toFilter(): Filter {
    val filter = when (this) {
        MoveType.PC -> WavuFilters.PowerCrush
        MoveType.HEAT -> WavuFilters.Heat
        MoveType.HOMING -> WavuFilters.Homing
    }

    return filter
}

private fun Move.toButtonList(
    character: Character,
    hasDetails: Boolean,
): List<BotResponse.EmbedButton> {
    val detailsButton = if (hasDetails) {
        BotResponse.EmbedButton(
            label = "Details",
            action = BotResponse.EmbedButton.Action.Expand(
                moveId = MoveId(
                    game = character.id.game,
                    characterId = character.id.naturalId,
                    input = input,
                ),
            ),
        )
    } else {
        null
    }
    val videoButton = urls.videoUrl?.let { url ->
        BotResponse.EmbedButton(label = "Video", action = BotResponse.EmbedButton.Action.Text(url))
    }
    val buttonList = listOfNotNull(detailsButton, videoButton)

    return buttonList
}

private fun Move.toPrimaryFields(): List<BotResponse.Field> {
    val coreFieldList = listOf(
        mandatoryFieldOf("Startup", startup),
        mandatoryFieldOf("Hit", onHit),
        mandatoryFieldOf("Block", onBlock),
        mandatoryFieldOf("Counter", onCH),
        mandatoryFieldOf("Damage", damage),
    )
    val gameFieldList = when (val properties = gameProperties) {
        is T8Properties -> properties.toPrimaryFields(this)

        is GGMoveProperties -> properties.toPrimaryFields(this)
        is BBMoveProperties -> properties.toPrimaryFields(this)
        is GBVSRMoveProperties -> properties.toPrimaryFields(this)
        is MTFSMoveProperties -> properties.toPrimaryFields(this)

        is SF6MoveProperties -> properties.toPrimaryFields(this)
        is AVLMoveProperties -> properties.toPrimaryFields(this)
        is MKMoveProperties -> properties.toPrimaryFields(this)

        is MBTLMoveProperties -> properties.toPrimaryFields(this)
        is Uni2MoveProperties -> properties.toPrimaryFields(this)
        is VSAVMoveProperties -> properties.toPrimaryFields(this)

        is KOF15MoveProperties -> properties.toPrimaryFields(this)

        else -> (toDefaultPrimaryFields() + properties?.toPrimaryFields().orEmpty())
    }
    val fieldList = (coreFieldList + gameFieldList)

    return fieldList
}

private fun Move.toSecondaryFields(): List<BotResponse.Field> {
    val fieldList = when (val properties = gameProperties) {
        is GGMoveProperties -> properties.toSecondaryFields(this)
        is BBMoveProperties -> properties.toSecondaryFields(this)
        is GBVSRMoveProperties -> properties.toSecondaryFields(this)

        is SF6MoveProperties -> properties.toSecondaryFields()
        is AVLMoveProperties -> properties.toSecondaryFields(this)

        else -> listOf()
    }

    return fieldList
}

private fun Move.toDefaultPrimaryFields(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Guard", guard),
        fieldOf("Active", active),
        fieldOf("Cancel", cancel),
        fieldOf("Recovery", recovery),
        fieldOf("Invul", invulnerability),
    )

    return fieldList
}

private fun MoveGameProperties.toPrimaryFields(): List<BotResponse.Field> {
    val fieldList = when (this) {
        is DBFZMoveProperties -> toPrimaryFields()

        is COTWMoveProperties -> toPrimaryFields()

        else -> listOf()
    }

    return fieldList
}

//region Wavu
private fun T8Properties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Recovery", move.recovery),
    )

    return fieldList
}
//endregion

//region DustLoop
private fun GGMoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun GGMoveProperties.toSecondaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Cancel", move.cancel),
        fieldOf("Level", level),
        fieldOf("Risc gain", riscGain),
        fieldOf("Risc loss", riscLoss),
        fieldOf("Wall damage", wallDamage),
        fieldOf("Input tension", inputTension),
        fieldOf("Chip ratio", chipRatio),
        fieldOf("OTG", otgType),
        fieldOf("Prorate", prorate),
    )

    return fieldList
}

private fun BBMoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Attribute", attribute),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun BBMoveProperties.toSecondaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Cancel", move.cancel),
        fieldOf("Level", level),
        fieldOf("Starter", starter),
        fieldOf("P1", p1),
        fieldOf("P2", p2),
        fieldOf("Ground hit", groundHit),
        fieldOf("Ground CH", groundCH),
        fieldOf("Air hit", airHit),
        fieldOf("Air CH", airCH),
    )

    return fieldList
}

private fun DBFZMoveProperties.toPrimaryFields(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Level", level),
        fieldOf("Attribute", attribute),
        fieldOf("Smash", smash),
        fieldOf("Ki gain", kiGain),
        fieldOf("Prorate", prorate),
        fieldOf("Blockstun", blockStun),
        fieldOf("Ground hit", groundHit),
        fieldOf("Air hit", airHit),
    )

    return fieldList
}

private fun GBVSRMoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun GBVSRMoveProperties.toSecondaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Cancel", move.cancel),
        fieldOf("Level", level),
        fieldOf("Meter", meter),
        fieldOf("Cooldown", cooldown),
        fieldOf("Class", cls),
    )

    return fieldList
}

private fun MTFSMoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Invul", move.invulnerability),
        fieldOf("Cancel", move.cancel),
        fieldOf("Simple input", simpleInput),
        fieldOf("Level", level),
        fieldOf("Prorate", prorate),
        fieldOf("Meter gain", meterGain),
        fieldOf("Untech", untechAmount),
        fieldOf("Hitbox", hitboxCaption),
    )

    return fieldList
}
//endregion

//region SuperCombo
private fun SF6MoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Guard", move.guard),
    )

    return fieldList
}

private fun SF6MoveProperties.toSecondaryFields(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Perfect parry", perfParryAdv),
        fieldOf("Blockstun", blockStun),
        fieldOf("Hitstop", hitStop),
        fieldOf("DRc OH", DRcOH),
        fieldOf("DRc OB", DRcOB),
        fieldOf("DR DMG OH", driveDmgOnHit),
        fieldOf("DR DMG OB", driveDmgOnBlock),
        fieldOf("JGL st | inc | lim", mergedValueOf(jugStart, jugIncrease, jugLimit)),
    )

    return fieldList
}

private fun AVLMoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Flow", flow),
    )

    return fieldList
}

private fun AVLMoveProperties.toSecondaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Cancel", move.cancel),
        fieldOf("Invul", move.invulnerability),
        fieldOf("Flow dmg", chiDamage),
    )

    return fieldList
}

private fun MKMoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Chip", chip),
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("OFB", flawlessBlockAdv),
        mandatoryFieldOf("Cost", cost),
        fieldOf("Invul", move.invulnerability),
        fieldOf("Hit cancel", hitCancelAdv),
        fieldOf("Block cancel", blockCancelAdv),
        fieldOf("Punish", punish),
    )

    return fieldList
}
//endregion

//region Mizuumi
private fun MBTLMoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Property", mizuumiProperty),
        mandatoryFieldOf("Cost", cost),
        mandatoryFieldOf("Attribute", attribute),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Overall", overall),
        mandatoryFieldOf("Invul", move.invulnerability),
        fieldOf("Input info", inputInfo),
        fieldOf("Subtitle", subtitle),
        fieldOf("Min damage", minDamage),
        fieldOf("Landing", landing),
    )

    return fieldList
}

private fun Uni2MoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Attribute", attribute),
        mandatoryFieldOf("Invul", move.invulnerability),
        mandatoryFieldOf("Property", mizuumiProperty),
    )

    return fieldList
}

private fun VSAVMoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Renda", renda),
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Invul", move.invulnerability),
        mandatoryFieldOf("W-dmg", whiteDmg),
        mandatoryFieldOf("Gauge", meter),
        mandatoryFieldOf("Reaction", reaction),
        fieldOf("Curse time", curseTime),
    )

    return fieldList
}
//endregion

//region DreamCancel
private fun KOF15MoveProperties.toPrimaryFields(move: Move): List<BotResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Invul", move.invulnerability),
        mandatoryFieldOf("Stun", stun),
    )

    return fieldList
}

private fun COTWMoveProperties.toPrimaryFields(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("REV damage", revDamage),
    )

    return fieldList
}
//endregion