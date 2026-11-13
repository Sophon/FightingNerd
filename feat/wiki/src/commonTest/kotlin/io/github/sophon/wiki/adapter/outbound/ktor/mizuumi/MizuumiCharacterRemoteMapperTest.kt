package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class MizuumiCharacterRemoteMapperTest {
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
                        portrait = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
                        full = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
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
                    portraitId = "MBTL_akiha_icon.png",
                    portrait = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
                    full = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
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
