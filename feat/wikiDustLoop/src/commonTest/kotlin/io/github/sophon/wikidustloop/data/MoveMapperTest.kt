package io.github.sophon.wikidustloop.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import kotlin.test.Test

class MoveMapperTest {
    val gb = Game.GBVSR.id
    val gg = Game.GGST.id
    val bb = Game.BBCF.id
    val emptyCharData = DownloadMoveListUseCase.CharacterData("", null)

    //region Aliases
    @Test
    fun `formAliases handles empty form input`() {
        //given
        val input = "fl[]"
        val expectedAlias = listOf("f.l[]")

        //when
        val resultAlias = formAliases(gb, input, "")

        //then
        assertThat(resultAlias).isEqualTo(expectedAlias)
    }
    
    @Test
    fun `formAliases handles gb variant inputs`() {
        // given
        val input = "22m~l/m"
        val expected = listOf("22m~l", "22m~m")
        
        // when
        val result = formAliases(gb, input, "")

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles release notation`() {
        // given
        val input = "214]p["
        val expected = listOf("214p")

        // when
        val result = formAliases(gg, input, "")

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    @Test
    fun `formWikiUrl handles spaces in inputs`() {
        // given
        val expected = "https://www.dustloop.com/w/BBCF/Platinum_the_Trinity#Magical_Bat"
        val dto = MoveSource.magicalBat

        // when
        val result = formMoveWikiUrl(bb, dto)

        //then
        assertThat(result).isEqualTo(expected)
    }
    
    @Test
    fun `toDomain handles close input`() {
        // given
        val move = MoveSource.closeSlash
        val expectedInput = "cs"
        val expectedAlias = listOf("c.s", "cls", "cl.s")
        
        // when
        val result = move.toDomain(
            gameId = gg,
            characterData = emptyCharData,
            imageUrlMap = emptyMap(),
        )

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `toDomain handles OR input`() {
        // given
        val move = MoveSource.airThrow
        val expectedInput = "j6d/j4d"
        val expectedAlias = listOf("j6d", "j4d", "j.6d", "j.4d")

        // when
        val result = move.toDomain(gg, emptyCharData, emptyMap())

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `toDomain handles Nago input aliases`() {
        // given
        val move1 = MoveSource.hLevel1
        val expectedAlias1 = listOf("2h")
        val move3 = MoveSource.sLevel3
        val expectedAlias3 = listOf("2s3")
        val moveB = MoveSource.hBr
        val expectedAliasB = listOf("2hb")

        // when
        val result1 = move1.toDomain(gg, emptyCharData, emptyMap())
        val result3 = move3.toDomain(gg, emptyCharData, emptyMap())
        val resultB = moveB.toDomain(gg, emptyCharData, emptyMap())

        //then
        assertThat(result1.aliases).isEqualTo(expectedAlias1)
        assertThat(result3.aliases).isEqualTo(expectedAlias3)
        assertThat(resultB.aliases).isEqualTo(expectedAliasB)
    }

    @Test
    fun `toDomain handles Narmaya stances`() {
        // given
        val moveK = MoveSource.fhk
        val expectedK = listOf("f.h[k]","k.fh")
        val moveG = MoveSource.fhg
        val expectedG = listOf("f.h[g]", "g.fh")

        // when
        val resultK = moveK.toDomain(gb, emptyCharData, emptyMap())
        val resultG = moveG.toDomain(gb, emptyCharData, emptyMap())

        //then
        assertThat(resultK.aliases).isEqualTo(expectedK)
        assertThat(resultG.aliases).isEqualTo(expectedG)
    }
}

private object MoveSource {
    val closeSlash = MoveDto(
        chara = "Sol Badguy",
        name = null,
        input = "c.S",
        damage = "44",
        guard = "All",
        startup = "7",
        active = "6",
        recovery = "10",
        onBlock = "+3",
        onHit = "+13",
        level = "4",
        counter = "Mid",
        images = "GGST Sol Badguy cS.png",
        hitboxes = "GGST Sol cS Hitbox.png",
        notes = "Input Proximity Range: 240;Hitstop on ground hit: 16F; Floating crumple on ground hit: Total 28F (airborne hitstun 1-11F, standing hitstun 12-18F, can block 19~28F)",
        type = "normal",
        riscGain = "1700",
        riscLoss = "1000",
        wallDamage = "300",
        inputTension = null,
        chipRatio = null,
        OTGType = "Up",
        prorate = "100%",
        invuln = null,
        cancel = "SJDRP",
        caption = "Kills your opponent on block or hit",
        hitboxCaption = "The sword also has a hitbox for some reason",
    )
    val airThrow = MoveDto(
        chara = "Sol Badguy",
        name = "Air Throw",
        input = "j.6D or j.4D",
        damage = "80",
        guard = "Air Throw",
        startup = "2",
        active = "3",
        recovery = "38 or Until Landing+10",
        onBlock = null,
        onHit = "HKD +50 (IAD)",
        level = null,
        counter = null,
        images = "GGST Sol Badguy Air Throw.png",
        hitboxes = "GGST Sol Badguy Air Throw Hitbox.png",
        notes = "Air Throws have both normal recovery (which recovers CH) and special recovery frames upon landing (not CH). It is technically possible to recover mid-air from a whiffed Air Throw, but it usually doesn't happen unless done from very high in the air.;Opponent cannot use Blue Psych Burst on hit or the resulting knockdown.;On-Hit value is for attacker's IAD height, except for Potemkin and Nagoriyuki, who have their instant Air Throw height instead.;Frame advantage decreases as height increases (+43 to +56).;Builds 1000 Tension once all damage is dealt.;On hit, applies an increased 25 Combo Decay.;Properties change if Roman Canceled before all damage is dealt:;* Reduces positive R.I.S.C. by 25% before applying R.I.S.C. Loss, similar to regular strikes.;* R.I.S.C. Loss: 1000;* Proration: Forced 75%;* Combo Decay on hit: 15",
        type = "other",
        riscGain = null,
        riscLoss = "2500",
        wallDamage = "1000",
        inputTension = null,
        chipRatio = null,
        OTGType = null,
        prorate = "Forced 50%",
        invuln = null,
        cancel = "RP",
        caption = "&#32;",
        hitboxCaption = "&#32;",
    )
    val hLevel1 = MoveDto(
        chara = "Nagoriyuki",
        name = "Level 1",
        input = "2H Level 1",
        damage = "50",
        guard = "All (Guard Crush)",
        startup = "19",
        active = "7",
        recovery = "29",
        onBlock = "-11",
        onHit = "HKD +48",
        level = "4",
        counter = "Large",
        images = "GGST Nagoriyuki 2HComparison.png",
        hitboxes = "GGST_Nagoriyuki_2H1-Hitbox.png",
        notes = "Lowers Blood Gauge on block/hit over 6 seconds (OB -7.2/300, OH -57.6/300);Hitstop: 25F",
        type = "normal",
        riscGain = "1700",
        riscLoss = "1000",
        wallDamage = "500",
        inputTension = null,
        chipRatio = "25%",
        OTGType = "Up",
        prorate = "90%",
        invuln = null,
        cancel = null,
        caption = "&#32;",
        hitboxCaption = "Has a blind spot above the head",
    )
    val sLevel3 = MoveDto(
        chara = "Nagoriyuki",
        name = "Level 3",
        input = "2S Level 3",
        damage = "42",
        guard = "Low",
        startup = "10",
        active = "4",
        recovery = "21",
        onBlock = "-11",
        onHit = "-8",
        level = "2",
        counter = "Mid",
        images = "GGST Nagoriyuki 2S.png",
        hitboxes = "GGST_Nagoriyuki_2S-Hitbox.png",
        notes = "Lowers Blood Gauge on hit over 6 seconds (-7.2/300);8 Chip Damage on block",
        type = "normal",
        riscGain = "825",
        riscLoss = "1000",
        wallDamage = "300",
        inputTension = null,
        chipRatio = "20%",
        OTGType = "Mid",
        prorate = "90%",
        invuln = null,
        cancel = null,
        caption = "&#32;",
        hitboxCaption = "&#32;",
    )
    val hBr = MoveDto(
        chara = "Nagoriyuki",
        name = "Blood Rage",
        input = "2H Level BR",
        damage = "143",
        guard = "All (Guard Crush)",
        startup = "15",
        active = "7",
        recovery = "29",
        onBlock = "-11",
        onHit = "HKD +48",
        level = "4",
        counter = "Large",
        images = "GGST Nagoriyuki 2H2.png",
        hitboxes = "GGST_Nagoriyuki_2HBR-Hitbox.png",
        notes = null,
        type = "normal",
        riscGain = "1700",
        riscLoss = "1000",
        wallDamage = "500",
        inputTension = null,
        chipRatio = "25%",
        OTGType = "Up",
        prorate = "90%",
        invuln = null,
        cancel = null,
        caption = "The no-fly zone",
        hitboxCaption = "&#32;",
    )

    val fhg = MoveDto(
        chara = "Narmaya",
        name = null,
        input = "f.H[g]",
        damage = "1300",
        guard = "Mid",
        startup = "10",
        active = "6",
        recovery = "22",
        onBlock = "<span style=\"color: \n#c71c1b\" >'''-11'''</span>",
        onHit = "<span style=\"color: \n#c71c1b\" >'''-7'''</span>",
        onCH = "<span style=\"color: \n#00d7c0\" >'''+1'''</span>",
        meter = null,
        level = "3",
        invuln = null,
        cooldown = null,
        cls = "2",
        images = "GBVSR_Narmaya_fH.png",
        caption = "One-hit-confirm fishing rod<br><span class=\"colorful-text-4\" >f.H[g]</span>",
        hitboxes = "GBVSR_Narmaya_fH_Hitbox.png\\GBVSR_Narmaya_fH_Hitbox_2.png",
        hitboxCaption = "Frames 10-12\\Frames 13-15<br><span class=\"colorful-text-4\" >f.H[g]</span>",
        type = "normal[g]",
        notes = null,
    )
    val fhk = MoveDto(
        chara = "Narmaya",
        name = null,
        input = "f.H[k]",
        damage = "1300",
        guard = "Mid",
        startup = "9",
        active = "6",
        recovery = "20",
        onBlock = "<span style=\"color: \n#c71c1b\" >'''-9'''</span>",
        onHit = "<span style=\"color: \n#c71c1b\" >'''-5'''</span>",
        onCH = "<span style=\"color: \n#00d7c0\" >'''+3'''</span>",
        meter = null,
        level = "3",
        invuln = null,
        cooldown = null,
        cls = "2",
        images = "GBVSR_Narmaya_fH_Kagura.png",
        caption = "<span class=\"colorful-text-4\" >f.H[k]</span>",
        hitboxes = "GBVSR_Narmaya_fH_Kagura_Hitbox.png\\GBVSR_Narmaya_fH_Kagura_Hitbox_2.png",
        hitboxCaption = "Frames 9-11\\Frames 12-14<br><span class=\"colorful-text-4\" >f.H[k]</span>",
        type = "normal[k]",
        notes = null,
    )
    
    val magicalBat = MoveDto(
        chara = "Platinum the Trinity",
        name = "Magical Bat",
        input = "5D Bat",
        damage = "900",
        guard = "Mid",
        startup = "11",
        active = "4",
        recovery = "26",
        onBlock = "-16",
        onODR = "-14",
        attribute = "B",
        invuln = "1~11 All",
        cancel = "(S)R",
        p1 = "60",
        p2 = "75",
        starter = "Very Short",
        level = "2",
        blockstun = "13",
        groundHit = "Launch",
        airHit = "30 + WStick 25",
        groundCH = "Launch",
        airCH = "42 + WBounce 40 + WStick 25",
        blockstop = "22",
        hitstop = "+0",
        CHstop = "+1",
        cancelTiming = null,
        images = "BBCS_Platinum_homerun5D.png",
        caption = "Also good in combos",
        hitboxes = "BBCF Platinum homerun5D hitbox 1.png;BBCF Platinum homerun5D hitbox 2.png",
        hitboxCaption = "Ground, frame 11\\Ground, frames 12-14",
        type = "drive",
        notes = "Counter Hit state for entire move; Reversal"
    )
}