package io.github.sophon.wikiSuperCombo.data

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.domain.model.Move
import kotlin.test.Test

class MoveMapperTest {
    @Test
    fun `toDomain should map SF6 move with HTML fields and templates`() {
        // given
        val moveDto = MoveDto(
            moveId = "ken_623kk",
            moveType = "special",
            chara = "Ken",
            input = "623KK",
            name = "Dragonlash Kick",
            images = "SF6_Ken_623kk.png",
            hitboxes = "SF6_Ken_623kk_hitbox.png",
            damage = "500,700",
            chip = "175",
            dmgScaling = "Combo (5% extra); both hits apply scaling",
            startup = "9(19)",
            active = "2(7)6",
            recovery = "3+21 land",
            total = "47",
            guard = "LH",
            cancel = "-",
            hitconfirm = "{{{hitconfirm}}}",
            hitAdv = "<span style=\"color: #32CD32;\">+1<span style=\"color: #30D5B8;\">'''(+4)'''</span></span>",
            blockAdv = "<span style=\"color: #b70c0b;\">'''-9(-6)'''</span>",
            punishAdv = "<span style=\"color: #30D5B8;\">'''+5(+8)'''</span>",
            perfParryAdv = "<span style=\"color: #b70c0b;\">'''-27'''</span>",
            DRcancelHit = "{{{DRcancelHit}}}",
            DRcancelBlk = "{{{DRcancelBlk}}}",
            afterDRHit = "{{{afterDRHit}}}",
            afterDRBlk = "{{{afterDRBlk}}}",
            hitstun = "31",
            blockstun = "21",
            hitstop = "12,12",
            driveDmgBlk = "3000",
            driveDmgHit = "[5000]",
            driveGain = "-20000",
            superGainHit = "600x2 (420x2)",
            superGainBlk = "300 (150)",
            invuln = "{{{invuln}}}",
            armor = "{{{armor}}}",
            airborne = "10-26 (FKD)",
            jugStart = "2,1 air",
            jugIncrease = "1,2",
            jugLimit = "3,11",
            projSpeed = "{{{projSpeed}}}",
            atkRange = "3.299 (3.198)",
            notes = "Can dodge lows until frame 32; initial active frames 2(7)1 only hit airborne opponents for an OTG bounce and side switch; next 3 active frames only hit standing opponents; 3f better hit/block advantage vs. crouching opponents (allows link into 2LP); cannot hit cross-up"
        )
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(MoveListResponseDto.Title(moveDto))
        )
        val expectedMove = Move(
            charName = "Ken",
            id = "ken_623kk",
            name = "Dragonlash Kick",
            input = "623kk",
            damage = "500,700",
            startup = "9(19)",
            onBlock = "-9(-6)",
            onHit = "+1(+4)",
            onCH = null,
            recovery = "3+21 land",
            active = "2(7)6",
            guard = "LH",
            cancel = null,
            invulnerability = null,
            notes = listOf(
                "Can dodge lows until frame 32",
                "initial active frames 2(7)1 only hit airborne opponents for an OTG bounce and side switch",
                "next 3 active frames only hit standing opponents",
                "3f better hit/block advantage vs. crouching opponents (allows link into 2LP)",
                "cannot hit cross-up"
            ),
            aliases = emptyList(),
            urls = Move.Urls(hitboxImage = null),
            sf6Properties = Move.SF6Properties(
                type = "special",
                images = listOf("SF6_Ken_623kk.png"),
                chip = "175",
                dmgScaling = "Combo (5% extra); both hits apply scaling",
                total = "47",
                hitConfirm = null,
                punishAdv = "+5(+8)",
                perfParryAdv = "-27",
                DRcOH = null,
                DRcOB = null,
                DROH = null,
                DROB = null,
                hitStun = "31",
                blockStun = "21",
                hitStop = "12,12",
                driveDmgOnBlock = "3000",
                driveDmgOnHit = "[5000]",
                driveGain = "-20000",
                superGainOnHit = "600x2 (420x2)",
                superGainOnBlock = "300 (150)",
                armor = null,
                jugStart = "2,1 air",
                jugIncrease = "1,2",
                jugLimit = "3,11",
                projectileSpeed = null,
                attackRange = "3.299 (3.198)"
            )
        )

        // when
        val result = responseDto.toDomain(emptyMap())

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedMove)
    }

    @Test
    fun `toDomain should map move with multiple hitboxes and image URL lookup`() {
        // given
        val moveDto = MoveDto(
            moveId = "ken_236236k",
            moveType = "super",
            chara = "Ken",
            input = "236236K",
            name = "Shippu Jinrai-kyaku",
            images = "SF6_Ken_236236k.png",
            hitboxes = "SF6_Ken_236236k_hitbox.png, SF6_Ken_236236k_hitbox2.png, SF6_Ken_236236k_hitbox3.png",
            damage = "2800",
            chip = "150x5 (750)",
            dmgScaling = "40% Minimum",
            startup = "6",
            active = "2(10)3(10)3(12)3(11)2",
            recovery = "28",
            total = "89",
            guard = "LH",
            cancel = "-",
            hitconfirm = "{{{hitconfirm}}}",
            hitAdv = "KD +36",
            blockAdv = "<span style=\"color: #b70c0b;\">'''-5'''</span>",
            punishAdv = "KD +36",
            perfParryAdv = "<span style=\"color: #b70c0b;\">'''-28'''</span>",
            DRcancelHit = "{{{DRcancelHit}}}",
            DRcancelBlk = "{{{DRcancelBlk}}}",
            afterDRHit = "{{{afterDRHit}}}",
            afterDRBlk = "{{{afterDRBlk}}}",
            hitstun = "{{{hitstun}}}",
            blockstun = "79 total",
            hitstop = "4x4,15,2x4,5",
            driveDmgBlk = "1000x5",
            driveDmgHit = "1000x10",
            driveGain = "{{{driveGain}}}",
            superGainHit = "-20000",
            superGainBlk = "-20000",
            invuln = "{{{invuln}}}",
            armor = "Break",
            airborne = "{{{airborne}}}",
            jugStart = "1",
            jugIncrease = "1",
            jugLimit = "99",
            projSpeed = "{{{projSpeed}}}",
            atkRange = "3.631 (1.979)",
            notes = "Fast startup and good range, making it good for punishes and juggle combos; completely safe on block due to pushback; great chip tool vs. opponents in Burnout (can continue dealing more chip after being safely blocked); Full Dmg distribution: 300x4,200x5,600"
        )
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(MoveListResponseDto.Title(moveDto))
        )
        val imageUrlMap = mapOf(
            "SF6_Ken_236236k_hitbox.png" to "https://example.com/hitbox1.png"
        )
        val expectedMove = Move(
            charName = "Ken",
            id = "ken_236236k",
            name = "Shippu Jinrai-kyaku",
            input = "236236k",
            damage = "2800",
            startup = "6",
            onBlock = "-5",
            onHit = "KD +36",
            onCH = null,
            recovery = "28",
            active = "2(10)3(10)3(12)3(11)2",
            guard = "LH",
            cancel = null,
            invulnerability = null,
            notes = listOf(
                "Fast startup and good range, making it good for punishes and juggle combos",
                "completely safe on block due to pushback",
                "great chip tool vs. opponents in Burnout (can continue dealing more chip after being safely blocked)",
                "Full Dmg distribution: 300x4,200x5,600"
            ),
            aliases = emptyList(),
            urls = Move.Urls(hitboxImage = "https://example.com/hitbox1.png"),
            sf6Properties = Move.SF6Properties(
                type = "super",
                images = listOf("SF6_Ken_236236k.png"),
                chip = "150x5 (750)",
                dmgScaling = "40% Minimum",
                total = "89",
                hitConfirm = null,
                punishAdv = "KD +36",
                perfParryAdv = "-28",
                DRcOH = null,
                DRcOB = null,
                DROH = null,
                DROB = null,
                hitStun = null,
                blockStun = "79 total",
                hitStop = "4x4,15,2x4,5",
                driveDmgOnBlock = "1000x5",
                driveDmgOnHit = "1000x10",
                driveGain = null,
                superGainOnHit = "-20000",
                superGainOnBlock = "-20000",
                armor = "Break",
                jugStart = "1",
                jugIncrease = "1",
                jugLimit = "99",
                projectileSpeed = null,
                attackRange = "3.631 (1.979)"
            )
        )

        // when
        val result = responseDto.toDomain(imageUrlMap)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedMove)
    }

    @Test
    fun `toDomain should filter dash values and parse recovery with HTML`() {
        // given
        val moveDto = MoveDto(
            moveId = "blanka_28kk",
            moveType = "special",
            chara = "Blanka",
            input = "[2]8KK",
            name = "Vertical Rolling Attack",
            images = "SF6_Blanka_28kk.png",
            hitboxes = "SF6_Blanka_28kk_hitbox.png",
            damage = "800x2",
            chip = "200",
            dmgScaling = "{{{dmgScaling}}}",
            startup = "7",
            active = "8(2)6",
            recovery = "31+17 land<br>(34+23 land oB)<br>(44+7 land oH)",
            total = "70",
            guard = "LH",
            cancel = "-",
            hitconfirm = "{{{hitconfirm}}}",
            hitAdv = "KD +28",
            blockAdv = "<span style=\"color: #b70c0b;\">'''-40'''</span>",
            punishAdv = "KD +28",
            perfParryAdv = "<span style=\"color: #b70c0b;\">'''-56'''</span>",
            DRcancelHit = "{{{DRcancelHit}}}",
            DRcancelBlk = "{{{DRcancelBlk}}}",
            afterDRHit = "{{{afterDRHit}}}",
            afterDRBlk = "{{{afterDRBlk}}}",
            hitstun = "{{{hitstun}}}",
            blockstun = "18",
            hitstop = "14,12 / 20",
            driveDmgBlk = "4000",
            driveDmgHit = "{{{driveDmgHit}}}",
            driveGain = "-20000",
            superGainHit = "500x2 (350x2)",
            superGainBlk = "250 (125)",
            invuln = "1-10 Full",
            armor = "{{{armor}}}",
            airborne = "4-53 (FKD)",
            jugStart = "1",
            jugIncrease = "1/10",
            jugLimit = "6/8",
            projSpeed = "{{{projSpeed}}}",
            atkRange = "0.85 (1st)",
            notes = "Trajectory: up and slightly forward (less horizontal distance than MK version); 40f charge time; cannot hit cross-up"
        )
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(MoveListResponseDto.Title(moveDto))
        )
        val expectedMove = Move(
            charName = "Blanka",
            id = "blanka_28kk",
            name = "Vertical Rolling Attack",
            input = "[2]8kk",
            damage = "800x2",
            startup = "7",
            onBlock = "-40",
            onHit = "KD +28",
            onCH = null,
            recovery = "31+17 land(34+23 land oB)(44+7 land oH)",
            active = "8(2)6",
            guard = "LH",
            cancel = null,
            invulnerability = "1-10 Full",
            notes = listOf(
                "Trajectory: up and slightly forward (less horizontal distance than MK version)",
                "40f charge time",
                "cannot hit cross-up"
            ),
            aliases = emptyList(),
            urls = Move.Urls(hitboxImage = null),
            sf6Properties = Move.SF6Properties(
                type = "special",
                images = listOf("SF6_Blanka_28kk.png"),
                chip = "200",
                dmgScaling = null,
                total = "70",
                hitConfirm = null,
                punishAdv = "KD +28",
                perfParryAdv = "-56",
                DRcOH = null,
                DRcOB = null,
                DROH = null,
                DROB = null,
                hitStun = null,
                blockStun = "18",
                hitStop = "14,12 / 20",
                driveDmgOnBlock = "4000",
                driveDmgOnHit = null,
                driveGain = "-20000",
                superGainOnHit = "500x2 (350x2)",
                superGainOnBlock = "250 (125)",
                armor = null,
                jugStart = "1",
                jugIncrease = "1/10",
                jugLimit = "6/8",
                projectileSpeed = null,
                attackRange = "0.85 (1st)"
            )
        )

        // when
        val result = responseDto.toDomain(emptyMap())

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedMove)
    }
//endregion
}