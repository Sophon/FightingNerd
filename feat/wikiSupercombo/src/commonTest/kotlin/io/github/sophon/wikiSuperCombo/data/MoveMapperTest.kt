package io.github.sophon.wikiSuperCombo.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import kotlin.test.Test

class MoveMapperTest {
    val gameIdSF6 = "Street_Fighter_6"
    val gameIdMK1 = "Mortal_Kombat_1"
    val emptyCharData = DownloadMoveListUseCase.CharacterData("", null)
    
    //region formMoveWikiUrl
    @Test
    fun `formUrl handles basic url`() {
        //given
        val charName = "A.K.I."
        val input = "2MP"
        val name = null
        val expected = "https://wiki.supercombo.gg/w/Street_Fighter_6/A.K.I.#2MP"
        
        //when
        val result = formMoveWikiUrl(gameIdSF6, charName, input, name)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formUrl handles move with name`() {
        //given
        val charName = "Chun-Li"
        val input = "214P~MK"
        val name = "Senpu Kick"
        val expected = "https://wiki.supercombo.gg/w/Street_Fighter_6/Chun-Li#Senpu_Kick_(214P~MK)"

        //when
        val result = formMoveWikiUrl(gameIdSF6, charName, input, name)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formUrl handles MK moves`() {
        //given
        val charName = "Kung Lao"
        val input = "121"
        val name = "Swollen Throat"
        val expected = "https://wiki.supercombo.gg/w/Mortal_Kombat_1/Kung_Lao/Data#121"

        //when
        val result = formMoveWikiUrl(gameIdMK1, charName, input, name)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formUrl handles Blanka rolling cannon`() {
        // given
        val move = MoveSource.rollingCannon
        val expected = ""

        // when
        val result = move.toDomain(gameIdSF6, emptyCharData, emptyMap())

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }
    //endregion

    //region Aliases
    @Test
    fun `formAliases handles motion input`() {
        // given
        val hadoken = MoveSource.hadoken
        val expected = listOf("qcfhp")

        // when
        val result = hadoken.toDomain(
            gameIdSF6,
            characterData = emptyCharData,
            imageUrlMap = emptyMap(),
        )

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles crouching input`() {
        // given
        val move = MoveSource.crHP
        val expected = listOf("crhp")

        // when
        val result = move.toDomain(gameIdSF6, emptyCharData, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    
    @Test
    fun `formAliases handles SA`() {
        // given
        val sa1 = MoveSource.sa1
        val expected = listOf("sa1")
        
        // when
        val result = sa1.toDomain(
            gameIdSF6,
            characterData = emptyCharData,
            imageUrlMap = emptyMap(),
        )

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `formAliases ignores CA`() {
        // given
        val ca = MoveSource.ca
        val expected = emptyList<String>()

        // when
        val result = ca.toDomain(
            gameIdSF6,
            characterData = emptyCharData,
            imageUrlMap = emptyMap(),
        )

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion
}

private object MoveSource {
    val hadoken = MoveDto(
        moveId = "ken_236hp",
        moveType = "special",
        chara = "Ken",
        input = "236HP",
        name = "Hadoken",
        images = "SF6_Ken_236hp.png",
        hitboxes = "SF6_Ken_236hp_hitbox.png",
        damage = "600",
        chip = "150",
        dmgScaling = null,
        startup = "12",
        active = "-",
        recovery = "37",
        total = "49",
        guard = "LH",
        cancel = "SA3",
        hitconfirm = "4",
        hitAdv = "-5",
        blockAdv = "-11",
        punishAdv = "-1",
        perfParryAdv = "-27",
        DRcancelHit = null,
        DRcancelBlk = null,
        afterDRHit = null,
        afterDRBlk = null,
        hitstun = "33",
        blockstun = "27",
        hitstop = "8",
        driveDmgBlk = "2500",
        driveDmgHit = "[2000]",
        driveGain = "1000",
        superGainHit = "600 (420)",
        superGainBlk = "300 (150)",
        invuln = null,
        armor = null,
        airborne = null,
        jugStart = "1",
        jugIncrease = "1",
        jugLimit = "1",
        projSpeed = "0.08",
        atkRange = null,
        notes = "1-hit projectile; puts airborne opponents into limited juggle state",
    )
    val shoryuken = MoveDto(
        moveId = "ken_623hp",
        moveType = "special",
        chara = "Ken",
        input = "623HP",
        name = "Shoryuken",
        images = "SF6_Ken_623hp.png",
        hitboxes = "SF6_Ken_623hp_hitbox.png",
        damage = "800,300x2 (1400)",
        chip = "200,75x2 (350)",
        dmgScaling = "20% Starter",
        startup = "7",
        active = "10",
        recovery = "35+15 land",
        total = "66",
        guard = "LH",
        cancel = "SA3",
        hitconfirm = "14",
        hitAdv = "KD +25",
        blockAdv = "-36(-38)",
        punishAdv = "KD +25",
        perfParryAdv = "-58",
        DRcancelHit = null,
        DRcancelBlk = null,
        afterDRHit = null,
        afterDRBlk = null,
        hitstun = null,
        blockstun = "24 total (22 crouch)",
        hitstop = "12,6,6",
        driveDmgBlk = "1400x3",
        driveDmgHit = "[5000]",
        driveGain = "2000,1000x2",
        superGainHit = "400x3 (280x3)",
        superGainBlk = "200x3 (100x3)",
        invuln = "1-9 Air, 5-11 Projectile",
        armor = null,
        airborne = "9-51 (FKD)",
        jugStart = "1x3",
        jugIncrease = "1,0,0",
        jugLimit = "6,8,8",
        projSpeed = null,
        atkRange = "1.326 (1st)",
        notes = "Good anti-air; 3rd hit whiffs vs. crouch block (2f worse advantage, 75 less chip dmg); cannot hit cross-up",
    )
    val sa1 = MoveDto(
        moveId = "ken_214214k",
        moveType = "super",
        chara = "Ken",
        input = "214214K",
        name = "Dragonlash Flame",
        images = "SF6_Ken_214214k.png",
        hitboxes = "SF6_Ken_214214k_hitbox.png",
        damage = "2000",
        chip = "500",
        dmgScaling = "30% Minimum",
        startup = "7",
        active = "3",
        recovery = "41",
        total = "50",
        guard = "LH",
        cancel = "-",
        hitconfirm = null,
        hitAdv = "KD +9",
        blockAdv = "-24",
        punishAdv = "KD +9",
        perfParryAdv = "-42",
        DRcancelHit = null,
        DRcancelBlk = null,
        afterDRHit = null,
        afterDRBlk = null,
        hitstun = null,
        blockstun = "20",
        hitstop = "15",
        driveDmgBlk = "2500",
        driveDmgHit = "5000",
        driveGain = null,
        superGainHit = "-10000",
        superGainBlk = "-10000",
        invuln = "1-9 Strike/Throw",
        armor = "Break",
        airborne = null,
        jugStart = "30",
        jugIncrease = "30",
        jugLimit = "99",
        projSpeed = null,
        atkRange = "1.587",
        notes = "Switches sides with opponent on hit; Full Dmg distribution: 500,300x2,900",
    )
    val ca = MoveDto(
        moveId = "ken_236236p(ca)",
        moveType = "super",
        chara = "Ken",
        input = "236236P",
        name = "Shinryu Reppa (CA)",
        images = "SF6_Ken_236236p(ca).png",
        hitboxes = "SF6_Ken_236236p(ca)_hitbox.png",
        damage = "4500 (2000~2600)",
        chip = "1450(1250)",
        dmgScaling = "50% Minimum; 10% Immediate (Sp)",
        startup = "7",
        active = "3(1)6(16)13",
        recovery = "30+25 land",
        total = "100",
        guard = "LH",
        cancel = "-",
        hitconfirm = null,
        hitAdv = "HKD +21",
        blockAdv = "-40(-43)",
        punishAdv = "HKD +21",
        perfParryAdv = "-66",
        DRcancelHit = null,
        DRcancelBlk = null,
        afterDRHit = null,
        afterDRBlk = null,
        hitstun = null,
        blockstun = "54 total (51 crouch)",
        hitstop = "2,5x4 / 5x7,15",
        driveDmgBlk = "4000,3000x4",
        driveDmgHit = "20000(1000x7,10000)",
        driveGain = null,
        superGainHit = "-30000",
        superGainBlk = "-30000",
        invuln = "1-18 Full",
        armor = "Break",
        airborne = "10-27, 35-75 (FKD)",
        jugStart = "[1]",
        jugIncrease = "[0]",
        jugLimit = "99 [99]",
        projSpeed = null,
        atkRange = "1.131 (1st)",
        notes = "1st hit leads to full animation (non-cinematic damage varies based on juggle height, and allows a follow-up juggle); 2 hits whiff on crouch block (reduced chip, slightly worse advantage); available at 25% HP or below; cinematic time regenerates ~2.1 Drive bars for Ken",
    )
    val rollingCannon = MoveDto(
        moveId = "blanka_xp",
        moveType = "super",
        chara = "Blanka",
        input = "Any Direction + P (during SA2)",
        name = "Rolling Cannon",
        images = "SF6_Blanka_xp.png",
        hitboxes = "SF6_Blanka_xp_hitbox.png",
        damage = "400",
        chip = "100",
        dmgScaling = null,
        startup = "3",
        active = "25",
        recovery = "7(9) land",
        total = "-",
        guard = "LH",
        cancel = "Sp*",
        hitconfirm = "41*",
        hitAdv = "KD~",
        blockAdv = "-",
        punishAdv = "KD~",
        perfParryAdv = "-",
        DRcancelHit = null,
        DRcancelBlk = null,
        afterDRHit = null,
        afterDRBlk = null,
        hitstun = null,
        blockstun = "17 (3P: 18)",
        hitstop = "15",
        driveDmgBlk = "1000 each",
        driveDmgHit = "2000 each",
        driveGain = "1000 oH (500 oB)",
        superGainHit = null,
        superGainBlk = null,
        invuln = null,
        armor = null,
        airborne = null,
        jugStart = "1",
        jugIncrease = "2 each",
        jugLimit = "99",
        projSpeed = null,
        atkRange = null,
        notes = "Special move follow-up available during SA2 only (depletes 200f or 13.3% of install time); input with any direction; usable after [4]6P, j.[4]6P, [2]8K, or 63214K; puts opponent into limited juggle state; can chain into itself up to 3 times consecutively if the hits connect (this limit resets if Blanka lands before starting another juggle); frame advantage on KD/block/parry varies significantly depending on height and attack angle (can lead to significant block advantage on 3P or 2P versions); total recovery varies significantly based on input direction, but landing recovery is always 7f for 8P/7P/4P/1P and 9f for 2P/3P/6P/9P; activates Blanka-chan doll with electricity (meterless 1-hit version); POTENTIAL BUG: 2P version canceled from minimum-height Aerial Rolling Attack will grant significant frame advantage without consuming any additional install time",
    )
    val crHP = MoveDto(
        moveId = "ken_2hp",
        moveType = "ground_normal",
        chara = "Ken",
        input = "2HP",
        name = "Crouching Heavy Punch",
        images = "SF6_Ken_2hp.png",
        hitboxes = "SF6_Ken_2hp_hitbox_preview.png, SF6_Ken_2hp_hitbox_1.png, SF6_Ken_2hp_hitbox_2.png",
        damage = "800",
        chip = null,
        dmgScaling = null,
        startup = "8",
        active = "4",
        recovery = "24",
        total = "35",
        guard = "LH",
        cancel = "Sp SA",
        hitconfirm = "16",
        hitAdv = "<span style=\"color: #32CD32;\">+3</span>",
        blockAdv = "<span style=\"color: #b70c0b;\">'''-10'''</span>",
        punishAdv = "<span style=\"color: #30D5B8;\">'''+7'''</span>",
        perfParryAdv = "<span style=\"color: #b70c0b;\">'''-26'''</span>",
        DRcancelHit = "<span style=\"color: #30D5B8;\">'''+21'''</span>",
        DRcancelBlk = "<span style=\"color: #30D5B8;\">'''+8'''</span>",
        afterDRHit = "<span style=\"color: #30D5B8;\">'''+7'''</span>",
        afterDRBlk = "<span style=\"color: #b70c0b;\">'''-6'''</span>",
        hitstun = "31",
        blockstun = "18",
        hitstop = "13",
        driveDmgBlk = "5000",
        driveDmgHit = "[8000]",
        driveGain = "2000",
        superGainHit = "1000 (700)",
        superGainBlk = "500 (250)",
        invuln = null,
        armor = null,
        airborne = null,
        jugStart = "1",
        jugIncrease = "1",
        jugLimit = "0",
        projSpeed = null,
        atkRange = "1.148",
        notes = "Forces stand; decent anti-air (cannot hit cross-up); only first 2 active frames are cancelable; Run~Stop cancel: +3/-10",
    )
}
