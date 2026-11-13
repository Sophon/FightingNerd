package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class MizuumiCharacterRemoteMapperTest {
    val iconUrlMap = mapOf(
        "morrigan" to "https://mizuumi.wiki/images/7/7f/Vsav-nav-portrait-morrigan.gif",
        "felicia" to "https://mizuumi.wiki/images/b/ba/Vsav-nav-portrait-felicia.gif",
    )

    @Test
    fun `chara maps to a character`() {
        //given
        val chara = "Morrigan"
        val expected = Character(
            id = CharacterId(Game.VSAV, "Morrigan"),
            displayName = "Morrigan",
            remoteQueryId = "Morrigan",
            wikiUrl = "https://mizuumi.wiki/w/Vampire_Savior/Morrigan",
            images = Character.Images(
                iconId = "morrigan",
                iconUrl = "https://mizuumi.wiki/images/7/7f/Vsav-nav-portrait-morrigan.gif",
            ),
        )

        //when
        val result = chara.toDomain(Game.VSAV, iconUrlMap)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `missing icon leaves the icon url null`() {
        //given
        val chara = "Lei-Lei"

        //when
        val result = chara.toDomain(Game.VSAV, iconUrlMap)

        //then
        assertThat(result.images?.iconUrl).isNull()
    }

    @Test
    fun `json character uses its display name as the remote query id`() {
        //given
        val characterList = MizuumiCharacterJsonResponseDto(
            characters = listOf(
                MizuumiCharacterJsonDto(
                    id = "akiha-tohno",
                    displayName = "Akiha Tohno",
                    aliasList = listOf("akiha", "ak"),
                    images = MizuumiCharacterJsonDto.Images(
                        largePng = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
                        officialLargePng = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
                    ),
                ),
            ),
        )
        val expected = listOf(
            Character(
                id = CharacterId(Game.MBTL, "Akiha Tohno"),
                displayName = "Akiha Tohno",
                remoteQueryId = "Akiha Tohno",
                wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno",
                aliasList = listOf("akiha", "ak"),
                images = Character.Images(
                    iconId = "MBTL_akiha_icon.png",
                    iconUrl = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
                ),
            ),
        )

        //when
        val result = characterList.toDomain(Game.MBTL)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `json character without images has no icon`() {
        //given
        val characterList = MizuumiCharacterJsonResponseDto(
            characters = listOf(
                MizuumiCharacterJsonDto(
                    id = "shiki-tohno",
                    displayName = "Shiki Tohno",
                    aliasList = listOf("shiki", "sh", "tony"),
                ),
            ),
        )

        //when
        val result = characterList.toDomain(Game.MBTL)

        //then
        assertThat(result.single().images).isEqualTo(Character.Images())
    }
}
