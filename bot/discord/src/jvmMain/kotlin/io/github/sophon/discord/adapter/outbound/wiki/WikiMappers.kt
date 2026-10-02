package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.getGame
import io.github.sophon.core.util.orDash
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.DiscordConfig
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.MoveGameProperties
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError
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
import io.github.sophon.wiki.application.domain.model.gameProperties.Roa2MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.SF6MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.Uni2MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.VSAVMoveProperties

internal fun DiscordConfig.toWikiConfig(): Result<WikiConfig, WikiError> {
    val gameSet = this.featureList
        .filter { it.isEnabled }
        .flatMap { it.supportedGames }
        .mapNotNull { gameId -> gameId.getGame() }
        .toSet()

    val result = WikiConfig.create(
        availableGameSet = gameSet,
        enabledGameSet = gameSet,
    )

    return result
}

internal fun WikiError.toDomainError(): BotError {
    val botError = when (this) {
        is WikiError.UnknownCharacter,
        is WikiError.UnknownMove -> BotError.BotLogicError(this.toString())

        is WikiError.DownloadError,
        is WikiError.PageNotFound,
        is WikiError.DatabaseError,
        is WikiError.InvalidConfig -> BotError.WikiError(this.toString())
    }

    return botError
}

internal fun Move.toDomain(character: Character): BotResponse.MoveResponse {
    val isCollapsedByDefault: Boolean = when (character.id.game) {
        Game.GGST,
        Game.StreetFighter6,
        Game.BBCF,
        Game.GBVSR -> true

        else -> false
    }

    val moveResponse = BotResponse.MoveResponse(
        input = input,
        url = urls.wikiUrl,
        characterName = character.displayName,
        moveName = name,
        characterImageUrl = character.images?.iconUrl,
        primaryFields = toPrimaryFields(),
        dataSource = BotResponse.DataSource(
            name = "${character.id.game.displayName} (${character.id.game.wiki.id})",
            iconUrl = character.id.game.iconUrl, //TODO: should be wiki icon url
        ),
        isCollapsedByDefault = isCollapsedByDefault,
        secondaryFields = toSecondaryFields(),
        noteList = notes,
        aliasList = aliases,
        videoUrl = urls.videoUrl,
        hitboxImageList = urls.hitboxImageList,
    )

    return moveResponse
}

private fun Move.toPrimaryFields(): List<BotResponse.MoveResponse.Field> {
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

        else -> listOf()
    }
    val fieldList = (coreFieldList + gameFieldList)

    return fieldList
}

private fun Move.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = when (val properties = gameProperties) {
        is T8Properties -> listOf()

        is GGMoveProperties -> properties.toSecondaryFields(this)
        is BBMoveProperties -> properties.toSecondaryFields(this)
        is GBVSRMoveProperties -> properties.toSecondaryFields(this)
        is MTFSMoveProperties -> properties.toSecondaryFields(this)

        is SF6MoveProperties -> properties.toSecondaryFields(this)
        is AVLMoveProperties -> properties.toSecondaryFields(this)
        is MKMoveProperties -> properties.toSecondaryFields(this)

        is MBTLMoveProperties -> properties.toSecondaryFields()
        is Uni2MoveProperties -> properties.toSecondaryFields()
        is VSAVMoveProperties -> properties.toSecondaryFields()

        else -> (toDefaultSecondaryFields() + properties?.toSecondaryFields().orEmpty())
    }

    return fieldList
}

private fun Move.toDefaultSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Guard", guard),
        fieldOf("Active", active),
        fieldOf("Cancel", cancel),
        fieldOf("Recovery", recovery),
        fieldOf("Invul", invulnerability),
    )

    return fieldList
}

private fun MoveGameProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = when (this) {
        is DBFZMoveProperties -> toSecondaryFields()

        is KOF15MoveProperties -> toSecondaryFields()
        is COTWMoveProperties -> toSecondaryFields()

        is Roa2MoveProperties -> toSecondaryFields()

        else -> listOf()
    }

    return fieldList
}

//region Wavu
private fun T8Properties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Recovery", move.recovery),
    )

    return fieldList
}
//endregion

//region DustLoop
private fun GGMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun GGMoveProperties.toSecondaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
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

private fun BBMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Attribute", attribute),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun BBMoveProperties.toSecondaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Cancel", move.cancel),
        fieldOf("Level", level),
        fieldOf("Starter", starter),
        fieldOf("P1", p1),
        fieldOf("P2", p2),
        fieldOf("On ODR", onODR),
        fieldOf("Blockstun", blockstun),
        fieldOf("Ground hit", groundHit),
        fieldOf("Air hit", airHit),
        fieldOf("Ground CH", groundCH),
        fieldOf("Air CH", airCH),
        fieldOf("Blockstop", blockstop),
        fieldOf("Hitstop", hitstop),
        fieldOf("CH stop", chStop),
        fieldOf("Cancel timing", cancelTiming),
    )

    return fieldList
}

private fun DBFZMoveProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
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

private fun GBVSRMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun GBVSRMoveProperties.toSecondaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Cancel", move.cancel),
        fieldOf("Level", level),
        fieldOf("Meter", meter),
        fieldOf("Cooldown", cooldown),
        fieldOf("Class", cls),
    )

    return fieldList
}

private fun MTFSMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun MTFSMoveProperties.toSecondaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
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
private fun SF6MoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Guard", move.guard),
    )

    return fieldList
}

private fun SF6MoveProperties.toSecondaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Invul", move.invulnerability),
        fieldOf("Chip", chip),
        fieldOf("Damage scaling", dmgScaling),
        fieldOf("Total", total),
        fieldOf("Hit confirm", hitConfirm),
        fieldOf("Punish", punishAdv),
        fieldOf("Perfect parry", perfParryAdv),
        fieldOf("DRc on hit", DRcOH),
        fieldOf("DRc on block", DRcOB),
        fieldOf("DR on hit", DROH),
        fieldOf("DR on block", DROB),
        fieldOf("Hitstun", hitStun),
        fieldOf("Blockstun", blockStun),
        fieldOf("Hitstop", hitStop),
        fieldOf("Drive damage on hit", driveDmgOnHit),
        fieldOf("Drive damage on block", driveDmgOnBlock),
        fieldOf("Drive gain", driveGain),
        fieldOf("Super gain on hit", superGainOnHit),
        fieldOf("Super gain on block", superGainOnBlock),
        fieldOf("Armor", armor),
        fieldOf("Airborne", airborne),
        fieldOf("Juggle start", jugStart),
        fieldOf("Juggle increase", jugIncrease),
        fieldOf("Juggle limit", jugLimit),
        fieldOf("Projectile speed", projectileSpeed),
        fieldOf("Range", attackRange),
        fieldOf("Images", images),
    )

    return fieldList
}

private fun AVLMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Flow", flow),
    )

    return fieldList
}

private fun AVLMoveProperties.toSecondaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Cancel", move.cancel),
        fieldOf("Invul", move.invulnerability),
        fieldOf("Flow dmg", chiDamage),
    )

    return fieldList
}

private fun MKMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Chip", chip),
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("OFB", flawlessBlockAdv),
        mandatoryFieldOf("Cost", cost),
    )

    return fieldList
}

private fun MKMoveProperties.toSecondaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Invul", move.invulnerability),
        fieldOf("Hit cancel", hitCancelAdv),
        fieldOf("Block cancel", blockCancelAdv),
        fieldOf("Punish", punish),
    )

    return fieldList
}
//endregion

//region Mizuumi
private fun MBTLMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Property", mizuumiProperty),
        mandatoryFieldOf("Cost", cost),
        mandatoryFieldOf("Attribute", attribute),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Overall", overall),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun MBTLMoveProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Input info", inputInfo),
        fieldOf("Subtitle", subtitle),
        fieldOf("Min damage", minDamage),
        fieldOf("Landing", landing),
    )

    return fieldList
}

private fun Uni2MoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Attribute", attribute),
        mandatoryFieldOf("Invul", move.invulnerability),
    )

    return fieldList
}

private fun Uni2MoveProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Input info", inputInfo),
        fieldOf("Subtitle", subtitle),
        fieldOf("Min damage", minDamage),
        fieldOf("Cancel window", cancelWindow),
        fieldOf("Property", mizuumiProperty),
        fieldOf("Cost", cost),
        fieldOf("Landing", landing),
        fieldOf("Overall", overall),
        fieldOf("Assault", assaultAdv),
        fieldOf("Blockstun", blockstun),
        fieldOf("Ground hit", groundHit),
        fieldOf("Air hit", airHit),
        fieldOf("Ground CH", groundCH),
        fieldOf("Air CH", airCH),
        fieldOf("Hitstop", hitstop),
        fieldOf("CH stop", CHstop),
        fieldOf("Proration", proration),
        fieldOf("Combo P1", comboP1),
        fieldOf("Combo P2", comboP2),
    )

    return fieldList
}

private fun VSAVMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Renda", renda),
        mandatoryFieldOf("Guard", move.guard),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Invul", move.invulnerability),
        mandatoryFieldOf("W-dmg", whiteDmg),
        mandatoryFieldOf("Gauge", meter),
    )

    return fieldList
}

private fun VSAVMoveProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Input info", inputInfo),
        fieldOf("Subtitle", subtitle),
        fieldOf("Reaction", reaction),
        fieldOf("Curse time", curseTime),
    )

    return fieldList
}
//endregion

//region DreamCancel
private fun KOF15MoveProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Stun", stun),
    )

    return fieldList
}

private fun COTWMoveProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("REV damage", revDamage),
    )

    return fieldList
}
//endregion

//region DragDown
private fun Roa2MoveProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Mode", mode),
        fieldOf("Hitbox caption", hitboxCaption),
        fieldOf("Startup notes", startupNotes),
        fieldOf("Active notes", totalActiveNotes),
        fieldOf("Endlag notes", endlagNotes),
        fieldOf("Cancel notes", cancelNotes),
        fieldOf("Landing lag", landingLag),
        fieldOf("Landing lag notes", landingLagNotes),
        fieldOf("IASA", iasa),
        fieldOf("IASA notes", iasaNotes),
        fieldOf("Total duration", totalDuration),
        fieldOf("Total duration notes", totalDurationNotes),
        fieldOf("Ledge grab frame", ledgeGrabFrame),
        fieldOf("Ledge grab frame notes", ledgeGrabFrameNotes),
        fieldOf("Hit ID", hitID),
        fieldOf("Hit move ID", hitMoveID),
        fieldOf("Hit name", hitName),
        fieldOf("Hit active", hitActive),
        fieldOf("Shield safety", customShieldSafety),
        fieldOf("Unique", uniqueField),
        fieldOf("Article ID", articleID),
        fieldOf("Notes", notes),
        fieldOf("Advanced notes", advNotes),
    )

    return fieldList
}
//endregion

private fun mandatoryFieldOf(title: String, value: String?): BotResponse.MoveResponse.Field {
    val field = BotResponse.MoveResponse.Field(title, value.orDash())

    return field
}

private fun mandatoryFieldOf(title: String, valueList: List<String>?): BotResponse.MoveResponse.Field {
    val field = mandatoryFieldOf(title, valueList?.joinToString(", "))

    return field
}

private fun fieldOf(title: String, value: String?): BotResponse.MoveResponse.Field? {
    val field = value
        ?.takeIf { it.isNotBlank() }
        ?.let { BotResponse.MoveResponse.Field(title, it) }

    return field
}

private fun fieldOf(title: String, valueList: List<String>?): BotResponse.MoveResponse.Field? {
    val field = fieldOf(title, valueList?.joinToString(", "))

    return field
}
