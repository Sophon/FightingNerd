package io.github.sophon.wiki.adapter.outbound.ktor.superCombo

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import kotlin.test.Test

class SuperComboMoveRemoteMapperTest {

    //region wiki url
    @Test
    fun `SF6 wiki url anchors on the input when the move has no name`() {
        //given
        val dto = SuperComboMoveSource.akiCrMP
        val expected = "https://wiki.supercombo.gg/w/Street_Fighter_6/A.K.I.#2MP"

        //when
        val result = dto.toMove(Game.StreetFighter6, SuperComboMoveSource.aki)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `SF6 wiki url anchors on the name and the input`() {
        //given
        val dto = SuperComboMoveSource.senpuKick
        val expected = "https://wiki.supercombo.gg/w/Street_Fighter_6/Chun-Li#Senpu_Kick_(214P~MK)"

        //when
        val result = dto.toMove(Game.StreetFighter6, SuperComboMoveSource.chunLi)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `MK1 wiki url points to the data page and anchors on the input`() {
        //given
        val dto = SuperComboMoveSource.swollenThroat
        val expected = "https://wiki.supercombo.gg/w/Mortal_Kombat_1/Kung_Lao/Data#121"

        //when
        val result = dto.toMove(Game.MK1, SuperComboMoveSource.kungLao)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `input by direction has no wiki url`() {
        //given
        val dto = SuperComboMoveSource.rollingCannon
        val expected = ""

        //when
        val result = dto.toMove(Game.StreetFighter6, SuperComboMoveSource.blanka)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }
    //endregion

    //region input
    @Test
    fun `input keeps the wiki notation`() {
        //given
        val dto = SuperComboMoveSource.hadoken
        val expected = "236HP"

        //when
        val result = dto.toMove(Game.StreetFighter6, SuperComboMoveSource.ken)

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `no aliases are created`() {
        //given
        val dto = SuperComboMoveSource.hadoken
        val expected = emptyList<String>()

        //when
        val result = dto.toMove(Game.StreetFighter6, SuperComboMoveSource.ken)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion
}

private fun MoveDto.toMove(
    game: Game,
    character: Character,
): Move {
    val responseDto = SuperComboMoveListResponseDto(cargoQuery = listOf(SuperComboMoveListResponseDto.Title(this)))
    val move = responseDto.toDomain(game, character, imageUrlMap = emptyMap()).single()
    return move
}

private object SuperComboMoveSource {
    val ken = superComboCharacter(id = "ken", name = "Ken", wikiPage = "Street_Fighter_6/Ken")
    val aki = superComboCharacter(id = "aki", name = "A.K.I.", wikiPage = "Street_Fighter_6/A.K.I.")
    val chunLi = superComboCharacter(id = "chun_li", name = "Chun-Li", wikiPage = "Street_Fighter_6/Chun-Li")
    val blanka = superComboCharacter(id = "blanka", name = "Blanka", wikiPage = "Street_Fighter_6/Blanka")
    val kungLao = superComboCharacter(id = "kung_lao", name = "Kung Lao", wikiPage = "Mortal_Kombat_1/Kung_Lao")

    val akiCrMP = MoveDto(
        moveId = "aki_2mp",
        moveType = "ground_normal",
        chara = "A.K.I.",
        input = "2MP",
    )
    val senpuKick = MoveDto(
        moveId = "chun-li_214p~mk",
        moveType = "special",
        chara = "Chun-Li",
        input = "214P~MK",
        name = "Senpu Kick",
    )
    val swollenThroat = MoveDto(
        moveId = "kung_lao_121",
        moveType = "normal",
        chara = "Kung Lao",
        input = "121",
        name = "Swollen Throat",
    )
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
        hitstun = "33",
        blockstun = "27",
        hitstop = "8",
        driveDmgBlk = "2500",
        driveDmgHit = "[2000]",
        driveGain = "1000",
        superGainHit = "600 (420)",
        superGainBlk = "300 (150)",
        jugStart = "1",
        jugIncrease = "1",
        jugLimit = "1",
        projSpeed = "0.08",
        notes = "1-hit projectile; puts airborne opponents into limited juggle state",
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
        blockstun = "17 (3P: 18)",
        hitstop = "15",
        driveDmgBlk = "1000 each",
        driveDmgHit = "2000 each",
        driveGain = "1000 oH (500 oB)",
        jugStart = "1",
        jugIncrease = "2 each",
        jugLimit = "99",
    )
}

private fun superComboCharacter(
    id: String,
    name: String,
    wikiPage: String,
): Character {
    val character = Character(
        id = id,
        displayName = name,
        remoteQueryId = name,
        wikiUrl = "https://wiki.supercombo.gg/w/$wikiPage",
    )
    return character
}
