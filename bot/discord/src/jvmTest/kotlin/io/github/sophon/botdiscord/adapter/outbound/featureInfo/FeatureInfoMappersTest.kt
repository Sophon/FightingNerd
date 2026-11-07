package io.github.sophon.botdiscord.adapter.outbound.featureInfo

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.adapter.outbound.featureInfo.toModule
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class FeatureInfoMappersTest {
    @Test
    fun `feature info becomes a module`() {
        // given
        val featureInfo = FeatureInfo(
            name = "Infil Glossary",
            url = "https://glossary.infil.net",
            version = "1.1.0",
            iconUrl = "https://glossary.infil.net/favicon.ico",
        )
        val expected = ModulesResponse.Module(
            name = "Infil Glossary",
            url = "https://glossary.infil.net",
            version = "1.1.0",
            gameSet = emptySet(),
        )

        // when
        val result = featureInfo.toModule(supportedGameSet = emptySet())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `supported games become module games`() {
        // given
        val featureInfo = FeatureInfo(
            name = "Wiki",
            url = "https://github.com/Sophon/FightingNerd/tree/main/feat/wiki",
            version = "2.3.0",
            iconUrl = "https://wavu.wiki/favicon.ico",
        )
        val expected = ModulesResponse.Module(
            name = "Wiki",
            url = "https://github.com/Sophon/FightingNerd/tree/main/feat/wiki",
            version = "2.3.0",
            gameSet = setOf(
                ModulesResponse.Module.Game(name = "Tekken 8", id = "Tekken_8"),
                ModulesResponse.Module.Game(name = "Guilty Gear -Strive-", id = "GGST"),
            ),
        )

        // when
        val result = featureInfo.toModule(supportedGameSet = setOf(Game.Tekken8, Game.GGST))

        // then
        assertThat(result).isEqualTo(expected)
    }
}
