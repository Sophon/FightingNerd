package io.github.sophon.botdiscord.adapter.outbound.wiki

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.discord.adapter.outbound.wiki.toDomain
import io.github.sophon.discord.adapter.outbound.wiki.toFilter
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.frameData.MoveId
import io.github.sophon.discord.app.model.frameData.MoveType
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.WavuFilters
import io.github.sophon.wiki.model.game.DBFZMoveProperties
import io.github.sophon.wiki.model.game.GGMoveProperties
import io.github.sophon.wiki.model.game.SF6MoveProperties
import io.github.sophon.wiki.model.game.T8Properties
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.model.wiki.Wiki
import kotlin.test.Test

class MoveMappersTest {
    //region toDomain
    @Test
    fun `tekken move is always expanded with guard, recovery and stance`() {
        // given
        val move = Move(
            input = "f,n,d,df+2",
            name = "Electric Wind God Fist",
            damage = "25",
            startup = "i11~12",
            onBlock = "+5",
            onHit = "+5a (+15)",
            onCH = "+5a (+15)",
            recovery = "r28",
            guard = "h",
            notes = listOf("Balcony break"),
            aliases = listOf("ewgf"),
            urls = Move.Urls(
                wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-f,n,d,df+2",
                videoUrl = VIDEO_URL,
                hitboxImageList = listOf("https://wavu.wiki/img/Jin_ewgf_hitbox.png"),
            ),
            gameProperties = T8Properties(isHeat = true, stance = "ZEN"),
        )
        val expected = MoveResponse(
            game = Game.Tekken8,
            input = "f,n,d,df+2",
            url = "https://wavu.wiki/t/Jin_movelist#Jin-f,n,d,df+2",
            characterName = "Jin",
            characterUrl = "https://wavu.wiki/t/Jin",
            moveName = "Electric Wind God Fist",
            characterImageUrl = JIN_ICON_URL,
            primaryFields = listOf(
                BotResponse.Field(title = "Startup", value = "i11~12"),
                BotResponse.Field(title = "Hit", value = "+5a (+15)"),
                BotResponse.Field(title = "Block", value = "+5"),
                BotResponse.Field(title = "Counter", value = "+5a (+15)"),
                BotResponse.Field(title = "Damage", value = "25"),
                BotResponse.Field(title = "Guard", value = "h"),
                BotResponse.Field(title = "Recovery", value = "r28"),
            ),
            dataSource = BotResponse.DataSource(
                name = "Tekken 8 (Wavu Wiki)",
                iconUrl = Wiki.Wavu.iconUrl,
                color = Wiki.Wavu.color,
            ),
            secondaryFields = emptyList(),
            aliasList = listOf("ewgf"),
            noteList = listOf("Balcony break"),
            videoUrl = VIDEO_URL,
            hitboxImageList = listOf("https://wavu.wiki/img/Jin_ewgf_hitbox.png"),
            stance = "ZEN",
            forceExpand = true,
            buttonSet = BotResponse.ButtonSet(
                buttonList = listOf(
                    mediaCommandButton(label = "Images", query = "jin::Tekken_8 f,n,d,df+2"),
                    mediaCommandButton(label = "Video", query = "jin::Tekken_8 f,n,d,df+2"),
                ),
            ),
        )

        // when
        val result = move.toDomain(jin)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `missing mandatory values are dashes`() {
        // given
        val move = Move(
            input = "1+2",
            startup = " ",
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1+2"),
            gameProperties = T8Properties(),
        )
        val expected = listOf("-", "-", "-", "-", "-", "-", "-")

        // when
        val result = move.toDomain(jin)

        // then
        assertThat(result.primaryFields.map { it.value }).isEqualTo(expected)
    }

    @Test
    fun `move with details and a video gets details and video buttons`() {
        // given
        val move = Move(
            input = "5K",
            active = "3",
            cancel = "Gatling, Special, Super",
            urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5K", videoUrl = VIDEO_URL),
            gameProperties = GGMoveProperties(level = "1", riscGain = " ", prorate = "90%"),
        )
        val expected = BotResponse.ButtonSet(
            buttonList = listOf(solDetailsButton, mediaCommandButton(label = "Video", query = "sol_badguy::GGST 5K")),
        )

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isEqualTo(expected)
    }

    @Test
    fun `move without details and a video gets only a video button`() {
        // given
        val move = Move(
            input = "5P",
            urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5P", videoUrl = VIDEO_URL),
            gameProperties = GGMoveProperties(),
        )
        val expected = BotResponse.ButtonSet(
            buttonList = listOf(mediaCommandButton(label = "Video", query = "sol_badguy::GGST 5P")),
        )

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isEqualTo(expected)
    }

    @Test
    fun `move with details and hitbox images gets an images button`() {
        // given
        val move = Move(
            input = "5K",
            cancel = "Gatling, Special, Super",
            urls = Move.Urls(
                wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5K",
                hitboxImageList = listOf(HITBOX_IMAGE_URL),
            ),
            gameProperties = GGMoveProperties(),
        )
        val expected = BotResponse.ButtonSet(
            buttonList = listOf(solDetailsButton, mediaCommandButton(label = "Images", query = "sol_badguy::GGST 5K")),
        )

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isEqualTo(expected)
    }

    @Test
    fun `move with details and move images gets an images button`() {
        // given
        val move = Move(
            input = "5K",
            cancel = "Gatling, Special, Super",
            urls = Move.Urls(
                wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5K",
                moveImageList = listOf(MOVE_IMAGE_URL),
            ),
            gameProperties = GGMoveProperties(),
        )
        val expected = BotResponse.ButtonSet(
            buttonList = listOf(solDetailsButton, mediaCommandButton(label = "Images", query = "sol_badguy::GGST 5K")),
        )

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isEqualTo(expected)
    }

    @Test
    fun `move with details and no media gets only the details button`() {
        // given
        val move = Move(
            input = "5K",
            cancel = "Gatling, Special, Super",
            urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5K"),
            gameProperties = GGMoveProperties(),
        )
        val expected = BotResponse.ButtonSet(buttonList = listOf(solDetailsButton))

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isEqualTo(expected)
    }

    @Test
    fun `move without details and images gets only an images button`() {
        // given
        val move = Move(
            input = "5P",
            urls = Move.Urls(
                wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5P",
                hitboxImageList = listOf(HITBOX_IMAGE_URL),
            ),
            gameProperties = GGMoveProperties(),
        )
        val expected = BotResponse.ButtonSet(
            buttonList = listOf(mediaCommandButton(label = "Images", query = "sol_badguy::GGST 5P")),
        )

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isEqualTo(expected)
    }

    @Test
    fun `guilty gear secondary fields skip blank values`() {
        // given
        val move = Move(
            input = "5K",
            cancel = "Gatling, Special, Super",
            urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5K"),
            gameProperties = GGMoveProperties(level = "1", riscGain = " ", prorate = "90%"),
        )
        val expected = listOf(
            BotResponse.Field(title = "Cancel", value = "Gatling, Special, Super"),
            BotResponse.Field(title = "Level", value = "1"),
            BotResponse.Field(title = "Prorate", value = "90%"),
        )

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.secondaryFields).isEqualTo(expected)
    }

    @Test
    fun `move without details or media has no buttons`() {
        // given
        val move = Move(
            input = "5P",
            urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5P"),
            gameProperties = GGMoveProperties(),
        )

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isNull()
    }

    @Test
    fun `street fighter juggle values are merged with dashes for blanks`() {
        // given
        val move = Move(
            input = "2HP",
            urls = Move.Urls(wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu#2HP"),
            gameProperties = SF6MoveProperties(blockStun = "17", jugStart = "1", jugIncrease = "3", jugLimit = null),
        )
        val expected = listOf(
            BotResponse.Field(title = "Blockstun", value = "17"),
            BotResponse.Field(title = "JGL st | inc | lim", value = "1 | 3 | -"),
        )

        // when
        val result = move.toDomain(character(game = Game.StreetFighter6, displayName = "Ryu"))

        // then
        assertThat(result.secondaryFields).isEqualTo(expected)
    }

    @Test
    fun `game without its own fields gets the non-blank default fields`() {
        // given
        val move = Move(
            input = "5L",
            damage = "400",
            guard = "All",
            active = "3",
            invulnerability = " ",
            urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/DBFZ/Goku_(Super_Saiyan)#5L"),
            gameProperties = DBFZMoveProperties(level = "1", kiGain = "5%"),
        )
        val expected = listOf(
            BotResponse.Field(title = "Startup", value = "-"),
            BotResponse.Field(title = "Hit", value = "-"),
            BotResponse.Field(title = "Block", value = "-"),
            BotResponse.Field(title = "Counter", value = "-"),
            BotResponse.Field(title = "Damage", value = "400"),
            BotResponse.Field(title = "Guard", value = "All"),
            BotResponse.Field(title = "Active", value = "3"),
            BotResponse.Field(title = "Level", value = "1"),
            BotResponse.Field(title = "Ki gain", value = "5%"),
        )

        // when
        val result = move.toDomain(character(game = Game.DBFZ, displayName = "Goku (Super Saiyan)"))

        // then
        assertThat(result.primaryFields).isEqualTo(expected)
    }

    @Test
    fun `move with only notes gets a details button`() {
        // given
        val move = Move(
            input = "5K",
            notes = listOf("Can be jump cancelled"),
            urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5K"),
            gameProperties = GGMoveProperties(),
        )
        val expected = BotResponse.ButtonSet(buttonList = listOf(solDetailsButton))

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isEqualTo(expected)
    }

    @Test
    fun `move with only aliases has no buttons`() {
        // given
        val move = Move(
            input = "6P",
            aliases = listOf("anti-air"),
            urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#6P"),
            gameProperties = GGMoveProperties(),
        )

        // when
        val result = move.toDomain(sol)

        // then
        assertThat(result.buttonSet).isNull()
    }

    @Test
    fun `tekken move with notes has no details button`() {
        // given
        val move = Move(
            input = "d/b+1",
            notes = listOf("Homing"),
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-d/b+1"),
            gameProperties = T8Properties(),
        )

        // when
        val result = move.toDomain(jin)

        // then
        assertThat(result.buttonSet).isNull()
    }

    @Test
    fun `only tekken is always expanded`() {
        // given
        val gameList = listOf(Game.Tekken8, Game.GGST, Game.StreetFighter6, Game.DBFZ)
        val expected = listOf(true, false, false, false)
        val move = Move(input = "5P", urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#5P"))

        // when
        val result = gameList.map { game -> move.toDomain(character(game = game)).forceExpand }

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toFilter
    @Test
    fun `each move type becomes its wavu filter`() {
        // given
        val expected = listOf(WavuFilters.PowerCrush, WavuFilters.Heat, WavuFilters.Homing)

        // when
        val result = listOf(MoveType.PC, MoveType.HEAT, MoveType.HOMING).map { it.toFilter() }

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}

private fun mediaCommandButton(label: String, query: String): BotResponse.EmbedButton {
    val button = BotResponse.EmbedButton(
        label = label,
        action = BotResponse.EmbedButton.Action.Command(command = Command.Media, query = query),
    )
    return button
}

private fun character(game: Game, displayName: String = "Sol Badguy"): Character {
    val character = Character(
        id = CharacterId(game = game, naturalId = displayName.lowercase().replace(" ", "_")),
        displayName = displayName,
        remoteQueryId = displayName,
        wikiUrl = "${game.wikiUrl}/${displayName.replace(" ", "_")}",
    )
    return character
}


private const val VIDEO_URL = "https://wavu.wiki/vid/Jin_ewgf.mp4"
private const val HITBOX_IMAGE_URL = "https://www.dustloop.com/wiki/images/GGST_Sol_Badguy_5K_Hitbox.png"
private const val MOVE_IMAGE_URL = "https://www.dustloop.com/wiki/images/GGST_Sol_Badguy_5K.png"
private const val JIN_ICON_URL = "https://wavu.wiki/img/Jin_icon.png"
private val jin = Character(
    id = CharacterId(game = Game.Tekken8, naturalId = "jin"),
    displayName = "Jin",
    remoteQueryId = "Jin",
    wikiUrl = "https://wavu.wiki/t/Jin",
    images = Character.Images(iconUrl = JIN_ICON_URL),
)
private val sol = character(game = Game.GGST)
private val solDetailsButton = BotResponse.EmbedButton(
    label = "Details",
    action = BotResponse.EmbedButton.Action.Expand(
        moveId = MoveId(game = Game.GGST, characterId = "sol_badguy", input = "5K"),
    ),
)
