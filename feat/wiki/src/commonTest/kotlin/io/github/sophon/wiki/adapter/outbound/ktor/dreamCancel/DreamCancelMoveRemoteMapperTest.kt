package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import kotlin.test.Test

class DreamCancelMoveRemoteMapperTest {

    @Test
    fun `bulk table is grouped into characters by chara`() {
        //given
        val responseDto = listOf(DreamCancelMoveSource.aurora, DreamCancelMoveSource.byeByeBoo).toResponseDto()
        val expected = listOf(CharacterId(Game.KoFXV, "B.Jenet") to 2)

        //when
        val result = responseDto.toDomainAll(Game.KoFXV, iconUrlMap = emptyMap(), hitboxUrlMap = emptyMap())

        //then
        assertThat(result.map { (character, moveList) -> character.id to moveList.size }).isEqualTo(expected)
    }

    @Test
    fun `wiki url is the game url and the chara page`() {
        //given
        val dto = DreamCancelMoveSource.aurora
        val expected = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/B.Jenet"

        //when
        val result = dto.toMove()

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }

    //region input
    @Test
    fun `input is the move ID without the character prefix`() {
        //given
        val dtoList = listOf(DreamCancelMoveSource.aurora, DreamCancelMoveSource.byeByeBoo)
        val expected = listOf("236236k", "cthrow")

        //when
        val result = dtoList.toMoveList()

        //then
        assertThat(result.map { it.input }).isEqualTo(expected)
    }

    @Test
    fun `remote ID is the move ID`() {
        //given
        val dtoList = listOf(DreamCancelMoveSource.aurora, DreamCancelMoveSource.byeByeBoo)
        val expected = listOf("bjenet_236236k", "bjenet_cthrow")

        //when
        val result = dtoList.toMoveList()

        //then
        assertThat(result.map { it.remoteId }).isEqualTo(expected)
    }

    @Test
    fun `wiki input is kept as an alias`() {
        //given
        val dtoList = listOf(DreamCancelMoveSource.aurora, DreamCancelMoveSource.byeByeBoo)
        val expected = listOf(listOf("236236B/D"), listOf("(close) 4/6C"))

        //when
        val result = dtoList.toMoveList()

        //then
        assertThat(result.map { it.aliases }).isEqualTo(expected)
    }
    //endregion
}

private fun List<MoveDto>.toResponseDto(): DreamCancelMoveListResponseDto {
    val responseDto = DreamCancelMoveListResponseDto(cargoQuery = map { Title(it) })
    return responseDto
}

/**
 * One character's moves - the bulk mapper groups them into that character.
 */
private fun List<MoveDto>.toMoveList(): List<Move> {
    val (_, moveList) = toResponseDto()
        .toDomainAll(Game.KoFXV, iconUrlMap = emptyMap(), hitboxUrlMap = emptyMap())
        .single()
    return moveList
}

private fun MoveDto.toMove(): Move {
    val move = listOf(this).toMoveList().single()
    return move
}

private object DreamCancelMoveSource {
    val aurora = MoveDto(
        chara = "B.Jenet",
        moveId = "bjenet_236236k",
        name = "Aurora",
        input = "236236B/D",
        damage = "206 ([20+10*8+40]+70)",
        guard = "Mid",
        startup = "5",
        active = "7 (1) 1 (1) 1 (6) 1 (1) 1 (1) 1 (9) 1 (1) 1 (1) 1",
        recovery = "59 (27 on ground)",
        hitAdv = "HKD (43)",
        blockAdv = "-66",
        invul = "Full Body: 1 to 11 (11 Frames)",
        cancel = "advanced, climax",
        images = "XV_bjenet_236236b_ima.png",
        hitboxes = "XV_bjenet_236236k.png, XV_bjenet_236236k2.png, XV_bjenet_236236k3.png, XV_bjenet_236236k4.png",
        guardDamage = "0",
    )
    val byeByeBoo = MoveDto(
        chara = "B.Jenet",
        moveId = "bjenet_cthrow",
        name = "Bye-Bye Boo",
        idle = "",
        rank = "",
        input = "(close) 4/6C",
        images = "XV_bjenet_cthrow_ima.png",
        hitboxes = "XV_bjenet_cthrow.png",
        damage = "100 (50+50)",
        guard = "N/A",
        cancel = "",
        startup = "1",
        active = "1",
        recovery = "0",
        hitAdv = "HKD (52)",
        blockAdv = "Unblockable",
        invul = "",
        guardDamage = "0",
    )
}
