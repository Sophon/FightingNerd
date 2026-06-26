package io.github.sophon.wikimizuumi.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import kotlin.test.Test

class MoveMapperTest {
    val uni = Game.Uni2.id
    val character = Character(
        id = "",
        displayName = "",
        remoteQueryId = "",
        wikiUrl = "",
    )
    
    //region URL
    @Test
    fun `toDomain handles url`() {
        // given
        val move = MoveSource.ak214a
        val expectedUrl = "[L](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Launch_(L))"

        // when
        val result = move.toDomain(
            character = MoveSource.ak,
            hitboxUrlMap = emptyMap(),
        )

        //then
        assertThat(result.mbProperties?.property).isEqualTo(expectedUrl)
    }

    @Test
    fun `toDomain handles multiple url`() {
        // given
        val move = MoveSource.dn214b
        val expectedUrl = "[L](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Launch_(L)), " +
                "[GB](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Ground_Bounce_(GB)), " +
                "[SK](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Soft_Knockdown_(SK))"

        // when
        val result = move.toDomain(MoveSource.dn, emptyMap())

        //then
        assertThat(result.mbProperties?.property).isEqualTo(expectedUrl)
    }
    //endregion
    
    @Test
    fun `toDomain handles jump and charge`() {
        // given
        val move = MoveSource.lumenStellaAir
        val expectedInput = "j[4]6a"
        val expectedAlias = listOf("j.[4]6a", "j46a")
        
        // when
        val result = move.toDomain(
            character,
            imageUrlMap = emptyMap(),
            gameId = uni,
        )

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }
}

private object MoveSource {
    val ak = Character(
        id = "akiha_tohno",
        displayName = "Akiha Tohno",
        remoteQueryId = "Akiha_Tohno",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno"
    )
    val dn = Character(
        id = "dead_apostle_noel",
        displayName = "Dead Apostle Noel",
        remoteQueryId = "Dead_Apostle_Noel",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Dead_Apostle_Noel"
    )

    val ak214a = MoveDto(
        moveId = "ak_214a",
        chara = "Akiha Tohno",
        input = "214A",
        inputInfo = "",
        name = "",
        subtitle = "",
        images = "MBTL_Akiha_214A.png",
        hitboxes = "MBTL_Akiha_214A_hb.png",
        damage = "440*3 (1320)",
        minDamage = "",
        guard = "LH",
        cancel = "-EX-, -AD-, -MD-",
        property = "[[Melty Blood/MBTL/Glossary#Launch (L)|L]]",
        cost = "",
        attribute = "Strike",
        startup = "20",
        active = "6",
        recovery = "19",
        landing = "",
        overall = "44",
        frameAdv = "-2",
        invul = "4-28 Low Crush",
    )
    val dn214b = MoveDto(
        moveId = "dn_214b",
        chara = "Dead Apostle Noel",
        input = "214B",
        inputInfo = "",
        name = "",
        subtitle = "",
        images = "MBTL_da_noel_214B.png",
        hitboxes = "MBTL_da_noel_214B_fb_hb_2.png",
        damage = "380*3 (1036)",
        minDamage = "",
        guard = "LHA",
        cancel = "EX+",
        property = "[[Melty Blood/MBTL/Glossary#Launch (L)|L]], [[Melty Blood/MBTL/Glossary#Ground Bounce (GB)|GB]], [[Melty Blood/MBTL/Glossary#Soft Knockdown (SK)|SK]]",
        cost = "",
        attribute = "Projectile",
        startup = "<span style=\"cursor:help;   border-bottom:1px dashed;\" title=\"14 in combos, hits ground at 23\">20</span>, <span style=\"cursor:help;   border-bottom:1px dashed;\" title=\"37 in combos, hits ground at 46\">43</span>",
        active = "",
        recovery = "",
        landing = "",
        overall = "55",
        frameAdv = "-2 / +21",
        invul = "",
    )
    val lumenStellaAir = MoveDto(
        moveId = "va_j4_ic_6a",
        chara = "Vatista",
        input = "j[4]6A",
        inputInfo = "",
        name = "Lumen Stella (Air)",
        subtitle = "ルーメンステラ",
        images = "UNI_Vatista_j.46A.png",
        hitboxes = "",
        damage = "819",
        minDamage = "",
        type = "special",
        guard = "All",
        cancel = "CS",
        cancelWindow = "",
        property = "",
        cost = "",
        attribute = "Projectile",
        startup = "13",
        active = "",
        recovery = "",
        landing = "",
        overall = "42",
        frameAdv = "-1",
        onHit = "",
        assaultAdv = "",
        blockstun = "",
        groundHit = "",
        airHit = "",
        groundCH = "",
        airCH = "",
        hitstop = "",
        CHstop = "",
        invul = "",
        proration = "84",
        comboP1 = "77",
        comboP2 = "93",
    )
}
