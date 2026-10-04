package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.gameProperties.BBCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GBVSRCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GGCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MTFSCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.Roa2CharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.SFCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.Uni2CharProperties
import io.github.sophon.wiki.application.domain.model.CharacterId as WikiCharacterId

internal fun Character.toDomain(): BotResponse.CharacterResponse {
    val characterResponse = BotResponse.CharacterResponse(
        id = id.naturalId,
        game = id.game,
        displayName = displayName,
        dataSource = toDataSource(),
        aliasList = aliasList,
        propertyList = toPropertyList(),
    )

    return characterResponse
}

internal fun CharacterId.toWikiCharacterId(): WikiCharacterId {
    val wikiCharacterId = WikiCharacterId(
        game = game,
        naturalId = characterId,
    )

    return wikiCharacterId
}

internal fun Character.toDataSource(): BotResponse.DataSource {
    val dataSource = BotResponse.DataSource(
        name = "${id.game.displayName} (${id.game.wiki.displayName})",
        iconUrl = id.game.wiki.iconUrl,
        color = id.game.wiki.color,
    )

    return dataSource
}

private fun Character.toPropertyList(): List<BotResponse.Field> {
    val healthFieldList = listOfNotNull(fieldOf("Health", hp))
    val gameFieldList = when (val properties = gameProperties) {
        is GGCharProperties -> properties.toFieldList()
        is BBCharProperties -> properties.toFieldList()
        is MTFSCharProperties -> properties.toFieldList()
        is GBVSRCharProperties -> properties.toFieldList()

        is SFCharProperties -> properties.toFieldList()

        is Uni2CharProperties -> properties.toFieldList()

        is Roa2CharProperties -> properties.toFieldList()

        else -> listOf()
    }
    val umoFieldList = listOfNotNull(fieldOf("Unique movement", umo))
    val fieldList = (healthFieldList + gameFieldList + umoFieldList)

    return fieldList
}

//region DustLoop
private fun GGCharProperties.toFieldList(): List<BotResponse.Field> {
    val backdash = listOfNotNull(
        bwdDashDuration.toFrames()?.let { "$it duration" },
        bwdDashInvulnerability.toFrames()?.let { "$it invuln" },
    ).joinToString("\n")
    val fieldList = listOfNotNull(
        fieldOf("Guts", guts),
        fieldOf("Backdash", backdash),
        fieldOf("Dash initial speed", dashInitialSpd),
        fieldOf("Air dash distance", adDist),
        fieldOf("Air backdash distance", abdDist),
        fieldOf("Jump startup", prejump.toFrames()),
    )

    return fieldList
}

private fun BBCharProperties.toFieldList(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Prejump", preJump),
        fieldOf("Backdash", backDash),
    )

    return fieldList
}

private fun MTFSCharProperties.toFieldList(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Backdash", backdash),
    )

    return fieldList
}

private fun GBVSRCharProperties.toFieldList(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Backdash", backdash),
        fieldOf("Jump startup", jump?.pre),
        fieldOf("Walk speed", walkSpeed),
        fieldOf("Backwalk speed", walkSpeedBack),
        fieldOf("Initial dash speed", dashInitial),
        fieldOf("Forward jump distance", jump?.forwardDistance),
        fieldOf("Backward jump distance", jump?.backDistance),
        fieldOf("Superjump height", jump?.superHeight),
        fieldOf("Forward superjump distance", jump?.superForwardDistance),
        fieldOf("Backward superjump distance", jump?.superBackDistance),
        fieldOf("c.L proximity range", closeRange?.l),
        fieldOf("c.M proximity range", closeRange?.m),
        fieldOf("c.H proximity range", closeRange?.h),
    )

    return fieldList
}
//endregion

//region SuperCombo
private fun SFCharProperties.toFieldList(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Forward walk speed", fwdWalkSpd),
        fieldOf("Backward walk speed", bwdWalkSpd),
        fieldOf("Forward dash speed", fwdDashSpd),
        fieldOf("Backward dash speed", bwdDashSpd),
        fieldOf("Forward dash distance", fwdDashDist),
        fieldOf("Backward dash distance", bwdDashDist),
        fieldOf("Drive rush min (throw)", dRushMin),
        fieldOf("Drive rush min (block)", dRushBlock),
        fieldOf("Drive rush max", dRushMax),
        fieldOf("Jump speed", jumpSpd),
        fieldOf("Jump apex", jumpApex),
        fieldOf("Forward jump distance", fwdJumpDist),
        fieldOf("Backward jump distance", bwdJumpDist),
        fieldOf("Throw range", throwRange),
        fieldOf("Throw hurtbox", throwHurtbox),
    )

    return fieldList
}
//endregion

//region Mizuumi
/** Backdash as `28F (1-8 full, 9-10 throw)`. */
private fun Uni2CharProperties.toFieldList(): List<BotResponse.Field> {
    val invulList = listOfNotNull(
        rangeOf(bDashFullInvulStart, bDashFullInvulEnd)?.let { "$it full" },
        rangeOf(bDashThrowInvulStart, bDashThrowInvulEnd)?.let { "$it throw" },
    )
    val invul = invulList
        .takeIf { it.isNotEmpty() }
        ?.joinToString(prefix = " (", postfix = ")")
    val backdash = bDashDuration.toFrames()?.let { duration -> "$duration${invul.orEmpty()}" }
    val fieldList = listOfNotNull(
        fieldOf("Prejump", jumpStartup.toFrames()),
        fieldOf("Backdash", backdash),
    )

    return fieldList
}
//endregion

//region DragDown
private fun Roa2CharProperties.toFieldList(): List<BotResponse.Field> {
    val fieldList = listOfNotNull(
        fieldOf("Weight", weight),
        fieldOf("Hitstun gravity", hitstunGravity),
        fieldOf("Max fall speed", fallSpeedMax),
        fieldOf("Dash speed", dashSpeed),
        fieldOf("Dash frames", dashFrames),
        fieldOf("Max run speed", runSpeedMax),
        fieldOf("Ground friction", frictionGround),
        fieldOf("Horizontal jump speed", jumpSpeedHorizontalMax),
        fieldOf("Air speed", airSpeedHorizontalMax),
        fieldOf("Air acceleration", airAcceleration),
    )

    return fieldList
}
//endregion


/** Appends `F` to bare frame counts - `4` → `4F`; null when blank. */
private fun String?.toFrames(): String? {
    val frames = this
        ?.takeIf { it.isNotBlank() }
        ?.let { if (it.endsWith("F", ignoreCase = true)) it else "${it}F" }

    return frames
}

/** `start-end`; null unless both are present. */
private fun rangeOf(start: String?, end: String?): String? {
    val range = if (start.isNullOrBlank() || end.isNullOrBlank()) null else "$start-$end"

    return range
}
