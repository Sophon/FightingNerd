package io.github.sophon.wiki.adapter.outbound.ktor.superCombo

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class SuperComboMoveRemoteMapperTest {

    //region wiki url
    @Test
    fun `SF6 wiki url anchors on the input when the move has no name`() {
        //given
        val dto = SuperComboMoveSource.akiCrMP
        val expected = "https://wiki.supercombo.gg/w/Street_Fighter_6/A.K.I.#2MP"

        //when
        val result = dto.toMove(Game.StreetFighter6)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `SF6 wiki url anchors on the name and the input`() {
        //given
        val dto = SuperComboMoveSource.senpuKick
        val expected = "https://wiki.supercombo.gg/w/Street_Fighter_6/Chun-Li#Senpu_Kick_(214P~MK)"

        //when
        val result = dto.toMove(Game.StreetFighter6)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `MK1 wiki url points to the data page and anchors on the input`() {
        //given
        val dto = SuperComboMoveSource.swollenThroat
        val expected = "https://wiki.supercombo.gg/w/Mortal_Kombat_1/Kung_Lao/Data#121"

        //when
        val result = dto.toMove(Game.MK1)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `input by direction has no wiki url`() {
        //given
        val dto = SuperComboMoveSource.rollingCannon
        val expected = ""

        //when
        val result = dto.toMove(Game.StreetFighter6)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }
    //endregion

    //region input
    @Test
    fun `input is the move ID without the character prefix`() {
        //given
        val moveList = listOf(SuperComboMoveSource.maiRyuuenbu, SuperComboMoveSource.maiRyuuenbuFlame)
        val expected = listOf("214hp", "214hp_flame")

        //when
        val result = moveList.toMoveList(Game.StreetFighter6)

        //then
        assertThat(result.map { it.input }).isEqualTo(expected)
    }

    @Test
    fun `critical art keeps its marker in the input`() {
        //given
        val moveList = listOf(SuperComboMoveSource.alexFinalPrison, SuperComboMoveSource.alexFinalPrisonCa)
        val expected = listOf("236236p", "236236p(ca)")

        //when
        val result = moveList.toMoveList(Game.StreetFighter6)

        //then
        assertThat(result.map { it.input }).isEqualTo(expected)
    }

    @Test
    fun `move ID prefix is stripped regardless of case`() {
        //given
        val moveList = listOf(SuperComboMoveSource.jamieFreeflow, SuperComboMoveSource.jamieDriveRush)
        val expected = listOf("236hp_dl2", "mpmk_66_drc")

        //when
        val result = moveList.toMoveList(Game.StreetFighter6)

        //then
        assertThat(result.map { it.input }).isEqualTo(expected)
    }

    @Test
    fun `remote ID is the move ID`() {
        //given
        val moveList = listOf(SuperComboMoveSource.maiRyuuenbu, SuperComboMoveSource.maiRyuuenbuFlame)
        val expected = listOf("mai_214hp", "mai_214hp_flame")

        //when
        val result = moveList.toMoveList(Game.StreetFighter6)

        //then
        assertThat(result.map { it.remoteId }).isEqualTo(expected)
    }

    @Test
    fun `wiki input is kept as an alias`() {
        //given
        val moveList = listOf(SuperComboMoveSource.maiRyuuenbu, SuperComboMoveSource.maiRyuuenbuFlame)
        val expected = listOf(listOf("214HP"), listOf("214HP"))

        //when
        val result = moveList.toMoveList(Game.StreetFighter6)

        //then
        assertThat(result.map { it.aliases }).isEqualTo(expected)
    }
    //endregion
}

private fun List<MoveDto>.toMoveList(game: Game): List<Move> {
    val responseDto = SuperComboMoveListResponseDto(cargoQuery = map { dto -> SuperComboMoveListResponseDto.Title(dto) })
    val moveList = responseDto.toDomain(game, imageUrlMap = emptyMap())
    return moveList
}

private fun MoveDto.toMove(game: Game): Move {
    val move = listOf(this).toMoveList(game).single()
    return move
}

private object SuperComboMoveSource {
    val maiRyuuenbu = MoveDto(
        moveId = "mai_214hp",
        moveType = "special",
        chara = "Mai",
        input = "214HP",
        name = "HP Ryuuenbu",
    )
    val maiRyuuenbuFlame = MoveDto(
        moveId = "mai_214hp_flame",
        moveType = "special",
        chara = "Mai",
        input = "214HP",
        name = "HP Ryuuenbu (Flame)",
    )
    val alexFinalPrison = MoveDto(
        moveId = "alex_236236p",
        moveType = "super",
        chara = "Alex",
        input = "236236P",
        name = "The Final Prison",
    )
    val alexFinalPrisonCa = MoveDto(
        moveId = "alex_236236p(ca)",
        moveType = "super",
        chara = "Alex",
        input = "236236P",
        name = "The Final Prison (CA)",
    )
    val jamieFreeflow = MoveDto(
        moveId = "Jamie_236hp_dl2",
        moveType = "special",
        chara = "Jamie",
        input = "236HP",
        name = "HP Freeflow Strikes 1",
    )
    val jamieDriveRush = MoveDto(
        moveId = "jamie_mpmk_66_drc",
        moveType = "drive",
        chara = "Jamie",
        input = "MPMK~66",
    )

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
