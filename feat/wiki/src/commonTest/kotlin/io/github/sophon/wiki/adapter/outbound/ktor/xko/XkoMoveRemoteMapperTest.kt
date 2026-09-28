package io.github.sophon.wiki.adapter.outbound.ktor.xko

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import kotlin.test.Test

class XkoMoveRemoteMapperTest {

    @Test
    fun `bulk is grouped into characters by page`() {
        //given
        val responseDto = XkoMoveListResponseDto(
            bucketQuery = "bucket('move')",
            bucket = listOf(XkoMoveSource.jinx5M, XkoMoveSource.darius5M),
        )
        val expected = listOf(
            XkoMoveSource.jinx to listOf(
                Move(
                    input = "5M",
                    damage = "55",
                    startup = "11",
                    onBlock = "-6",
                    recovery = "18",
                    active = "5",
                    guard = "LHA",
                    cancel = "N,SP,SU",
                    urls = Move.Urls(
                        hitboxImageList = listOf("https://wiki.play2xko.com/en-us/images/Jinx_5M_Hitbox.png"),
                        moveImageList = listOf("https://wiki.play2xko.com/en-us/images/Jinx_5M.png"),
                        wikiUrl = "https://wiki.play2xko.com/en-us/Jinx#5M",
                    ),
                ),
            ),
            XkoMoveSource.darius to listOf(
                Move(
                    input = "5M",
                    damage = "65",
                    startup = "12",
                    onBlock = "-3",
                    recovery = "15",
                    active = "5",
                    guard = "LHA",
                    cancel = "N,SP,SU",
                    urls = Move.Urls(
                        hitboxImageList = listOf("https://wiki.play2xko.com/en-us/images/Darius_5M_Hitbox.png"),
                        moveImageList = listOf("https://wiki.play2xko.com/en-us/images/Darius_5M.png"),
                        wikiUrl = "https://wiki.play2xko.com/en-us/Darius#5M",
                    ),
                ),
            ),
        )

        //when
        val result = responseDto.toDomainAll()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `empty fields become null`() {
        //given
        val responseDto = XkoMoveListResponseDto(
            bucketQuery = "bucket('move')",
            bucket = listOf(XkoMoveSource.jinx5MEmptyFields),
        )
        val expected = Move(
            input = "5M",
            damage = null,
            startup = "11",
            onBlock = "-6",
            recovery = "18",
            active = null,
            guard = null,
            cancel = null,
            invulnerability = null,
            urls = Move.Urls(
                hitboxImageList = listOf("https://wiki.play2xko.com/en-us/images/Jinx_5M_Hitbox.png"),
                moveImageList = listOf("https://wiki.play2xko.com/en-us/images/Jinx_5M.png"),
                wikiUrl = "https://wiki.play2xko.com/en-us/Jinx#5M",
            ),
        )

        //when
        val result = responseDto.toDomainAll().single().second.single()

        //then
        assertThat(result).isEqualTo(expected)
    }
}

private object XkoMoveSource {
    val jinx = Character(
        id = CharacterId("Jinx"),
        displayName = "Jinx",
        remoteQueryId = "Jinx",
        wikiUrl = "https://wiki.play2xko.com/en-us/Jinx",
    )
    val darius = Character(
        id = CharacterId("Darius"),
        displayName = "Darius",
        remoteQueryId = "Darius",
        wikiUrl = "https://wiki.play2xko.com/en-us/Darius",
    )

    val jinx5M = MoveDto(
        pageName = "Jinx",
        cancel = "N,SP,SU",
        startup = "11",
        active = "5",
        onBlock = "-6",
        recovery = "18",
        input = "5M",
        guard = "LHA",
        damage = "55",
    )
    val darius5M = MoveDto(
        pageName = "Darius",
        cancel = "N,SP,SU",
        startup = "12",
        active = "5",
        invuln = "",
        onBlock = "-3",
        recovery = "15",
        input = "5M",
        guard = "LHA",
        damage = "65",
    )
    val jinx5MEmptyFields = MoveDto(
        pageName = "Jinx",
        cancel = "",
        startup = "11",
        active = "",
        onBlock = "-6",
        recovery = "18",
        input = "5M",
        guard = "",
        damage = "",
        invuln = "",
    )
}
