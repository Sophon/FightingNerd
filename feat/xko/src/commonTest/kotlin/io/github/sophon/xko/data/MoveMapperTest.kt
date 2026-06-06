package io.github.sophon.xko.data

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.github.sophon.core.wiki.model.Move
import kotlin.test.Test

class MoveMapperTest {
    @Test
    fun `toDomain should group moves by character and map correctly`() {
        // given
        val jinxMove = MoveDto(
            pageName = "Jinx",
            cancel = "N,SP,SU",
            startup = "11",
            active = "5",
            onBlock = "-6",
            recovery = "18",
            input = "5M",
            guard = "LHA",
            damage = "55"
        )
        val dariusMove = MoveDto(
            pageName = "Darius",
            cancel = "N,SP,SU",
            startup = "12",
            active = "5",
            invuln = "",
            onBlock = "-3",
            recovery = "15",
            input = "5M",
            guard = "LHA",
            damage = "65"
        )
        val responseDto = MoveListResponseDto(
            bucketQuery = "test",
            bucket = listOf(jinxMove, dariusMove)
        )
        val expectedJinxMove = Move(
            characterId = "Jinx",
            id = "jinx_5m",
            input = "5m",
            damage = "55",
            startup = "11",
            onBlock = "-6",
            recovery = "18",
            active = "5",
            guard = "LHA",
            cancel = "N,SP,SU",
            invulnerability = null,
            urls = Move.Urls(
                hitboxImageList = listOf("https://wiki.play2xko.com/en-us/images/Jinx_5M_Hitbox.png"),
                moveImageList = listOf("https://wiki.play2xko.com/en-us/images/Jinx_5M.png"),
                wikiUrl = "https://wiki.play2xko.com/en-us/Jinx#5M"
            )
        )
        val expectedDariusMove = Move(
            characterId = "Darius",
            id = "darius_5m",
            input = "5m",
            damage = "65",
            startup = "12",
            onBlock = "-3",
            recovery = "15",
            active = "5",
            guard = "LHA",
            cancel = "N,SP,SU",
            invulnerability = null,
            urls = Move.Urls(
                hitboxImageList = listOf("https://wiki.play2xko.com/en-us/images/Darius_5M_Hitbox.png"),
                moveImageList = listOf("https://wiki.play2xko.com/en-us/images/Darius_5M.png"),
                wikiUrl = "https://wiki.play2xko.com/en-us/Darius#5M"
            )
        )

        // when
        val result = responseDto.toDomain()

        // then
        assertThat(result.size).isEqualTo(2)

        val jinxCharacter = result.keys.find { it.remoteQueryId == "Jinx" }
        assertThat(jinxCharacter).isNotNull()
        assertThat(result[jinxCharacter]!!).hasSize(1)
        assertThat(result[jinxCharacter]!![0]).isEqualTo(expectedJinxMove)

        val dariusCharacter = result.keys.find { it.remoteQueryId == "Darius" }
        assertThat(dariusCharacter).isNotNull()
        assertThat(result[dariusCharacter]!!).hasSize(1)
        assertThat(result[dariusCharacter]!![0]).isEqualTo(expectedDariusMove)
    }

    @Test
    fun `toDomain should filter empty strings to null`() {
        // given
        val moveDto = MoveDto(
            pageName = "Jinx",
            cancel = "",
            startup = "11",
            active = "",
            onBlock = "-6",
            recovery = "18",
            input = "5M",
            guard = "",
            damage = "",
            invuln = ""
        )
        val responseDto = MoveListResponseDto(
            bucketQuery = "test",
            bucket = listOf(moveDto)
        )
        val expectedMove = Move(
            characterId = "Jinx",
            id = "jinx_5m",
            input = "5m",
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
                wikiUrl = "https://wiki.play2xko.com/en-us/Jinx#5M"
            )
        )

        // when
        val result = responseDto.toDomain()

        // then
        assertThat(result.size).isEqualTo(1)

        val jinxCharacter = result.keys.find { it.remoteQueryId == "Jinx" }
        assertThat(jinxCharacter).isNotNull()
        assertThat(result[jinxCharacter]!!).hasSize(1)
        assertThat(result[jinxCharacter]!![0]).isEqualTo(expectedMove)
    }

    @Test
    fun `addExtraAliases removes parentheses`() {
        // given
        val string = listOf("j.(M+H)")
        val expected = listOf("j.(M+H)", "j.M+H")

        // when
        val result = string.addExtraAliases("j.(M+H)")

        //then
        assertThat(result).isEqualTo(expected)
    }
}