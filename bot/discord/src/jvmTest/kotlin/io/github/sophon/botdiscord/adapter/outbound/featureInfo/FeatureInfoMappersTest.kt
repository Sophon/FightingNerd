package io.github.sophon.botdiscord.adapter.outbound.featureInfo

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.adapter.outbound.featureInfo.toModule
import io.github.sophon.discord.app.model.response.ModulesResponse
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
        )

        // when
        val result = featureInfo.toModule()

        // then
        assertThat(result).isEqualTo(expected)
    }
}
