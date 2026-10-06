package io.github.sophon.botdiscord.adapter.outbound.glossary

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.adapter.outbound.glossary.toDataSource
import io.github.sophon.discord.adapter.outbound.glossary.toDomain
import io.github.sophon.discord.adapter.outbound.glossary.toDomainError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.GlossaryResponse
import io.github.sophon.glossaryinfil.model.GlossaryError
import io.github.sophon.glossaryinfil.model.GlossaryItem
import kotlin.test.Test

class GlossaryMappersTest {
    //region toDataSource
    @Test
    fun `feature info becomes a brown data source`() {
        // given
        val expected = BotResponse.DataSource(name = FEATURE_NAME, iconUrl = ICON_URL, color = 0xDAA06D)

        // when
        val result = featureInfo.toDataSource()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `missing icon is blank`() {
        // given
        val expected = ""

        // when
        val result = featureInfo.copy(iconUrl = null).toDataSource()

        // then
        assertThat(result.iconUrl).isEqualTo(expected)
    }
    //endregion

    //region toDomain
    @Test
    fun `glossary item becomes a glossary response`() {
        // given
        val glossaryItem = GlossaryItem(
            term = "Okizeme",
            definition = "Pressure on a knocked-down opponent as they get up.",
            altTerm = listOf("Oki"),
            games = listOf("Tekken 8"),
            jpTranslation = listOf("起き攻め"),
            url = GlossaryItem.Url(
                term = "https://glossary.infil.net/?t=Okizeme",
                video = "https://glossary.infil.net/vid/okizeme.mp4",
                image = null,
            ),
        )
        val expected = GlossaryResponse(
            dataSource = dataSource,
            term = "Okizeme",
            definition = "Pressure on a knocked-down opponent as they get up.",
            jpTranslationList = listOf("起き攻め"),
            termUrl = "https://glossary.infil.net/?t=Okizeme",
            videoUrl = "https://glossary.infil.net/vid/okizeme.mp4",
            imageUrl = null,
        )

        // when
        val result = glossaryItem.toDomain(dataSource)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toDomainError
    @Test
    fun `empty glossary stays empty glossary`() {
        // given
        val error = GlossaryError.EmptyGlossary

        // when
        val result = error.toDomainError()

        // then
        assertThat(result).isInstanceOf(BotError.EmptyGlossary::class)
    }

    @Test
    fun `download error carries the cause`() {
        // given
        val error = GlossaryError.Download(DataError.Remote.NO_INTERNET)
        val expected = "DownloadError(NO_INTERNET)"

        // when
        val result = error.toDomainError()

        // then
        assertThat(result.toString()).isEqualTo(expected)
    }

    @Test
    fun `database error becomes a database error`() {
        // given
        val error = GlossaryError.Database(DataError.Local.DISK_FULL)

        // when
        val result = error.toDomainError()

        // then
        assertThat(result).isInstanceOf(BotError.DatabaseError::class)
    }
    //endregion
}


private const val FEATURE_NAME = "Infil Glossary"
private const val ICON_URL = "https://glossary.infil.net/favicon.ico"
private val featureInfo = FeatureInfo(
    name = FEATURE_NAME,
    url = "https://glossary.infil.net",
    version = "1.1.0",
    iconUrl = ICON_URL,
)
private val dataSource = BotResponse.DataSource(name = FEATURE_NAME, iconUrl = ICON_URL, color = 0xDAA06D)
