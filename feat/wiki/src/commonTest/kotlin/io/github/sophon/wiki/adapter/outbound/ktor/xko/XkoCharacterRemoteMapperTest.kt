package io.github.sophon.wiki.adapter.outbound.ktor.xko

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class XkoCharacterRemoteMapperTest {

    @Test
    fun `character maps to domain with the official icon`() {
        //given
        val responseDto = XkoCharacterJsonResponseDto(
            characters = listOf(XkoCharacterSource.jinx),
        )
        val expected = listOf(
            Character(
                id = CharacterId(Game.Xko, "Jinx"),
                displayName = "Jinx",
                remoteQueryId = "Jinx",
                wikiUrl = "https://wiki.play2xko.com/en-us/Jinx",
                aliasList = listOf(),
                images = Character.Images(
                    iconId = "Jinx_cs.png?218c3",
                    iconUrl = "https://wiki.play2xko.com/en-us/images/Jinx_cs.png?218c3",
                ),
            ),
        )

        //when
        val result = responseDto.toDomain(Game.Xko)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `missing images leave the icon empty`() {
        //given
        val responseDto = XkoCharacterJsonResponseDto(
            characters = listOf(XkoCharacterSource.settWithoutImages),
        )
        val expected = Character.Images(
            iconId = null,
            iconUrl = null,
        )

        //when
        val result = responseDto.toDomain(Game.Xko).single().images

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `every character is mapped in order`() {
        //given
        val responseDto = XkoCharacterJsonResponseDto(
            characters = listOf(
                XkoCharacterSource.jinx,
                XkoCharacterSource.settWithoutImages,
            ),
        )
        val expected = listOf("Jinx", "Sett")

        //when
        val result = responseDto.toDomain(Game.Xko).map { it.displayName }

        //then
        assertThat(result).isEqualTo(expected)
    }
}

private object XkoCharacterSource {
    val jinx = XkoCharacterJsonDto(
        id = "jinx",
        displayName = "Jinx",
        aliasList = listOf(),
        images = XkoCharacterJsonDto.Images(
            largePng = "https://wiki.play2xko.com/en-us/images/thumb/Champ_JinxBase_Icon.png/120px-Champ_JinxBase_Icon.png?5dd1d",
            officialLargePng = "https://wiki.play2xko.com/en-us/images/Jinx_cs.png?218c3",
        ),
    )

    val settWithoutImages = XkoCharacterJsonDto(
        id = "sett",
        displayName = "Sett",
        aliasList = listOf(),
        images = null,
    )
}
