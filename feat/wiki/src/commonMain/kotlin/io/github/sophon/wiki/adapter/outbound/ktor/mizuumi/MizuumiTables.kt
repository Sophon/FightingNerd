package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import io.github.sophon.core.featureConfig.model.Game

internal data class CargoTable(
    val name: String,
    val fieldList: List<String>,
)

/**
 * Only separate games (`Game.separateCharMoveDownload`) have a character table.
 */
internal object MizuumiTables {
    val characterTableByGame = mapOf(
        Game.Uni2 to CargoTable(name = "UNI2_CharStats", fieldList = uni2CharacterFieldList),
    )

    val moveTableByGame = mapOf(
        Game.MBTL to CargoTable(name = "MBTL_MoveData", fieldList = mbtlMoveFieldList),
        Game.Uni2 to CargoTable(name = "UNI2_MoveData", fieldList = uni2MoveFieldList),
        Game.VSAV to CargoTable(name = "VSAV_MoveData", fieldList = vsavMoveFieldList),
    )
}


private val uni2CharacterFieldList = listOf(
    "chara",
    "smartSteer",
    "health",
    "fWalkSpeed",
    "fWalkSpeedNote",
    "bWalkSpeed",
    "bWalkSpeedNote",
    "jumpStartup",
    "jumpDuration",
    "jumpDurationNote",
    "dashStartup",
    "iDashSpeed",
    "iDashSpeedNote",
    "dashAccel",
    "dashAccelNote",
    "maxDashSpeed",
    "bDashStartup",
    "bDashDuration",
    "bDashDurationNote",
    "bDashDistance",
    "bDashDistanceNote",
    "bDashFullInvulStart",
    "bDashFullInvulEnd",
    "bDashThrowInvulStart",
    "bDashThrowInvulEnd",
    "throwWidth",
    "throwRange",
    "trait",
    "vorpalTrait",
)

private val mbtlMoveFieldList = listOf(
    "moveId",
    "chara",
    "input",
    "inputInfo",
    "name",
    "subtitle",
    "images",
    "hitboxes",
    "damage",
    "minDamage",
    "guard",
    "cancel",
    "property",
    "cost",
    "attribute",
    "startup",
    "active",
    "recovery",
    "landing",
    "overall",
    "frameAdv",
    "invul",
)

private val uni2MoveFieldList = listOf(
    "moveId",
    "chara",
    "input",
    "inputInfo",
    "name",
    "subtitle",
    "images",
    "hitboxes",
    "damage",
    "minDamage",
    "type",
    "guard",
    "cancel",
    "cancelWindow",
    "property",
    "cost",
    "attribute",
    "startup",
    "active",
    "recovery",
    "landing",
    "overall",
    "frameAdv",
    "onHit",
    "assaultAdv",
    "blockstun",
    "groundHit",
    "airHit",
    "groundCH",
    "airCH",
    "hitstop",
    "CHstop",
    "invul",
    "proration",
    "comboP1",
    "comboP2",
)

private val vsavMoveFieldList = listOf(
    "moveId",
    "chara",
    "input",
    "inputInfo",
    "name",
    "subtitle",
    "images",
    "hitboxes",
    "totaldmg",
    "whitedmg",
    "guard",
    "startup",
    "active",
    "recovery",
    "advHit",
    "advBlock",
    "invul",
    "cancel",
    "renda",
    "meter",
    "reaction",
    "cursetime",
)
