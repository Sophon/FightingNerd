package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class MizuumiCharacterRemoteMapperTest {
    val iconUrlMap = mapOf(
        "ciel" to "https://mizuumi.wiki/images/1/18/MBTL_ciel_icon.png",
        "akiha" to "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
    )

    @Test
    fun `single-word chara maps to a character`() {
        //given
        val chara = "Ciel"
        val expected = Character(
            id = CharacterId(Game.MBTL, "Ciel"),
            displayName = "Ciel",
            remoteQueryId = "Ciel",
            wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Ciel",
            aliasList = listOf("cl", "ci"),
            images = Character.Images(
                iconId = "ciel",
                iconUrl = "https://mizuumi.wiki/images/1/18/MBTL_ciel_icon.png",
            ),
        )

        //when
        val result = chara.toDomain(Game.MBTL, iconUrlMap)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `multi-word chara joins its wiki url by underscore and uses the first name's icon`() {
        //given
        val chara = "Akiha Tohno"
        val expected = Character(
            id = CharacterId(Game.MBTL, "Akiha Tohno"),
            displayName = "Akiha Tohno",
            remoteQueryId = "Akiha Tohno",
            wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno",
            aliasList = listOf("akiha", "ak"),
            images = Character.Images(
                iconId = "akiha",
                iconUrl = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
            ),
        )

        //when
        val result = chara.toDomain(Game.MBTL, iconUrlMap)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `missing icon falls back to the game icon`() {
        //given
        val chara = "Shiki Tohno"
        val expected = Game.MBTL.iconUrl

        //when
        val result = chara.toDomain(Game.MBTL, iconUrlMap)

        //then
        assertThat(result.images?.iconUrl).isEqualTo(expected)
    }
}
