package io.github.sophon.fightingnerd.adapter.inbound.more

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.util.toHumanReadableString
import io.github.sophon.fightingnerd.adapter.inbound.more.featureSettings.FeatureSettingsState
import io.github.sophon.fightingnerd.adapter.inbound.more.updates.UpdatesState
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Wiki
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.time.Instant

internal class UiMappersTest {
    private val wavuWiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png")
    private val mizuumiWiki = Wiki(name = "Mizuumi Wiki", url = "https://wiki.gbl.gg/", iconUrl = "https://wiki.gbl.gg/images/mizuumi.png")
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wiki = wavuWiki,
    )
    private val meltyBlood = Game(
        id = "MBTL",
        displayName = "Melty Blood: Type Lumina",
        iconUrl = "https://i.imgur.com/0bVb3Pq.png",
        wiki = mizuumiWiki,
    )
    private val underNightInBirth = Game(
        id = "UNI2",
        displayName = "Under Night In-Birth II Sys:Celes",
        iconUrl = "https://i.imgur.com/6wqJm9C.png",
        wiki = mizuumiWiki,
    )

    private val tekken8LastUpdate = Instant.parse("2026-09-01T08:15:00Z")
    private val meltyBloodLastUpdate = Instant.parse("2026-09-04T11:27:00Z")

    @Test
    fun `game settings are grouped under their wiki`() {
        // given
        val isEnabledByGame = mapOf(tekken8 to true, meltyBlood to false, underNightInBirth to true)
        val expected = persistentListOf(
            FeatureSettingsState.UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "https://wavu.wiki/android-chrome-512x512.png",
                gameList = persistentListOf(
                    FeatureSettingsState.UiFeatureSetting.UiGame(displayName = "Tekken 8", id = "Tekken_8", isEnabled = true),
                ),
            ),
            FeatureSettingsState.UiFeatureSetting(
                featureName = "Mizuumi Wiki",
                iconUrl = "https://wiki.gbl.gg/images/mizuumi.png",
                gameList = persistentListOf(
                    FeatureSettingsState.UiFeatureSetting.UiGame(displayName = "Melty Blood: Type Lumina", id = "MBTL", isEnabled = false),
                    FeatureSettingsState.UiFeatureSetting.UiGame(displayName = "Under Night In-Birth II Sys:Celes", id = "UNI2", isEnabled = true),
                ),
            ),
        )

        // when
        val result = isEnabledByGame.toUiFeatureSettingList()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `last updates are grouped under their wiki and refreshing games are flagged`() {
        // given
        val lastUpdateByGame = mapOf(tekken8 to tekken8LastUpdate, meltyBlood to meltyBloodLastUpdate)
        val expected = persistentListOf(
            UpdatesState.UiFeatureSetting(
                name = "Wavu Wiki",
                iconUrl = "https://wavu.wiki/android-chrome-512x512.png",
                gameList = persistentListOf(
                    UpdatesState.UiFeatureSetting.UiGame(
                        name = "Tekken 8",
                        id = "Tekken_8",
                        lastUpdatedTimeStamp = tekken8LastUpdate.toHumanReadableString(),
                        isRefreshing = true,
                    ),
                ),
            ),
            UpdatesState.UiFeatureSetting(
                name = "Mizuumi Wiki",
                iconUrl = "https://wiki.gbl.gg/images/mizuumi.png",
                gameList = persistentListOf(
                    UpdatesState.UiFeatureSetting.UiGame(
                        name = "Melty Blood: Type Lumina",
                        id = "MBTL",
                        lastUpdatedTimeStamp = meltyBloodLastUpdate.toHumanReadableString(),
                        isRefreshing = false,
                    ),
                ),
            ),
        )

        // when
        val result = lastUpdateByGame.toUiUpdatesFeatureList(refreshingGameIdSet = setOf("Tekken_8"))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `games that were never downloaded are left out of the updates`() {
        // given
        val lastUpdateByGame = mapOf(meltyBlood to meltyBloodLastUpdate, underNightInBirth to null)
        val expected = listOf("MBTL")

        // when
        val result = lastUpdateByGame
            .toUiUpdatesFeatureList(refreshingGameIdSet = emptySet())
            .flatMap { feature -> feature.gameList.map { game -> game.id } }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wikis without downloaded games are left out of the updates`() {
        // given
        val lastUpdateByGame = mapOf(tekken8 to tekken8LastUpdate, meltyBlood to null, underNightInBirth to null)
        val expected = listOf("Wavu Wiki")

        // when
        val result = lastUpdateByGame
            .toUiUpdatesFeatureList(refreshingGameIdSet = emptySet())
            .map { feature -> feature.name }

        // then
        assertThat(result).isEqualTo(expected)
    }
}
