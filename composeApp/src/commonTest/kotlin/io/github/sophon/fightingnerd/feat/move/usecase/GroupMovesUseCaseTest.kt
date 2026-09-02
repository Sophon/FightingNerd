package io.github.sophon.fightingnerd.feat.move.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiSuperCombo.domain.SFGroups
import io.github.sophon.wikidustloop.integration.model.GGSTGroups
import io.github.sophon.wikiwavu.integration.model.TekkenGroups
import kotlin.test.Test

internal class GroupMovesUseCaseTest {
    //region Setup
    val tekkenGroups = listOf(
        TekkenGroups.Heat,
        TekkenGroups.Neutral,
        TekkenGroups.Forward,
        TekkenGroups.DownForward,
        TekkenGroups.Down,
        TekkenGroups.DownBack,
        TekkenGroups.Back,
        TekkenGroups.Up,
        TekkenGroups.UpBack,
        TekkenGroups.Motion,
        TekkenGroups.Crouch,
        TekkenGroups.WS,
    )
    private object TekkenMoves {
        val heat1 = Move(
            id = "armor king-BAD.3h.2",
            characterId = "armor king",
            input = "BAD.3h.2",
            urls = Move.Urls(wikiUrl = ""),
        )
        val heat2 = Move(
            id = "armor king-db2h.1",
            characterId = "armor king",
            input = "db2h.1",
            urls = Move.Urls(wikiUrl = ""),
        )
        val n1 = Move(
            id = "armor king-1",
            characterId = "armor king",
            input = "1",
            urls = Move.Urls(wikiUrl = ""),
        )
        val n2 = Move(
            id = "armor king-2",
            characterId = "armor king",
            input = "2",
            urls = Move.Urls(wikiUrl = ""),
        )
        val f = Move(
            id = "armor king-f21",
            characterId = "armor king",
            input = "f21",
            urls = Move.Urls(wikiUrl = ""),
        )
        val motion = Move(
            id = "armor king-ff4",
            characterId = "armor king",
            input = "ff4",
            urls = Move.Urls(wikiUrl = ""),
        )
    }

    val ggGroups = listOf(
        GGSTGroups.Normal,
        GGSTGroups.Universal,
        GGSTGroups.Special,
        GGSTGroups.Super,
    )
    private object GGMoves {
        val n1 = Move(
            id = "sol-5k",
            characterId = "sol",
            input = "5k",
            urls = Move.Urls(wikiUrl = ""),
            type = "normal",
        )
        val fs = Move(
            id = "sol-fs",
            characterId = "sol",
            input = "fs",
            urls = Move.Urls(wikiUrl = ""),
            type = "normal",
        )
        val u = Move(
            id = "sol-6d",
            characterId = "sol",
            input = "6d/4d",
            urls = Move.Urls(wikiUrl = ""),
            type = "other",
        )
        val sp = Move(
            id = "sol-214p",
            characterId = "sol",
            input = "214p",
            urls = Move.Urls(wikiUrl = ""),
            type = "special",
        )
        val overdrive = Move(
            id = "sol-632146h",
            characterId = "sol",
            input = "632146h",
            urls = Move.Urls(wikiUrl = ""),
            type = "super",
        )
    }

    val sfGroups = listOf(
        SFGroups.Normal,
        SFGroups.Throw,
        SFGroups.Special,
        SFGroups.Drive,
        SFGroups.Super,
        SFGroups.Taunt,
    )
    private object SFMoves {
        val n1 = Move(
            id = "ryu-5lp",
            characterId = "ryu",
            input = "5lp",
            urls = Move.Urls(wikiUrl = ""),
            type = "ground_normal",
        )
        val n2 = Move(
            id = "ryu-jmp",
            characterId = "ryu",
            input = "jmp",
            urls = Move.Urls(wikiUrl = ""),
            type = "air_normal",
        )
        val dr = Move(
            id = "ryu-6hphk",
            characterId = "ryu",
            input = "6hphk",
            urls = Move.Urls(wikiUrl = ""),
            type = "drive",
        )
        val sp = Move(
            id = "ryu-236mp",
            characterId = "ryu",
            input = "236mp",
            urls = Move.Urls(wikiUrl = ""),
            type = "special",
        )
        val superArt = Move(
            id = "ryu-214214k",
            characterId = "ryu",
            input = "214214k",
            urls = Move.Urls(wikiUrl = ""),
            type = "super",
        )
    }

    val usecase = GroupMovesUseCase()
    //endregion

    @Test
    fun `usecase groups tekken moves`() {
        // given
        val moveList = listOf(
            TekkenMoves.n1,
            TekkenMoves.n2,
            TekkenMoves.heat1,
            TekkenMoves.heat2,
            TekkenMoves.f,
            TekkenMoves.motion,
        )
        val expected = listOf(
            TekkenMoves.heat1,
            TekkenMoves.heat2,
            TekkenMoves.n1,
            TekkenMoves.n2,
            TekkenMoves.f,
            TekkenMoves.motion,
        )

        // when
        val result = usecase(moveList, tekkenGroups)

        //then
        assertThat(result.first).isEqualTo(expected)
    }

    @Test
    fun `usecase groups gg moves`() {
        // given
        val moveList = listOf(
            GGMoves.sp,
            GGMoves.n1,
            GGMoves.overdrive,
            GGMoves.u,
            GGMoves.fs,
        )
        val expected = listOf(
            GGMoves.n1,
            GGMoves.fs,
            GGMoves.u,
            GGMoves.sp,
            GGMoves.overdrive,
        )

        // when
        val result = usecase(moveList, ggGroups)

        //then
        assertThat(result.first).isEqualTo(expected)
    }

    @Test
    fun `usecase groups sf moves`() {
        // given
        val moveList = listOf(
            SFMoves.superArt,
            SFMoves.sp,
            SFMoves.n1,
            SFMoves.dr,
            SFMoves.n2,
        )
        val expected = listOf(
            SFMoves.n1,
            SFMoves.n2,
            SFMoves.sp,
            SFMoves.dr,
            SFMoves.superArt,
        )

        // when
        val result = usecase(moveList, sfGroups)

        //then
        assertThat(result.first).isEqualTo(expected)
    }
}