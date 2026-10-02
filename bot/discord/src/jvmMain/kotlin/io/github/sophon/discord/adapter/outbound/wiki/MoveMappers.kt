package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.orDash
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.Emoji
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
import kotlin.collections.orEmpty
import kotlin.time.Duration.Companion.seconds

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

internal fun List<Move>.toListResponse(
    character: Character,
    moveType: MoveType,
): BotResponse.ListResponse {
    val buttonSet = mapIndexed { index, move ->
        BotResponse.EmbedButton(
            label = (index + 1).toString(),
            action = BotResponse.EmbedButton.Action.Query(
                moveId = MoveId(
                    game = character.id.game,
                    characterId = character.id.naturalId,
                    input = move.input,
                ),
            ),
        )
    }
        .takeIf { it.isNotEmpty() }
        ?.let { buttonList ->
            BotResponse.ButtonSet(
                buttonList = buttonList,
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        }

    val listResponse = BotResponse.ListResponse(
        title = "${moveType.toEmoji()}${character.displayName.uppercase()} ${moveType.toTitle()} moves",
        values = map { it.input },
        dataSource = character.toDataSource(),
        buttonSet = buttonSet,
    )

    return listResponse
}

internal fun List<Move>.toListResponse(
    character: Character,
    moveType: String,
): BotResponse.ListResponse {
    val buttonSet = mapIndexed { index, move ->
        BotResponse.EmbedButton(
            label = (index + 1).toString(),
            action = BotResponse.EmbedButton.Action.Query(
                moveId = MoveId(
                    game = character.id.game,
                    characterId = character.id.naturalId,
                    input = move.input,
                ),
            ),
        )
    }
        .takeIf { it.isNotEmpty() }
        ?.let { buttonList ->
            BotResponse.ButtonSet(
                buttonList = buttonList,
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        }

    val listResponse = BotResponse.ListResponse(
        title = "$moveType moves",
        values = map { it.input },
        dataSource = character.toDataSource(),
        buttonSet = buttonSet,
    )

    return listResponse
}

internal fun Set<String>.toListResponse(
    character: Character,
): BotResponse.ListResponse {
    val buttonSet = mapIndexed { index, stance ->
        BotResponse.EmbedButton(
            label = (index + 1).toString(),
            action = BotResponse.EmbedButton.Action.Command(
                command = Command.Stance,
                query = "${character.id.naturalId} $stance",
            ),
        )
    }
        .takeIf { it.isNotEmpty() }
        ?.let { buttonList ->
            BotResponse.ButtonSet(
                buttonList = buttonList,
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        }

    val listResponse = BotResponse.ListResponse(
        title = "${character.displayName.uppercase()} stances",
        values = toList(),
        dataSource = character.toDataSource(),
        buttonSet = buttonSet,
    )

    return listResponse
}

private fun MoveType.toTitle(): String {
    val title = when (this) {
        MoveType.PC -> "Power Crush"
        MoveType.HEAT -> "Heat"
        MoveType.HOMING -> "Homing"
    }

    return title
}

private fun MoveType.toEmoji(): Emoji {
    val emoji = when (this) {
        MoveType.PC -> Emoji.TK_PC
        MoveType.HEAT -> Emoji.TK_HEAT
        MoveType.HOMING -> Emoji.TK_HOMING
    }

    return emoji
}

private fun Character.toDataSource(): BotResponse.DataSource {
    val dataSource = BotResponse.DataSource(
        name = "${id.game.displayName} (${id.game.wiki.id})",
        iconUrl = id.game.iconUrl, //TODO: should be wiki icon url
    )

    return dataSource
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

        is KOF15MoveProperties -> properties.toPrimaryFields(this)

        else -> (toDefaultPrimaryFields() + properties?.toPrimaryFields().orEmpty())
    }
    val fieldList = (coreFieldList + gameFieldList)

    return fieldList
}

private fun Move.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
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

private fun Move.toDefaultPrimaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Guard", guard),
        fieldOf("Active", active),
        fieldOf("Cancel", cancel),
        fieldOf("Recovery", recovery),
        fieldOf("Invul", invulnerability),
    )

    return fieldList
}

private fun MoveGameProperties.toPrimaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = when (this) {
        is DBFZMoveProperties -> toPrimaryFields()

        is COTWMoveProperties -> toPrimaryFields()

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
        fieldOf("Ground hit", groundHit),
        fieldOf("Ground CH", groundCH),
        fieldOf("Air hit", airHit),
        fieldOf("Air CH", airCH),
    )

    return fieldList
}

private fun DBFZMoveProperties.toPrimaryFields(): List<BotResponse.MoveResponse.Field> {
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
private fun SF6MoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOf(
        mandatoryFieldOf("Active", move.active),
        mandatoryFieldOf("Recovery", move.recovery),
        mandatoryFieldOf("Cancel", move.cancel),
        mandatoryFieldOf("Guard", move.guard),
    )

    return fieldList
}

private fun SF6MoveProperties.toSecondaryFields(): List<BotResponse.MoveResponse.Field> {
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
private fun MBTLMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
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

private fun Uni2MoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
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

private fun VSAVMoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
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
private fun KOF15MoveProperties.toPrimaryFields(move: Move): List<BotResponse.MoveResponse.Field> {
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

private fun COTWMoveProperties.toPrimaryFields(): List<BotResponse.MoveResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("REV damage", revDamage),
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

/** `a | b | c` with blanks as dashes; null when every value is blank. */
private fun mergedValueOf(vararg values: String?): String? {
    val mergedValue = values
        .takeIf { valueList -> valueList.any { !it.isNullOrBlank() } }
        ?.joinToString(" | ") { it.orDash() }

    return mergedValue
}

private fun fieldOf(title: String, valueList: List<String>?): BotResponse.MoveResponse.Field? {
    val field = fieldOf(title, valueList?.joinToString(", "))

    return field
}