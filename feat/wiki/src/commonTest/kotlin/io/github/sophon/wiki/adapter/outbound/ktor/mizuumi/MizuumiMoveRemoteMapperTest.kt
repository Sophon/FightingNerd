package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.MBTLMoveProperties
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class MizuumiMoveRemoteMapperTest {

    //region property links
    @Test
    fun `property wiki link becomes a markdown link`() {
        //given
        val move = MizuumiMoveSource.ak214a
        val expected = "[L](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Launch_(L))"

        //when
        val result = move.toMove(Game.MBTL, MizuumiMoveSource.ak)

        //then
        assertThat(result.mbtlProperties?.mizuumiProperty).isEqualTo(expected)
    }

    @Test
    fun `every property wiki link becomes a markdown link`() {
        //given
        val move = MizuumiMoveSource.dn214b
        val expected = "[L](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Launch_(L)), " +
                "[GB](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Ground_Bounce_(GB)), " +
                "[SK](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Soft_Knockdown_(SK))"

        //when
        val result = move.toMove(Game.MBTL, MizuumiMoveSource.dn)

        //then
        assertThat(result.mbtlProperties?.mizuumiProperty).isEqualTo(expected)
    }
    //endregion

    //region input
    @Test
    fun `input is the move ID without the character prefix`() {
        //given
        val moveList = listOf(MizuumiMoveSource.ak5bClose, MizuumiMoveSource.ak5bFar)
        val expected = listOf("5b_close", "5b_far")

        //when
        val result = moveList.toMoveList(Game.Uni2, MizuumiMoveSource.akatsuki)

        //then
        assertThat(result.map { it.input }).isEqualTo(expected)
    }

    @Test
    fun `move ID without the character prefix stays whole`() {
        //given
        val moveList = listOf(MizuumiMoveSource.le214hp, MizuumiMoveSource.le236hp, MizuumiMoveSource.tenraihaAnvil)
        val expected = listOf("214HP", "236HP", "tenraiha_anvil")

        //when
        val result = moveList.toMoveList(Game.VSAV, MizuumiMoveSource.leiLei)

        //then
        assertThat(result.map { it.input }).isEqualTo(expected)
    }

    @Test
    fun `remote ID is the move ID`() {
        //given
        val move = MizuumiMoveSource.lumenStellaAir
        val expected = "va_j4_ic_6a"

        //when
        val result = move.toMove(Game.Uni2, MizuumiMoveSource.va)

        //then
        assertThat(result.remoteId).isEqualTo(expected)
    }

    @Test
    fun `wiki input is kept as an alias`() {
        //given
        val move = MizuumiMoveSource.lumenStellaAir
        val expected = listOf("j[4]6A")

        //when
        val result = move.toMove(Game.Uni2, MizuumiMoveSource.va)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion

    //region bulk
    @Test
    fun `bulk table is grouped into characters by chara`() {
        //given
        val moveList = (movesOf(chara = "Akiha Tohno", idPrefix = "ak", count = 10)
                + movesOf(chara = "Ciel", idPrefix = "ci", count = 10))
        val responseDto = MizuumiMoveListResponseDto(cargoquery = moveList.map { move -> Title(move) })
        val expected = listOf(CharacterId(Game.MBTL, "Akiha Tohno") to 10, CharacterId(Game.MBTL, "Ciel") to 10)

        //when
        val result = responseDto.toDomainAll(Game.MBTL, iconUrlMap = emptyMap(), hitboxUrlMap = emptyMap())

        //then
        assertThat(result.map { (character, characterMoveList) -> character.id to characterMoveList.size })
            .isEqualTo(expected)
    }

    @Test
    fun `chara with fewer than 10 moves is dropped`() {
        //given
        val moveList = (movesOf(chara = "Akiha Tohno", idPrefix = "ak", count = 10)
                + movesOf(chara = "Ciel", idPrefix = "ci", count = 9))
        val responseDto = MizuumiMoveListResponseDto(cargoquery = moveList.map { move -> Title(move) })
        val expected = listOf(CharacterId(Game.MBTL, "Akiha Tohno"))

        //when
        val result = responseDto.toDomainAll(Game.MBTL, iconUrlMap = emptyMap(), hitboxUrlMap = emptyMap())

        //then
        assertThat(result.map { (character, _) -> character.id }).isEqualTo(expected)
    }
    //endregion
}

private val Move.mbtlProperties: MBTLMoveProperties?
    get() = gameProperties as? MBTLMoveProperties

private fun List<MoveDto>.toMoveList(
    game: Game,
    character: Character,
): List<Move> {
    val responseDto = MizuumiMoveListResponseDto(cargoquery = map { dto -> Title(dto) })
    val moveList = responseDto.toDomain(game, character, hitboxUrlMap = emptyMap())
    return moveList
}

private fun MoveDto.toMove(
    game: Game,
    character: Character,
): Move {
    val move = listOf(this).toMoveList(game, character).single()
    return move
}

private fun movesOf(
    chara: String,
    idPrefix: String,
    count: Int,
): List<MoveDto> {
    val moveList = mbtlInputList
        .take(count)
        .map { input ->
            MoveDto(
                moveId = "${idPrefix}_${input.lowercase().replace(".", "")}",
                chara = chara,
                input = input,
            )
        }
    return moveList
}

private object MizuumiMoveSource {
    val ak = Character(
        id = CharacterId(Game.MBTL, "Akiha Tohno"),
        displayName = "Akiha Tohno",
        remoteQueryId = "Akiha Tohno",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno",
    )
    val dn = Character(
        id = CharacterId(Game.MBTL, "Dead Apostle Noel"),
        displayName = "Dead Apostle Noel",
        remoteQueryId = "Dead Apostle Noel",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Dead_Apostle_Noel",
    )
    val va = Character(
        id = CharacterId(Game.Uni2, "Vatista"),
        displayName = "Vatista",
        remoteQueryId = "Vatista",
        wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2/Vatista",
    )
    val akatsuki = Character(
        id = CharacterId(Game.Uni2, "Akatsuki"),
        displayName = "Akatsuki",
        remoteQueryId = "Akatsuki",
        wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2/Akatsuki",
    )
    val leiLei = Character(
        id = CharacterId(Game.VSAV, "Lei-Lei"),
        displayName = "Lei-Lei",
        remoteQueryId = "Lei-Lei",
        wikiUrl = "https://mizuumi.wiki/w/Vampire_Savior/Lei-Lei",
    )

    val ak5bClose = MoveDto(moveId = "ak_5b_close", chara = "Akatsuki", input = "5B")
    val ak5bFar = MoveDto(moveId = "ak_5b_far", chara = "Akatsuki", input = "5B")
    val le214hp = MoveDto(moveId = "LE_214HP", chara = "Lei-Lei", input = "214HP")
    val le236hp = MoveDto(moveId = "LE_236HP", chara = "Lei-Lei", input = "236HP")
    val tenraihaAnvil = MoveDto(moveId = "tenraiha_anvil", chara = "Lei-Lei", input = "LK,HK,MP,MP,8")

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


private val mbtlInputList = listOf("5A", "5B", "5C", "2A", "2B", "2C", "j.A", "j.B", "j.C", "214A")
