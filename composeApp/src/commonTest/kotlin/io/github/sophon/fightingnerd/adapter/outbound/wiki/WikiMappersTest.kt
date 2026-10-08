package io.github.sophon.fightingnerd.adapter.outbound.wiki

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.model.game.MKCharProperties
import io.github.sophon.fightingnerd.app.model.game.T8Properties
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.WikiEvent
import kotlin.test.Test
import io.github.sophon.wiki.model.Character as WikiCharacter
import io.github.sophon.wiki.model.Move as WikiMove
import io.github.sophon.wiki.model.game.MKCharProperties as WikiMKCharProperties
import io.github.sophon.wiki.model.game.T8Properties as WikiT8Properties
import io.github.sophon.wiki.model.wiki.Game as WikiGame

internal class WikiMappersTest {
    @Test
    fun `failure event keeps the wiki error and the character`() {
        // given
        val event = WikiEvent.Refresh.Failure(
            game = WikiGame.Tekken8,
            error = WikiError.DownloadError("Jin"),
            characterId = CharacterId(game = WikiGame.Tekken8, naturalId = "jin"),
        )
        val expected = RefreshEvent.Failure(
            game = WikiGame.Tekken8.toDomain(),
            error = AppError.WikiError("DownloadError(Jin)"),
            characterId = "jin",
        )

        // when
        val result = event.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `finished event keeps the success count`() {
        // given
        val event = WikiEvent.Refresh.Finished(game = WikiGame.Tekken8, successCount = 37)
        val expected = RefreshEvent.Finished(game = WikiGame.Tekken8.toDomain(), successCount = 37)

        // when
        val result = event.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `progress event keeps the fraction`() {
        // given
        val event = WikiEvent.Refresh.Progress(game = WikiGame.Tekken8, fraction = 0.5f)
        val expected = RefreshEvent.Progress(game = WikiGame.Tekken8.toDomain(), fraction = 0.5f)

        // when
        val result = event.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wiki game keeps its wiki name`() {
        // given
        val wikiGame = WikiGame.Tekken8
        val expected = Game(
            id = "Tekken_8",
            displayName = "Tekken 8",
            iconUrl = "https://i.imgur.com/Yl6j809.png",
            wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
        )

        // when
        val result = wikiGame.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wiki character keeps its natural id and icon`() {
        // given
        val wikiCharacter = WikiCharacter(
            id = CharacterId(game = WikiGame.Tekken8, naturalId = "armor_king"),
            displayName = "Armor King",
            remoteQueryId = "Armor King",
            wikiUrl = "https://wavu.wiki/t/Armor_King",
            images = WikiCharacter.Images(iconUrl = "https://wavu.wiki/w/images/Armor_King_icon.png"),
        )
        val expected = Character(
            id = "armor_king",
            displayName = "Armor King",
            iconUrl = "https://wavu.wiki/w/images/Armor_King_icon.png",
        )

        // when
        val result = wikiCharacter.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `games of disabled features aren't available`() {
        // given
        val composeConfig = ComposeConfig(
            featureList = listOf(
                ComposeConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
                ComposeConfig.Feature(name = "Mizuumi Wiki", isEnabled = false, supportedGames = listOf("MBTL", "UNI2")),
            ),
        )
        val expected = WikiConfig.create(
            availableGameSet = setOf(WikiGame.Tekken8),
            enabledGameSet = emptySet(),
        )

        // when
        val result = composeConfig.toWikiConfig(enabledGameIdSet = emptySet())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unknown game ids are dropped`() {
        // given
        val composeConfig = ComposeConfig(
            featureList = listOf(
                ComposeConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8", "Tekken_9")),
            ),
        )
        val expected = WikiConfig.create(
            availableGameSet = setOf(WikiGame.Tekken8),
            enabledGameSet = emptySet(),
        )

        // when
        val result = composeConfig.toWikiConfig(enabledGameIdSet = emptySet())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `enabled games come from the preferences`() {
        // given
        val composeConfig = ComposeConfig(
            featureList = listOf(
                ComposeConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
                ComposeConfig.Feature(name = "DustLoop Wiki", isEnabled = true, supportedGames = listOf("GGST")),
            ),
        )
        val expected = WikiConfig.create(
            availableGameSet = setOf(WikiGame.Tekken8, WikiGame.GGST),
            enabledGameSet = setOf(WikiGame.Tekken8),
        )

        // when
        val result = composeConfig.toWikiConfig(enabledGameIdSet = setOf("Tekken_8"))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wiki character keeps its stats`() {
        // given
        val wikiCharacter = WikiCharacter(
            id = CharacterId(game = WikiGame.MK1, naturalId = "scorpion"),
            displayName = "Scorpion",
            remoteQueryId = "Scorpion",
            wikiUrl = "https://srk.shib.live/w/Mortal_Kombat_1/Scorpion",
            hp = "1000",
            umo = listOf("Hellfire Teleport"),
            gameProperties = WikiMKCharProperties(hpMod = "1.0", throwDmg = "130"),
        )
        val expected = Character(
            id = "scorpion",
            displayName = "Scorpion",
            hp = "1000",
            umo = listOf("Hellfire Teleport"),
            gameProperties = MKCharProperties(hpMod = "1.0", throwDmg = "130"),
        )

        // when
        val result = wikiCharacter.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wiki move keeps its frame data and gets its group and filters`() {
        // given
        val wikiMove = WikiMove(
            input = "f,n,d,d/f+2",
            remoteId = "Jin-f,n,d,df+2",
            name = "Electric Wind Hook Fist",
            damage = "25",
            startup = "i11",
            onBlock = "+5",
            onHit = "+34a (+24)",
            aliases = listOf("EWHF"),
            urls = WikiMove.Urls(
                wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-f,n,d,df+2",
                videoUrl = "https://wavu.wiki/images/Jin-ewhf.mp4",
            ),
            gameProperties = WikiT8Properties(isHoming = true),
        )
        val expected = Move(
            input = "f,n,d,d/f+2",
            remoteId = "Jin-f,n,d,df+2",
            name = "Electric Wind Hook Fist",
            damage = "25",
            startup = "i11",
            onBlock = "+5",
            onHit = "+34a (+24)",
            aliases = listOf("EWHF"),
            urls = Move.Urls(
                wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-f,n,d,df+2",
                videoUrl = "https://wavu.wiki/images/Jin-ewhf.mp4",
            ),
            gameProperties = T8Properties(isHoming = true),
            groupId = "Motion input",
            filterNameSet = setOf("Homing"),
        )

        // when
        val result = wikiMove.toDomain(groupId = "Motion input", filterNameSet = setOf("Homing"))

        // then
        assertThat(result).isEqualTo(expected)
    }
}
