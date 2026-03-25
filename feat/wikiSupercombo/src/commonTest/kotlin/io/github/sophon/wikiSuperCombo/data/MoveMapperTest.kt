package io.github.sophon.wikiSuperCombo.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import kotlin.test.Test

class MoveMapperTest {
    val gameIdSF6 = "Street_Fighter_6"
    val gameIdMK1 = "Mortal_Kombat_1"
    
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
            characterData = DownloadMoveListUseCase.CharacterData("", null),
            imageUrlMap = emptyMap(),
        )

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
            characterData = DownloadMoveListUseCase.CharacterData("", null),
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
            characterData = DownloadMoveListUseCase.CharacterData("", null),
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
}
