package io.github.sophon.wiki.adapter.outbound.ktor.dustLoop

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import kotlin.test.Test

class DustLoopMoveRemoteMapperTest {

    //region wiki url
    @Test
    fun `wiki url anchors on the move name`() {
        //given
        val dto = DustLoopMoveSource.magicalBat
        val expected = "https://www.dustloop.com/w/BBCF/Platinum_the_Trinity#Magical_Bat"

        //when
        val result = dto.toMove(Game.BBCF, DustLoopMoveSource.platinum)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `wiki url anchors on the input when the move has no name`() {
        //given
        val dto = DustLoopMoveSource.dLv2
        val expected = "https://www.dustloop.com/w/BBCF/Makoto_Nanaya#5D_Lv2"

        //when
        val result = dto.toMove(Game.BBCF, DustLoopMoveSource.makoto)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }
    //endregion

    //region input
    @Test
    fun `input keeps the wiki notation`() {
        //given
        val dto = DustLoopMoveSource.closeSlash
        val expected = "c.S"

        //when
        val result = dto.toMove(Game.GGST, DustLoopMoveSource.sol)

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `no aliases are created`() {
        //given
        val dto = DustLoopMoveSource.closeSlash
        val expected = emptyList<String>()

        //when
        val result = dto.toMove(Game.GGST, DustLoopMoveSource.sol)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `remote ID is empty - DustLoop has no move ID`() {
        //given
        val dto = DustLoopMoveSource.closeSlash

        //when
        val result = dto.toMove(Game.GGST, DustLoopMoveSource.sol)

        //then
        assertThat(result.remoteId).isNull()
    }
    //endregion

    @Test
    fun `hitbox file names are split by semicolon`() {
        //given
        val dto = DustLoopMoveSource.magicalBat
        val imageUrlMap = mapOf(
            "BBCF Platinum homerun5D hitbox 1.png" to "https://www.dustloop.com/wiki/images/BBCF_Platinum_homerun5D_hitbox_1.png",
            "BBCF Platinum homerun5D hitbox 2.png" to "https://www.dustloop.com/wiki/images/BBCF_Platinum_homerun5D_hitbox_2.png",
        )
        val expected = listOf(
            "https://www.dustloop.com/wiki/images/BBCF_Platinum_homerun5D_hitbox_1.png",
            "https://www.dustloop.com/wiki/images/BBCF_Platinum_homerun5D_hitbox_2.png",
        )

        //when
        val result = dto.toMove(Game.BBCF, DustLoopMoveSource.platinum, imageUrlMap)

        //then
        assertThat(result.urls.hitboxImageList).isEqualTo(expected)
    }
}

private fun MoveDto.toMove(
    game: Game,
    character: Character,
    imageUrlMap: Map<String, String> = emptyMap(),
): Move {
    val responseDto = DustLoopMoveListResponseDto(cargoQuery = listOf(MoveQueryItem(this)))
    val move = responseDto.toDomain(game, character, imageUrlMap).single()
    return move
}

private object DustLoopMoveSource {
    val sol = Character(
        id = CharacterId("Sol Badguy"),
        displayName = "Sol Badguy",
        remoteQueryId = "Sol Badguy",
        wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy",
    )
    val platinum = Character(
        id = CharacterId("Platinum the Trinity"),
        displayName = "Platinum the Trinity",
        remoteQueryId = "Platinum the Trinity",
        wikiUrl = "https://www.dustloop.com/w/BBCF/Platinum_the_Trinity",
    )
    val makoto = Character(
        id = CharacterId("Makoto Nanaya"),
        displayName = "Makoto Nanaya",
        remoteQueryId = "Makoto Nanaya",
        wikiUrl = "https://www.dustloop.com/w/BBCF/Makoto_Nanaya",
    )

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
        notes = "Counter Hit state for entire move; Reversal",
    )
    val dLv2 = MoveDto(
        chara = "Makoto Nanaya",
        name = null,
        input = "5D Lv2",
        damage = "850",
        guard = "Mid",
        startup = "17",
        active = "3",
        recovery = "31",
        onBlock = "-15",
        onODR = null,
        attribute = "B",
        invuln = null,
        cancel = "SR",
        p1 = "90",
        p2 = "80",
        starter = "Long",
        level = "4",
        blockstun = "18",
        groundHit = "19",
        airHit = "30",
        groundCH = "Crumple 58",
        airCH = "45",
        blockstop = "12",
        hitstop = "+0",
        CHstop = "+5",
        cancelTiming = null,
        images = "BBCF_Makoto_Nanaya_5D.png",
        caption = "&#32;",
        hitboxes = "BBCF_Makoto_Nanaya_5D_Lv2_Hitbox.png",
        hitboxCaption = "Level 2 Hitbox",
        type = "drive",
        notes = "Startup is fastest possible, can be extended by holding button past Lv3;Slowest possible startup is 29;On CH Crumple Duration 58F, Crumple Fall 81F;",
    )
}
