package io.github.sophon.wiki.adapter.outbound.ktor.superCombo

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.ktor.CargoTable

/**
 * Character tables alias `_pageName` as `Character` - the character id is formed from the page name.
 */
internal object SuperComboTables {
    val characterTableByGame = mapOf(
        Game.StreetFighter6 to CargoTable(name = "SF6_CharacterData", fieldList = sf6CharacterFieldList),
        Game.MK1 to CargoTable(name = "MK1_CharacterData", fieldList = mk1CharacterFieldList),
        Game.AVL to CargoTable(name = "AL_CharacterData", fieldList = avlCharacterFieldList),
    )

    val moveTableByGame = mapOf(
        Game.StreetFighter6 to CargoTable(name = "SF6_FrameData", fieldList = sf6MoveFieldList),
        Game.MK1 to CargoTable(name = "MK1_FrameData", fieldList = mk1MoveFieldList),
        Game.AVL to CargoTable(name = "AL_FrameData", fieldList = avlMoveFieldList),
    )
}


private val sf6CharacterFieldList = listOf(
    "_pageName=Character",
    "chara",
    "name",
    "portrait",
    "icon",
    "hp",
    "throwRange",
    "throwHurtbox",
    "fwdWalkSpd",
    "bwdWalkSpd",
    "fwdDashSpd",
    "bwdDashSpd",
    "fwdDashDist",
    "bwdDashDist",
    "jumpSpd",
    "jumpApex",
    "fwdJumpDist",
    "bwdJumpDist",
    "dRushMin",
    "dRushBlock",
    "dRushMax",
)

private val mk1CharacterFieldList = listOf(
    "_pageName=Character",
    "chara",
    "name",
    "portrait",
    "icon",
    "hp",
    "hpmod",
    "throwdmg",
)

private val avlCharacterFieldList = listOf(
    "_pageName=Character",
    "chara",
    "portrait",
    "icon",
    "hp",
)

private val sf6MoveFieldList = listOf(
    "moveId",
    "moveType",
    "chara",
    "input",
    "name",
    "images",
    "hitboxes",
    "damage",
    "chip",
    "dmgScaling",
    "startup",
    "active",
    "recovery",
    "total",
    "guard",
    "cancel",
    "hitconfirm",
    "hitAdv",
    "blockAdv",
    "punishAdv",
    "perfParryAdv",
    "DRcancelHit",
    "DRcancelBlk",
    "afterDRHit",
    "afterDRBlk",
    "hitstun",
    "blockstun",
    "hitstop",
    "driveDmgBlk",
    "driveDmgHit",
    "driveGain",
    "superGainHit",
    "superGainBlk",
    "invuln",
    "armor",
    "airborne",
    "jugStart",
    "jugIncrease",
    "jugLimit",
    "projSpeed",
    "atkRange",
    "notes",
)

private val mk1MoveFieldList = listOf(
    "moveId",
    "moveType",
    "chara",
    "input",
    "name",
    "images",
    "hitboxes",
    "cost",
    "damage",
    "chip",
    "startup",
    "active",
    "recovery",
    "invuln",
    "hitAdv",
    "blockAdv",
    "flawlessBlockAdv",
    "hitCancelAdv",
    "blockCancelAdv",
    "guard",
    "cancel",
    "punish",
    "notes",
)

private val avlMoveFieldList = listOf(
    "moveId",
    "chara",
    "input",
    "moveType",
    "name",
    "images",
    "hitboxes",
    "damage",
    "flowDamage",
    "startup",
    "active",
    "recovery",
    "onBlock",
    "onHit",
    "guard",
    "flow",
    "invuln",
    "cancel",
    "properties",
)
