package io.github.sophon.fightingnerd.adapter.outbound.wiki

import io.github.sophon.fightingnerd.app.model.CharacterGameProperties
import io.github.sophon.fightingnerd.app.model.MoveGameProperties
import io.github.sophon.fightingnerd.app.model.game.AVLMoveProperties
import io.github.sophon.fightingnerd.app.model.game.BBCharProperties
import io.github.sophon.fightingnerd.app.model.game.BBMoveProperties
import io.github.sophon.fightingnerd.app.model.game.COTWMoveProperties
import io.github.sophon.fightingnerd.app.model.game.DBFZCharProperties
import io.github.sophon.fightingnerd.app.model.game.DBFZMoveProperties
import io.github.sophon.fightingnerd.app.model.game.GBVSRCharProperties
import io.github.sophon.fightingnerd.app.model.game.GBVSRMoveProperties
import io.github.sophon.fightingnerd.app.model.game.GGCharProperties
import io.github.sophon.fightingnerd.app.model.game.GGMoveProperties
import io.github.sophon.fightingnerd.app.model.game.KOF15MoveProperties
import io.github.sophon.fightingnerd.app.model.game.MBTLMoveProperties
import io.github.sophon.fightingnerd.app.model.game.MKCharProperties
import io.github.sophon.fightingnerd.app.model.game.MKMoveProperties
import io.github.sophon.fightingnerd.app.model.game.MTFSCharProperties
import io.github.sophon.fightingnerd.app.model.game.MTFSMoveProperties
import io.github.sophon.fightingnerd.app.model.game.Roa2CharProperties
import io.github.sophon.fightingnerd.app.model.game.Roa2MoveProperties
import io.github.sophon.fightingnerd.app.model.game.SF6MoveProperties
import io.github.sophon.fightingnerd.app.model.game.SFCharProperties
import io.github.sophon.fightingnerd.app.model.game.T8Properties
import io.github.sophon.fightingnerd.app.model.game.Uni2CharProperties
import io.github.sophon.fightingnerd.app.model.game.Uni2MoveProperties
import io.github.sophon.fightingnerd.app.model.game.VSAVMoveProperties
import io.github.sophon.wiki.model.CharacterGameProperties as WikiCharacterGameProperties
import io.github.sophon.wiki.model.MoveGameProperties as WikiMoveGameProperties
import io.github.sophon.wiki.model.game.AVLMoveProperties as WikiAVLMoveProperties
import io.github.sophon.wiki.model.game.BBCharProperties as WikiBBCharProperties
import io.github.sophon.wiki.model.game.BBMoveProperties as WikiBBMoveProperties
import io.github.sophon.wiki.model.game.COTWMoveProperties as WikiCOTWMoveProperties
import io.github.sophon.wiki.model.game.DBFZCharProperties as WikiDBFZCharProperties
import io.github.sophon.wiki.model.game.DBFZMoveProperties as WikiDBFZMoveProperties
import io.github.sophon.wiki.model.game.GBVSRCharProperties as WikiGBVSRCharProperties
import io.github.sophon.wiki.model.game.GBVSRMoveProperties as WikiGBVSRMoveProperties
import io.github.sophon.wiki.model.game.GGCharProperties as WikiGGCharProperties
import io.github.sophon.wiki.model.game.GGMoveProperties as WikiGGMoveProperties
import io.github.sophon.wiki.model.game.KOF15MoveProperties as WikiKOF15MoveProperties
import io.github.sophon.wiki.model.game.MBTLMoveProperties as WikiMBTLMoveProperties
import io.github.sophon.wiki.model.game.MKCharProperties as WikiMKCharProperties
import io.github.sophon.wiki.model.game.MKMoveProperties as WikiMKMoveProperties
import io.github.sophon.wiki.model.game.MTFSCharProperties as WikiMTFSCharProperties
import io.github.sophon.wiki.model.game.MTFSMoveProperties as WikiMTFSMoveProperties
import io.github.sophon.wiki.model.game.Roa2CharProperties as WikiRoa2CharProperties
import io.github.sophon.wiki.model.game.Roa2MoveProperties as WikiRoa2MoveProperties
import io.github.sophon.wiki.model.game.SF6MoveProperties as WikiSF6MoveProperties
import io.github.sophon.wiki.model.game.SFCharProperties as WikiSFCharProperties
import io.github.sophon.wiki.model.game.T8Properties as WikiT8Properties
import io.github.sophon.wiki.model.game.Uni2CharProperties as WikiUni2CharProperties
import io.github.sophon.wiki.model.game.Uni2MoveProperties as WikiUni2MoveProperties
import io.github.sophon.wiki.model.game.VSAVMoveProperties as WikiVSAVMoveProperties

@Suppress("LongMethod")
internal fun WikiCharacterGameProperties.toDomain(): CharacterGameProperties? {
    val gameProperties = when (this) {
        is WikiRoa2CharProperties -> Roa2CharProperties(
            dacusSpeedMultiplier = dacusSpeedMultiplier,
            weight = weight,
            frictionGround = frictionGround,
            frictionAir = frictionAir,
            dashFrames = dashFrames,
            dashSpeed = dashSpeed,
            dashAcceleration = dashAcceleration,
            runSpeedMax = runSpeedMax,
            runTurnAcceleration = runTurnAcceleration,
            runTurnFrames = runTurnFrames,
            walkAccelerationMax = walkAccelerationMax,
            walkSpeedMax = walkSpeedMax,
            gravity = gravity,
            hitstunGravity = hitstunGravity,
            fallSpeedMax = fallSpeedMax,
            fastFallSpeed = fastFallSpeed,
            airAcceleration = airAcceleration,
            airSpeedHorizontalMax = airSpeedHorizontalMax,
            jumpSpeedHorizontalMax = jumpSpeedHorizontalMax,
            fullHopSpeed = fullHopSpeed,
            shortHopSpeed = shortHopSpeed,
            doubleJumpSpeed = doubleJumpSpeed,
            doubleJumpMaxHorizontalSpeed = doubleJumpMaxHorizontalSpeed,
            airDodgeSpeed = airDodgeSpeed,
            airDodgeFriction = airDodgeFriction,
            rollSpeed = rollSpeed,
            shieldSizeMultiplier = shieldSizeMultiplier,
            ledgeStandSpeed = ledgeStandSpeed,
            ledgeRollSpeed = ledgeRollSpeed,
            ledgeJumpMaxHorizontalAirSpeed = ledgeJumpMaxHorizontalAirSpeed,
            getupRollSpeed = getupRollSpeed,
            techRollSpeed = techRollSpeed,
            wallJumpSpeedY = wallJumpSpeedY,
            wallJumpSpeedX = wallJumpSpeedX,
        )
        is WikiGGCharProperties -> GGCharProperties(
            defense = defense,
            guts = guts,
            guardBalance = guardBalance,
            prejump = prejump,
            bwdDash = bwdDash,
            bwdDashDuration = bwdDashDuration,
            bwdDashInvulnerability = bwdDashInvulnerability,
            bwdDashAirborne = bwdDashAirborne,
            bwdDashDist = bwdDashDist,
            fwdDash = fwdDash,
            jumpDuration = jumpDuration,
            highJumpDuration = highJumpDuration,
            jumpHeight = jumpHeight,
            highJumpHeight = highJumpHeight,
            earliestIAD = earliestIAD,
            adDuration = adDuration,
            abdDuration = abdDuration,
            adDist = adDist,
            abdDist = abdDist,
            movementTension = movementTension,
            jumpTension = jumpTension,
            airDashTension = airDashTension,
            walkSpd = walkSpd,
            bwdWalkSpd = bwdWalkSpd,
            dashInitialSpd = dashInitialSpd,
            dashAcceleration = dashAcceleration,
            dashFriction = dashFriction,
            jumpGravity = jumpGravity,
            highJumpGravity = highJumpGravity,
            boostAttack = boostAttack,
            boostDefense = boostDefense,
        )
        is WikiBBCharProperties -> BBCharProperties(
            preJump = preJump,
            backDash = backDash,
            forwardDash = forwardDash,
        )
        is WikiDBFZCharProperties -> DBFZCharProperties(
            kiMod = kiMod,
        )
        is WikiGBVSRCharProperties -> GBVSRCharProperties(
            jump = jump?.toDomain(),
            backdash = backdash,
            walkSpeed = walkSpeed,
            walkSpeedBack = walkSpeedBack,
            dashInitial = dashInitial,
            dashAcceleration = dashAcceleration,
            closeRange = closeRange?.toDomain(),
        )
        is WikiMTFSCharProperties -> MTFSCharProperties(
            prejump = prejump,
            backdash = backdash,
            team = team,
        )
        is WikiUni2CharProperties -> Uni2CharProperties(
            smartSteer = smartSteer,
            fWalkSpeed = fWalkSpeed,
            fWalkSpeedNote = fWalkSpeedNote,
            bWalkSpeed = bWalkSpeed,
            bWalkSpeedNote = bWalkSpeedNote,
            jumpStartup = jumpStartup,
            jumpDuration = jumpDuration,
            jumpDurationNote = jumpDurationNote,
            dashStartup = dashStartup,
            iDashSpeed = iDashSpeed,
            iDashSpeedNote = iDashSpeedNote,
            dashAccel = dashAccel,
            dashAccelNote = dashAccelNote,
            maxDashSpeed = maxDashSpeed,
            bDashStartup = bDashStartup,
            bDashDuration = bDashDuration,
            bDashDurationNote = bDashDurationNote,
            bDashDistance = bDashDistance,
            bDashDistanceNote = bDashDistanceNote,
            bDashFullInvulStart = bDashFullInvulStart,
            bDashFullInvulEnd = bDashFullInvulEnd,
            bDashThrowInvulStart = bDashThrowInvulStart,
            bDashThrowInvulEnd = bDashThrowInvulEnd,
            throwWidth = throwWidth,
            throwRange = throwRange,
            trait = trait,
            vorpalTrait = vorpalTrait,
        )
        is WikiSFCharProperties -> SFCharProperties(
            fwdWalkSpd = fwdWalkSpd,
            bwdWalkSpd = bwdWalkSpd,
            fwdDashSpd = fwdDashSpd,
            bwdDashSpd = bwdDashSpd,
            fwdDashDist = fwdDashDist,
            bwdDashDist = bwdDashDist,
            dRushMin = dRushMin,
            dRushBlock = dRushBlock,
            dRushMax = dRushMax,
            throwRange = throwRange,
            throwHurtbox = throwHurtbox,
            jumpSpd = jumpSpd,
            jumpApex = jumpApex,
            fwdJumpDist = fwdJumpDist,
            bwdJumpDist = bwdJumpDist,
        )
        is WikiMKCharProperties -> MKCharProperties(
            hpMod = hpMod,
            throwDmg = throwDmg,
        )
        else -> null
    }
    return gameProperties
}

@Suppress("LongMethod", "CyclomaticComplexMethod")
internal fun WikiMoveGameProperties.toDomain(): MoveGameProperties? {
    val gameProperties = when (this) {
        is WikiRoa2MoveProperties -> Roa2MoveProperties(
            mode = mode,
            caption = caption,
            hitboxCaption = hitboxCaption,
            startupNotes = startupNotes,
            totalActiveNotes = totalActiveNotes,
            endlagNotes = endlagNotes,
            cancelNotes = cancelNotes,
            landingLag = landingLag,
            landingLagNotes = landingLagNotes,
            iasa = iasa,
            iasaNotes = iasaNotes,
            totalDuration = totalDuration,
            totalDurationNotes = totalDurationNotes,
            ledgeGrabFrame = ledgeGrabFrame,
            ledgeGrabFrameNotes = ledgeGrabFrameNotes,
            hitID = hitID,
            hitMoveID = hitMoveID,
            hitName = hitName,
            hitActive = hitActive,
            customShieldSafety = customShieldSafety,
            uniqueField = uniqueField,
            articleID = articleID,
            notes = notes,
            advNotes = advNotes,
        )
        is WikiKOF15MoveProperties -> KOF15MoveProperties(
            stun = stun,
        )
        is WikiCOTWMoveProperties -> COTWMoveProperties(
            revDamage = revDamage,
        )
        is WikiGGMoveProperties -> GGMoveProperties(
            riscGain = riscGain,
            riscLoss = riscLoss,
            wallDamage = wallDamage,
            inputTension = inputTension,
            chipRatio = chipRatio,
            otgType = otgType,
            prorate = prorate,
            level = level,
        )
        is WikiBBMoveProperties -> BBMoveProperties(
            onODR = onODR,
            attribute = attribute,
            p1 = p1,
            p2 = p2,
            starter = starter,
            level = level,
            blockstun = blockstun,
            groundHit = groundHit,
            airHit = airHit,
            groundCH = groundCH,
            airCH = airCH,
            blockstop = blockstop,
            hitstop = hitstop,
            chStop = chStop,
            cancelTiming = cancelTiming,
        )
        is WikiDBFZMoveProperties -> DBFZMoveProperties(
            attribute = attribute,
            smash = smash,
            kiGain = kiGain,
            prorate = prorate,
            blockStun = blockStun,
            groundHit = groundHit,
            airHit = airHit,
            level = level,
        )
        is WikiGBVSRMoveProperties -> GBVSRMoveProperties(
            meter = meter,
            level = level,
            cooldown = cooldown,
            cls = cls,
        )
        is WikiMTFSMoveProperties -> MTFSMoveProperties(
            simpleInput = simpleInput,
            level = level,
            prorate = prorate,
            meterGain = meterGain,
            untechAmount = untechAmount,
            hitboxCaption = hitboxCaption,
        )
        is WikiMBTLMoveProperties -> MBTLMoveProperties(
            inputInfo = inputInfo,
            subtitle = subtitle,
            minDamage = minDamage,
            mizuumiProperty = mizuumiProperty,
            cost = cost,
            attribute = attribute,
            landing = landing,
            overall = overall,
        )
        is WikiUni2MoveProperties -> Uni2MoveProperties(
            inputInfo = inputInfo,
            subtitle = subtitle,
            minDamage = minDamage,
            cancelWindow = cancelWindow,
            mizuumiProperty = mizuumiProperty,
            cost = cost,
            attribute = attribute,
            landing = landing,
            overall = overall,
            assaultAdv = assaultAdv,
            blockstun = blockstun,
            groundHit = groundHit,
            airHit = airHit,
            groundCH = groundCH,
            airCH = airCH,
            hitstop = hitstop,
            CHstop = CHstop,
            proration = proration,
            comboP1 = comboP1,
            comboP2 = comboP2,
        )
        is WikiVSAVMoveProperties -> VSAVMoveProperties(
            inputInfo = inputInfo,
            subtitle = subtitle,
            whiteDmg = whiteDmg,
            renda = renda,
            meter = meter,
            reaction = reaction,
            curseTime = curseTime,
        )
        is WikiSF6MoveProperties -> SF6MoveProperties(
            images = images,
            chip = chip,
            dmgScaling = dmgScaling,
            total = total,
            hitConfirm = hitConfirm,
            punishAdv = punishAdv,
            perfParryAdv = perfParryAdv,
            DRcOH = DRcOH,
            DRcOB = DRcOB,
            DROH = DROH,
            DROB = DROB,
            hitStun = hitStun,
            blockStun = blockStun,
            hitStop = hitStop,
            driveDmgOnBlock = driveDmgOnBlock,
            driveDmgOnHit = driveDmgOnHit,
            driveGain = driveGain,
            superGainOnHit = superGainOnHit,
            superGainOnBlock = superGainOnBlock,
            armor = armor,
            airborne = airborne,
            jugStart = jugStart,
            jugIncrease = jugIncrease,
            jugLimit = jugLimit,
            projectileSpeed = projectileSpeed,
            attackRange = attackRange,
        )
        is WikiAVLMoveProperties -> AVLMoveProperties(
            chiDamage = chiDamage,
            flow = flow,
        )
        is WikiMKMoveProperties -> MKMoveProperties(
            cost = cost,
            chip = chip,
            flawlessBlockAdv = flawlessBlockAdv,
            hitCancelAdv = hitCancelAdv,
            blockCancelAdv = blockCancelAdv,
            punish = punish,
        )
        is WikiT8Properties -> T8Properties(
            isHeat = isHeat,
            isHoming = isHoming,
            stance = stance,
            isPowerCrush = isPowerCrush,
            isHighCrush = isHighCrush,
            isLowCrush = isLowCrush,
            hasWallInteraction = hasWallInteraction,
            hasFloorInteraction = hasFloorInteraction,
        )
        else -> null
    }
    return gameProperties
}

private fun WikiGBVSRCharProperties.Jump.toDomain(): GBVSRCharProperties.Jump {
    val jump = GBVSRCharProperties.Jump(
        pre = pre,
        forwardDistance = forwardDistance,
        superForwardDistance = superForwardDistance,
        backDistance = backDistance,
        superBackDistance = superBackDistance,
        gravity = gravity,
        superGravity = superGravity,
        superHeight = superHeight,
    )
    return jump
}

private fun WikiGBVSRCharProperties.CloseRange.toDomain(): GBVSRCharProperties.CloseRange {
    val closeRange = GBVSRCharProperties.CloseRange(
        l = l,
        m = m,
        h = h,
    )
    return closeRange
}
