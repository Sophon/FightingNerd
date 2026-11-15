package io.github.sophon.fightingnerd.app.model.game

data class GGCharProperties(
    val defense: String?,
    val guts: String?,
    val guardBalance: String?,
    val prejump: String?,
    val bwdDash: String?,
    val bwdDashDuration: String?,
    val bwdDashInvulnerability: String?,
    val bwdDashAirborne: String?,
    val bwdDashDist: String?,
    val fwdDash: String?,
    val jumpDuration: String?,
    val highJumpDuration: String?,
    val jumpHeight: String?,
    val highJumpHeight: String?,
    val earliestIAD: String?,
    val adDuration: String?,
    val abdDuration: String?,
    val adDist: String?,
    val abdDist: String?,
    val movementTension: String?,
    val jumpTension: String?,
    val airDashTension: String?,
    val walkSpd: String?,
    val bwdWalkSpd: String?,
    val dashInitialSpd: String?,
    val dashAcceleration: String?,
    val dashFriction: String?,
    val jumpGravity: String?,
    val highJumpGravity: String?,
    val boostAttack: String?,
    val boostDefense: String?,
): CharacterGameProperties

data class BBCharProperties(
    val preJump: String? = null,
    val backDash: String? = null,
    val forwardDash: String? = null,
): CharacterGameProperties

data class DBFZCharProperties(
    val kiMod: String?,
): CharacterGameProperties

data class GBVSRCharProperties(
    val jump: Jump?,
    val backdash: String?,
    val walkSpeed: String?,
    val walkSpeedBack: String?,
    val dashInitial: String?,
    val dashAcceleration: String?,
    val closeRange: CloseRange?,
): CharacterGameProperties {
    data class Jump(
        val pre: String?,
        val forwardDistance: String?,
        val superForwardDistance: String?,
        val backDistance: String?,
        val superBackDistance: String?,
        val gravity: String?,
        val superGravity: String?,
        val superHeight: String?,
    )

    data class CloseRange(
        val l: String?,
        val m: String?,
        val h: String?,
    )
}

data class GGXrdCharProperties(
    val defense: String? = null,
    val guts: String? = null,
    val stunResistance: String? = null,
    val weight: String? = null,
    val prejump: String? = null,
    val backdashDuration: String? = null,
    val backdashDistance: String? = null,
    val backdashInvuln: String? = null,
    val dashForward: String? = null,
    val dashForwardDuration: String? = null,
    val dashResistance: String? = null,
    val dashAcceleration: String? = null,
    val initialDashSpeed: String? = null,
    val friction: String? = null,
    val walkSpeedForwards: String? = null,
    val walkSpeedBackwards: String? = null,
    val airDashForwardsSpeed: String? = null,
    val airDashForwardsStartup: String? = null,
    val airDashBackwardsSpeed: String? = null,
    val airDashBackwardsStartup: String? = null,
    val landingRecovery: String? = null,
    val throwRange: String? = null,
    val airThrowRange: String? = null,
    val riscGainRate: String? = null,
    val riscInertia: String? = null,
    val wakeupFaceUp: String? = null,
    val wakeupFaceDown: String? = null,
    val walkForwardsTensionGain: String? = null,
    val dashTensionGain: String? = null,
    val jumpForwardsTensionGain: String? = null,
    val airDashForwardsTensionGain: String? = null,
): CharacterGameProperties

data class MTFSCharProperties(
    val prejump: String? = null,
    val backdash: String? = null,
    val team: String? = null,
): CharacterGameProperties


data class GGMoveProperties(
    val riscGain: String? = null,
    val riscLoss: String? = null,
    val wallDamage: String? = null,
    val inputTension: String? = null,
    val chipRatio: String? = null,
    val otgType: String? = null,
    val prorate: String? = null,
    val level: String? = null,
): MoveGameProperties

data class BBMoveProperties(
    val onODR: String? = null,
    val attribute: String? = null,
    val p1: String? = null,
    val p2: String? = null,
    val starter: String? = null,
    val level: String? = null,
    val blockstun: String? = null,
    val groundHit: String? = null,
    val airHit: String? = null,
    val groundCH: String? = null,
    val airCH: String? = null,
    val blockstop: String? = null,
    val hitstop: String? = null,
    val chStop: String? = null,
    val cancelTiming: String? = null,
): MoveGameProperties

data class DBFZMoveProperties(
    val attribute: String? = null,
    val smash: String? = null,
    val kiGain: String? = null,
    val prorate: String? = null,
    val blockStun: String? = null,
    val groundHit: String? = null,
    val airHit: String? = null,
    val level: String? = null,
): MoveGameProperties

data class GBVSRMoveProperties(
    val meter: String? = null,
    val level: String? = null,
    val cooldown: String? = null,
    val cls: String? = null,
): MoveGameProperties

data class GGXrdMoveProperties(
    val level: String? = null,
    val riscGain: String? = null,
    val riscLoss: String? = null,
    val prorate: String? = null,
    val roman: String? = null,
    val tension: String? = null,
): MoveGameProperties

data class MTFSMoveProperties(
    val simpleInput: String? = null,
    val level: String? = null,
    val prorate: String? = null,
    val meterGain: String? = null,
    val untechAmount: String? = null,
    val hitboxCaption: String? = null,
): MoveGameProperties
