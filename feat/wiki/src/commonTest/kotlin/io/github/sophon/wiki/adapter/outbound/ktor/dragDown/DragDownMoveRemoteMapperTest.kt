package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.Roa2MoveProperties
import kotlin.test.Test

class DragDownMoveRemoteMapperTest {
    @Test
    fun `default mode adds nothing to the input`() {
        // given
        val dto = DragDownMoveSource.olympiaBair
        val character = DragDownCharacterSource.olympia
        val expected = Move(
            remoteId = dto.attackID,
            startup = dto.startup,
            active = "6-17",
            recovery = dto.endlag,
            name = dto.attack,
            input = dto.attack.orEmpty().lowercase(),
            urls = Move.Urls(
                wikiUrl = "https://dragdown.wiki/wiki/RoA2/Olympia",
            ),
            gameProperties = Roa2MoveProperties(
                caption = dto.caption,
                hitboxCaption = dto.hitboxCaption,
                startupNotes = null,
                totalActiveNotes = null,
                endlagNotes = null,
                cancelNotes = dto.cancelNotes,
                landingLag = dto.landingLag,
                landingLagNotes = null,
                iasa = dto.iasa,
                iasaNotes = null,
                totalDuration = dto.totalDuration,
                totalDurationNotes = null,
                ledgeGrabFrame = dto.ledgeGrabFrame,
                ledgeGrabFrameNotes = null,
                hitID = dto.hitID,
                hitMoveID = dto.hitMoveID,
                hitName = dto.hitName,
                hitActive = dto.hitActive,
                customShieldSafety = emptyList(),
                uniqueField = emptyList(),
                articleID = dto.articleID,
                notes = null,
                advNotes = null,
            ),
        )

        // when
        val result = dto.toMove(DragDownCharacterSource.olympia)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `mode drops its parenthesised part`() {
        // given
        val dto = DragDownMoveSource.forsburnDSpecialEmptyInhale
        val character = DragDownCharacterSource.forsburn
        val expected = Move(
            remoteId = dto.attackID,
            startup = dto.startup,
            active = "N/A",
            recovery = "N/A",
            name = dto.attack,
            input = "dspecialemptyinhale",
            urls = Move.Urls(
                wikiUrl = "https://dragdown.wiki/wiki/RoA2/Forsburn",
            ),
            gameProperties = Roa2MoveProperties(
                mode = "emptyinhale",
                caption = dto.caption,
                hitboxCaption = dto.hitboxCaption,
                startupNotes = null,
                totalActiveNotes = null,
                endlagNotes = null,
                cancelNotes = dto.cancelNotes,
                landingLag = null,
                landingLagNotes = null,
                iasa = dto.iasa,
                iasaNotes = null,
                totalDuration = dto.totalDuration,
                totalDurationNotes = null,
                ledgeGrabFrame = dto.ledgeGrabFrame,
                ledgeGrabFrameNotes = null,
                hitID = dto.hitID,
                hitMoveID = dto.hitMoveID,
                hitName = dto.hitName,
                hitActive = dto.hitActive,
                customShieldSafety = emptyList(),
                uniqueField = emptyList(),
                articleID = dto.articleID,
                notes = null,
                advNotes = null,
            ),
        )

        // when
        val result = dto.toMove(DragDownCharacterSource.forsburn)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `html in the cancel list becomes plain text`() {
        // given
        val dto = DragDownMoveSource.rannoUspecialDivekick
        val character = DragDownCharacterSource.ranno
        val expected = Move(
            remoteId = dto.attackID,
            startup = dto.startup,
            active = dto.totalActive,
            recovery = dto.endlag,
            name = dto.attack,
            input = "uspecialdivekick",
            cancel = "Double Jump & Wall Jump: 43+\nLedge Grab: 57+",
            urls = Move.Urls(
                wikiUrl = "https://dragdown.wiki/wiki/RoA2/Ranno",
            ),
            gameProperties = Roa2MoveProperties(
                mode = "divekick",
                caption = dto.caption,
                hitboxCaption = dto.hitboxCaption,
                startupNotes = null,
                totalActiveNotes = null,
                endlagNotes = null,
                cancelNotes = dto.cancelNotes,
                landingLag = dto.landingLag,
                landingLagNotes = null,
                iasa = dto.iasa,
                iasaNotes = null,
                totalDuration = dto.totalDuration,
                totalDurationNotes = null,
                ledgeGrabFrame = dto.ledgeGrabFrame,
                ledgeGrabFrameNotes = null,
                hitID = dto.hitID,
                hitMoveID = dto.hitMoveID,
                hitName = dto.hitName,
                hitActive = dto.hitActive,
                customShieldSafety = emptyList(),
                uniqueField = emptyList(),
                articleID = dto.articleID,
                notes = null,
                advNotes = null,
            ),
        )

        // when
        val result = dto.toMove(DragDownCharacterSource.ranno)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `wiki links in notes become markdown links`() {
        // given
        val dto = DragDownMoveSource.olympiaDSpecialFADC
        val character = DragDownCharacterSource.olympia
        val expected = Move(
            remoteId = dto.attackID,
            startup = dto.startup,
            active = "N/A",
            recovery = "N/A",
            name = dto.attack,
            input = "dspecialfadc",
            cancel = "Ledge Grab: 6",
            urls = Move.Urls(
                wikiUrl = "https://dragdown.wiki/wiki/RoA2/Olympia",
            ),
            notes = listOf(
                "This move has a 36 frame [cooldown](https://dragdown.wiki/wiki/RoA2/System_Mechanics/Misc#Cooldowns) once endlag begins.",
            ),
            gameProperties = Roa2MoveProperties(
                mode = "fadc",
                caption = dto.caption,
                hitboxCaption = dto.hitboxCaption,
                startupNotes = null,
                totalActiveNotes = null,
                endlagNotes = null,
                cancelNotes = dto.cancelNotes,
                landingLag = null,
                landingLagNotes = null,
                iasa = dto.iasa,
                iasaNotes = null,
                totalDuration = dto.totalDuration,
                totalDurationNotes = null,
                ledgeGrabFrame = dto.ledgeGrabFrame,
                ledgeGrabFrameNotes = null,
                hitID = dto.hitID,
                hitMoveID = dto.hitMoveID,
                hitName = dto.hitName,
                hitActive = dto.hitActive,
                customShieldSafety = emptyList(),
                uniqueField = emptyList(),
                articleID = dto.articleID,
                advNotes = null,
            ),
        )

        // when
        val result = dto.toMove(DragDownCharacterSource.olympia)

        //then
        assertThat(result).isEqualTo(expected)
    }
}

private fun DragDownMoveResponseDto.toMove(character: Character): Move {
    val move = listOf(this).toDomain(character, imageUrlMap = emptyMap()).single()
    return move
}

private object DragDownMoveSource {
    val olympiaBair = DragDownMoveResponseDto(
        chara = "Olympia",
        attack = "Bair",
        attackID = "Bair",
        mode = "Default",
        image = listOf("RoA2_Olympia_Bair_0.png"),
        hitbox = listOf(""),
        caption = listOf(""),
        hitboxCaption = listOf(""),
        startup = "6",
        startupNotes = null,
        totalActive = "6-17",
        totalActiveNotes = null,
        endlag = "12",
        endlagNotes = null,
        cancel = listOf(""),
        cancelNotes = listOf(""),
        landingLag = "7",
        landingLagNotes = null,
        iasa = "30",
        iasaNotes = null,
        totalDuration = "35",
        totalDurationNotes = null,
        ledgeGrabFrame = "36",
        ledgeGrabFrameNotes = null,
        hitID = listOf("Early", "Late"),
        hitMoveID = listOf("Bair", "Bair"),
        hitName = listOf("Early (Strong)", "Late (Weak)"),
        hitActive = listOf("6-8", "9-17"),
        customShieldSafety = listOf("-", "-"),
        uniqueField = listOf("-", "-"),
        articleID = listOf(""),
        notes = null,
        advNotes = null,
    )
    val olympiaDSpecialFADC = DragDownMoveResponseDto(
        chara = "Olympia",
        attack = "Dspecial",
        attackID = "Dspecial",
        mode = "FADC",
        image = listOf("RoA2_Olympia_Dspecial_3.png"),
        hitbox = listOf(""),
        caption = listOf("Focus Attack Dash Cancel"),
        hitboxCaption = listOf(""),
        startup = "5",
        startupNotes = null,
        totalActive = "N/A",
        totalActiveNotes = null,
        endlag = "N/A",
        endlagNotes = null,
        cancel = listOf("Ledge Grab: 6"),
        cancelNotes = listOf(""),
        landingLag = null,
        landingLagNotes = null,
        iasa = "13",
        iasaNotes = null,
        totalDuration = "25",
        totalDurationNotes = null,
        ledgeGrabFrame = "13",
        ledgeGrabFrameNotes = null,
        hitID = listOf(""),
        hitMoveID = listOf(""),
        hitName = listOf(""),
        hitActive = listOf(""),
        customShieldSafety = listOf(""),
        uniqueField = listOf(""),
        articleID = listOf(""),
        notes = "This move has a 36 frame [[RoA2/System Mechanics/Misc#Cooldowns|cooldown]] once endlag begins.",
        advNotes = null,
    )
    val rannoUspecialDivekick = DragDownMoveResponseDto(
        chara = "Ranno",
        attack = "Uspecial",
        attackID = "Uspecial",
        mode = "Divekick",
        image = listOf("RoA2_Ranno_Uspecial_1.png"),
        hitbox = listOf(
            "RoA2_Ranno_Uspecial_Divekick_hb_0.png ",
            " RoA2_Ranno_Uspecial_Divekick_hb_1.png ",
            " RoA2_Ranno_Uspecial_Divekick_hb_2.png",
        ),
        caption = listOf(
            "&#039;&#039;&#039;Divekick&#039;&#039;&#039;&lt;br&gt;&#039;&#039;Get Out of Jail Free&#039;&#039;",
        ),
        hitboxCaption = listOf(
            "&#039;&#039;&#039;Divekick&#039;&#039;&#039;&lt;br&gt;Frame 9 ",
            " &#039;&#039;&#039;Divekick&#039;&#039;&#039;&lt;br&gt;Frames 10-56 ",
            " &#039;&#039;&#039;Divekick Landing Hit&#039;&#039;&#039;&lt;br&gt;Frame 1",
        ),
        startup = "9",
        startupNotes = null,
        totalActive = "9-56",
        totalActiveNotes = null,
        endlag = "10",
        endlagNotes = null,
        cancel = listOf(
            "&lt",
            "span class=&quot",
            "mod-color--other mod-color-other&quot",
            "&gt",
            "Double Jump&lt",
            "/span&gt",
            " &amp",
            " &lt",
            "span class=&quot",
            "mod-color--other mod-color-other&quot",
            "&gt",
            "Wall Jump&lt",
            "/span&gt",
            ": 43+&lt",
            "br&gt",
            "&lt",
            "span class=&quot",
            "mod-color--other mod-color-other&quot",
            "&gt",
            "Ledge Grab&lt",
            "/span&gt",
            ": 57+",
        ),
        cancelNotes = listOf(""),
        landingLag = "32",
        landingLagNotes = null,
        iasa = null,
        iasaNotes = null,
        totalDuration = null,
        totalDurationNotes = null,
        ledgeGrabFrame = null,
        ledgeGrabFrameNotes = null,
        hitID = listOf("Divekick", "Divekick Landing"),
        hitMoveID = listOf("Uspecial", "Uspecial"),
        hitName = listOf("Divekick", "Landing Hit"),
        hitActive = listOf("9-56", "...1"),
        customShieldSafety = listOf("-", "-"),
        uniqueField = listOf("-", "-"),
        articleID = listOf(""),
        notes = null,
        advNotes = null,
    )
    val forsburnDSpecialEmptyInhale = DragDownMoveResponseDto(
        chara = "Forsburn",
        attack = "Dspecial",
        attackID = "Dspecial",
        mode = "Empty Inhale (Yippee!)",
        image = listOf("RoA2_Forsburn_Dspecial_1.png"),
        hitbox = listOf(""),
        caption = listOf(""),
        hitboxCaption = listOf(""),
        startup = "8",
        startupNotes = null,
        totalActive = "N/A",
        totalActiveNotes = null,
        endlag = "N/A",
        endlagNotes = null,
        cancel = listOf(""),
        cancelNotes = listOf(""),
        landingLag = null,
        landingLagNotes = null,
        iasa = "14",
        iasaNotes = null,
        totalDuration = "23",
        totalDurationNotes = null,
        ledgeGrabFrame = "14",
        ledgeGrabFrameNotes = null,
        hitID = listOf(""),
        hitMoveID = listOf(""),
        hitName = listOf(""),
        hitActive = listOf(""),
        customShieldSafety = listOf(""),
        uniqueField = listOf(""),
        articleID = listOf(""),
        notes = null,
        advNotes = null,
    )
}

private object DragDownCharacterSource {
    val olympia = Character(
        id = CharacterId(Game.ROA2, "Olympia"),
        displayName = "Olympia",
        remoteQueryId = "Olympia",
        wikiUrl = "https://dragdown.wiki/wiki/RoA2/Olympia",
    )
    val forsburn = Character(
        id = CharacterId(Game.ROA2, "Forsburn"),
        displayName = "Forsburn",
        remoteQueryId = "Forsburn",
        wikiUrl = "https://dragdown.wiki/wiki/RoA2/Forsburn",
    )
    val ranno = Character(
        id = CharacterId(Game.ROA2, "Ranno"),
        displayName = "Ranno",
        remoteQueryId = "Ranno",
        wikiUrl = "https://dragdown.wiki/wiki/RoA2/Ranno",
    )
}
