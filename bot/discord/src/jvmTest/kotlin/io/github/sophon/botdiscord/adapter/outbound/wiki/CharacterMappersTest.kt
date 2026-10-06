package io.github.sophon.botdiscord.adapter.outbound.wiki

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.discord.adapter.outbound.wiki.toDomain
import io.github.sophon.discord.adapter.outbound.wiki.toGameList
import io.github.sophon.discord.adapter.outbound.wiki.toWikiCharacterId
import io.github.sophon.discord.app.model.GameList
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.wiki.WikiFeatureInfo
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterGameProperties
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.game.GGCharProperties
import io.github.sophon.wiki.model.game.SFCharProperties
import io.github.sophon.wiki.model.game.Uni2CharProperties
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.model.wiki.Wiki
import kotlin.test.Test
import io.github.sophon.discord.app.model.frameData.CharacterId as DiscordCharacterId

class CharacterMappersTest {
    //region toDomain
    @Test
    fun `character becomes a character response with its wiki as data source`() {
        // given
        val character = Character(
            id = CharacterId(game = Game.Tekken8, naturalId = "jin"),
            displayName = "Jin",
            remoteQueryId = "Jin",
            wikiUrl = "https://wavu.wiki/t/Jin",
            aliasList = listOf("jim"),
            hp = "180",
        )
        val expected = CharacterResponse(
            id = "jin",
            game = Game.Tekken8,
            displayName = "Jin",
            url = "https://wavu.wiki/t/Jin",
            dataSource = BotResponse.DataSource(
                name = "Tekken 8 (Wavu Wiki)",
                iconUrl = Wiki.Wavu.iconUrl,
                color = Wiki.Wavu.color,
            ),
            aliasList = listOf("jim"),
            propertyList = listOf(BotResponse.Field(title = "Health", value = "180")),
        )

        // when
        val result = character.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `guilty gear properties merge the backdash and add frame suffixes`() {
        // given
        val character = character(
            game = Game.GGST,
            hp = "420",
            umo = listOf("Run", "Double jump"),
            gameProperties = ggCharProperties(
                guts = "2",
                prejump = "4",
                bwdDashDuration = "23",
                bwdDashInvulnerability = "1-7F",
                dashInitialSpd = "8.75",
            ),
        )
        val expected = listOf(
            BotResponse.Field(title = "Health", value = "420"),
            BotResponse.Field(title = "Guts", value = "2"),
            BotResponse.Field(title = "Backdash", value = "23F duration\n1-7F invuln"),
            BotResponse.Field(title = "Dash initial speed", value = "8.75"),
            BotResponse.Field(title = "Jump startup", value = "4F"),
            BotResponse.Field(title = "Unique movement", value = "Run, Double jump"),
        )

        // when
        val result = character.toDomain()

        // then
        assertThat(result.propertyList).isEqualTo(expected)
    }

    @Test
    fun `guilty gear backdash without frame data is left out`() {
        // given
        val character = character(game = Game.GGST, gameProperties = ggCharProperties(guts = "2"))
        val expected = listOf(BotResponse.Field(title = "Guts", value = "2"))

        // when
        val result = character.toDomain()

        // then
        assertThat(result.propertyList).isEqualTo(expected)
    }

    @Test
    fun `under night backdash lists its invulnerability ranges`() {
        // given
        val character = character(
            game = Game.Uni2,
            displayName = "Hyde",
            gameProperties = Uni2CharProperties(
                jumpStartup = "4F",
                bDashDuration = "28",
                bDashFullInvulStart = "1",
                bDashFullInvulEnd = "8",
                bDashThrowInvulStart = "9",
                bDashThrowInvulEnd = "10",
            ),
        )
        val expected = listOf(
            BotResponse.Field(title = "Prejump", value = "4F"),
            BotResponse.Field(title = "Backdash", value = "28F (1-8 full, 9-10 throw)"),
        )

        // when
        val result = character.toDomain()

        // then
        assertThat(result.propertyList).isEqualTo(expected)
    }

    @Test
    fun `under night backdash drops a half-open invulnerability range`() {
        // given
        val character = character(
            game = Game.Uni2,
            displayName = "Hyde",
            gameProperties = Uni2CharProperties(bDashDuration = "28", bDashFullInvulStart = "1"),
        )
        val expected = listOf(BotResponse.Field(title = "Backdash", value = "28F"))

        // when
        val result = character.toDomain()

        // then
        assertThat(result.propertyList).isEqualTo(expected)
    }

    @Test
    fun `street fighter properties skip blank values`() {
        // given
        val character = character(
            game = Game.StreetFighter6,
            displayName = "Ryu",
            gameProperties = SFCharProperties(
                fwdWalkSpd = "0.047",
                bwdWalkSpd = " ",
                fwdDashSpd = null,
                bwdDashSpd = null,
                fwdDashDist = null,
                bwdDashDist = null,
                dRushMin = null,
                dRushBlock = null,
                dRushMax = null,
                throwRange = "0.8",
                throwHurtbox = null,
                jumpSpd = null,
                jumpApex = null,
                fwdJumpDist = null,
                bwdJumpDist = null,
            ),
        )
        val expected = listOf(
            BotResponse.Field(title = "Forward walk speed", value = "0.047"),
            BotResponse.Field(title = "Throw range", value = "0.8"),
        )

        // when
        val result = character.toDomain()

        // then
        assertThat(result.propertyList).isEqualTo(expected)
    }
    //endregion

    //region toGameList
    @Test
    fun `games are listed under the wiki feature`() {
        // given
        val expected = GameList(
            gameList = listOf(Game.Tekken8, Game.GGST),
            dataSource = BotResponse.DataSource(
                name = WikiFeatureInfo.featureInfo.name,
                iconUrl = WikiFeatureInfo.featureInfo.iconUrl.orEmpty(),
            ),
        )

        // when
        val result = setOf(Game.Tekken8, Game.GGST).toGameList()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toWikiCharacterId
    @Test
    fun `character id becomes a wiki character id`() {
        // given
        val characterId = DiscordCharacterId(game = Game.Tekken8, characterId = "jin")
        val expected = CharacterId(game = Game.Tekken8, naturalId = "jin")

        // when
        val result = characterId.toWikiCharacterId()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}

private fun character(
    game: Game,
    displayName: String = "Sol Badguy",
    hp: String? = null,
    umo: List<String> = emptyList(),
    gameProperties: CharacterGameProperties,
): Character {
    val character = Character(
        id = CharacterId(game = game, naturalId = displayName.lowercase().replace(" ", "_")),
        displayName = displayName,
        remoteQueryId = displayName,
        wikiUrl = "${game.wikiUrl}/${displayName.replace(" ", "_")}",
        hp = hp,
        umo = umo,
        gameProperties = gameProperties,
    )
    return character
}

@Suppress("LongParameterList")
private fun ggCharProperties(
    guts: String? = null,
    prejump: String? = null,
    bwdDashDuration: String? = null,
    bwdDashInvulnerability: String? = null,
    dashInitialSpd: String? = null,
): GGCharProperties {
    val properties = GGCharProperties(
        defense = null,
        guts = guts,
        guardBalance = null,
        prejump = prejump,
        bwdDash = null,
        bwdDashDuration = bwdDashDuration,
        bwdDashInvulnerability = bwdDashInvulnerability,
        bwdDashAirborne = null,
        bwdDashDist = null,
        fwdDash = null,
        jumpDuration = null,
        highJumpDuration = null,
        jumpHeight = null,
        highJumpHeight = null,
        earliestIAD = null,
        adDuration = null,
        abdDuration = null,
        adDist = null,
        abdDist = null,
        movementTension = null,
        jumpTension = null,
        airDashTension = null,
        walkSpd = null,
        bwdWalkSpd = null,
        dashInitialSpd = dashInitialSpd,
        dashAcceleration = null,
        dashFriction = null,
        jumpGravity = null,
        highJumpGravity = null,
        boostAttack = null,
        boostDefense = null,
    )
    return properties
}
