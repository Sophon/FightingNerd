package io.github.sophon.wikimizuumi.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import kotlin.test.Test

internal class CharacterMapperTest {
    val imageUrlMap = mapOf(
        "ciel" to "https://mizuumi.wiki/images/1/18/MBTL_ciel_icon.png",
        "akiha" to "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png",
    )

    @Test
    fun `mapper handles basic name`() {
        // given
        val name = "Ciel"
        val expected = Character(
            id = "ciel",
            displayName = "Ciel",
            remoteQueryId = "Ciel",
            wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Ciel",
            aliasList = listOf("cl", "ci",),
            images = Character.Images(
                iconUrl = "https://mizuumi.wiki/images/1/18/MBTL_ciel_icon.png",
            ),
        )

        // when
        val result = name.toDomain(Game.MBTL.id, imageUrlMap)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `mapper handles multi-word name`() {
        // given
        val name = "Akiha Tohno"
        val expected = Character(
            id = "akiha_tohno",
            displayName = "Akiha Tohno",
            remoteQueryId = "Akiha Tohno",
            wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno",
            aliasList = listOf("akiha", "ak",),
            images = Character.Images(
                iconUrl = "https://mizuumi.wiki/images/b/ba/MBTL_akiha_icon.png"
            )
        )

        // when
        val result = name.toDomain(Game.MBTL.id, imageUrlMap)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `mapper return game's icon if not found in image map`() {
        // given
        val name = "Shiki Tohno"

        // when
        val result = name.toDomain(Game.MBTL.id, imageUrlMap)

        //then
        assertThat(result.images?.iconUrl).isEqualTo(Game.MBTL.iconUrl)
    }
}