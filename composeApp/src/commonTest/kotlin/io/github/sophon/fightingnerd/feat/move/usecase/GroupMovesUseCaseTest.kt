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
            characterId = "id",
            input = "BAD.3h.2",
            urls = Move.Urls(wikiUrl = ""),
            t8Properties = Move.T8Properties(isHeat = true),
        )
        val heat2 = Move(
            id = "armor king-db2h.1",
            characterId = "id",
            input = "db2h.1",
            urls = Move.Urls(wikiUrl = ""),
            t8Properties = Move.T8Properties(isHeat = true),
        )
        val n1 = Move(
            id = "armor king-1",
            characterId = "id",
            input = "1",
            urls = Move.Urls(wikiUrl = ""),
        )
        val n2 = Move(
            id = "armor king-2",
            characterId = "id",
            input = "2",
            urls = Move.Urls(wikiUrl = ""),
        )
        val f = Move(
            id = "armor king-f21",
            characterId = "id",
            input = "f21",
            urls = Move.Urls(wikiUrl = ""),
        )
        val motion = Move(
            id = "armor king-ff4",
            characterId = "id",
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
            id = "",
            characterId = "",
            input = "5k",
            urls = Move.Urls(wikiUrl = ""),
            ggstProperties = Move.GGSTProperties(type = "normal"),
        )
        val fs = Move(
            id = "",
            characterId = "",
            input = "fs",
            urls = Move.Urls(wikiUrl = ""),
            ggstProperties = Move.GGSTProperties(type = "normal"),
        )
        val u = Move(
            id = "",
            characterId = "",
            input = "6d/4d",
            urls = Move.Urls(wikiUrl = ""),
            ggstProperties = Move.GGSTProperties(type = "other"),
        )
        val sp = Move(
            id = "",
            characterId = "",
            input = "214p",
            urls = Move.Urls(wikiUrl = ""),
            ggstProperties = Move.GGSTProperties(type = "special"),
        )
        val overdrive = Move(
            id = "",
            characterId = "",
            input = "632146h",
            urls = Move.Urls(wikiUrl = ""),
            ggstProperties = Move.GGSTProperties(type = "super"),
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
            id = "",
            characterId = "",
            input = "5lp",
            urls = Move.Urls(wikiUrl = ""),
            sf6Properties = Move.SF6Properties(type = Move.SF6Properties.Type.GROUND_NORMAL)
        )
        val n2 = Move(
            id = "",
            characterId = "",
            input = "jmp",
            urls = Move.Urls(wikiUrl = ""),
            sf6Properties = Move.SF6Properties(type = Move.SF6Properties.Type.AIR_NORMAL)
        )
        val dr = Move(
            id = "",
            characterId = "",
            input = "6hphk",
            urls = Move.Urls(wikiUrl = ""),
            sf6Properties = Move.SF6Properties(type = Move.SF6Properties.Type.DRIVE)
        )
        val sp = Move(
            id = "",
            characterId = "",
            input = "236mp",
            urls = Move.Urls(wikiUrl = ""),
            sf6Properties = Move.SF6Properties(type = Move.SF6Properties.Type.SPECIAL)
        )
        val superArt = Move(
            id = "",
            characterId = "",
            input = "214214k",
            urls = Move.Urls(wikiUrl = ""),
            sf6Properties = Move.SF6Properties(type = Move.SF6Properties.Type.SUPER)
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
        val result = usecase.invoke(moveList, tekkenGroups)

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
        val result = usecase.invoke(moveList, ggGroups)

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
        val result = usecase.invoke(moveList, sfGroups)

        //then
        assertThat(result.first).isEqualTo(expected)
    }
}