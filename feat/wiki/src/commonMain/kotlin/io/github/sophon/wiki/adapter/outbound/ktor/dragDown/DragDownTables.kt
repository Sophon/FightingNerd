package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import io.github.sophon.wiki.adapter.outbound.ktor.CargoTable
import io.github.sophon.wiki.application.domain.model.wiki.Game

internal object DragDownTables {
    val characterTableByGame = mapOf(
        Game.ROA2 to CargoTable(name = "CharacterData_RoA2", fieldList = roa2CharacterFieldList),
    )

    val moveTableByGame = mapOf(
        Game.ROA2 to CargoTable(name = "ROA2_MoveMode", fieldList = roa2MoveFieldList),
    )
}


private val roa2CharacterFieldList = listOf(
    "chara",
    "DacusSpeedMultiplier",
    "Weight",
    "FrictionGround",
    "FrictionAir",
    "DashFrames",
    "DashSpeed",
    "DashAcceleration",
    "RunSpeedMax",
    "RunTurnAcceleration",
    "RunTurnFrames",
    "WalkAccelerationMax",
    "WalkSpeedMax",
    "Gravity",
    "HitstunGravity",
    "FallSpeedMax",
    "FastFallSpeed",
    "AirAcceleration",
    "AirSpeedHorizontalMax",
    "JumpSpeedHorizontalMax",
    "FullHopSpeed",
    "ShortHopSpeed",
    "DoubleJumpSpeed",
    "DoubleJumpMaxHorizontalSpeed",
    "AirDodgeSpeed",
    "AirDodgeFriction",
    "RollSpeed",
    "ShieldSizeMultiplier",
    "LedgeStandSpeed",
    "LedgeRollSpeed",
    "LedgeJumpMaxHorizontalAirSpeed",
    "GetupRollSpeed",
    "TechRollSpeed",
    "WallJumpSpeedY",
    "WallJumpSpeedX",
)

private val roa2MoveFieldList = listOf(
    "chara",
    "attack",
    "attackID",
    "mode",
    "image",
    "hitbox",
    "caption",
    "hitboxCaption",
    "startup",
    "startupNotes",
    "totalActive",
    "totalActiveNotes",
    "endlag",
    "endlagNotes",
    "cancel",
    "cancelNotes",
    "landingLag",
    "landingLagNotes",
    "iasa",
    "iasaNotes",
    "totalDuration",
    "totalDurationNotes",
    "ledgeGrabFrame",
    "ledgeGrabFrameNotes",
    "frameChart",
    "hitID",
    "hitMoveID",
    "hitName",
    "hitActive",
    "customShieldSafety",
    "uniqueField",
    "articleID",
    "notes",
    "advNotes",
)
