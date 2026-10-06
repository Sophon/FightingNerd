package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.inPort.GetGroupsUseCase
import io.github.sophon.wiki.model.AVLGroups
import io.github.sophon.wiki.model.BBCFGroups
import io.github.sophon.wiki.model.COTWGroups
import io.github.sophon.wiki.model.DBFZGroups
import io.github.sophon.wiki.model.DreamCancelGroups
import io.github.sophon.wiki.model.GBVSRGroups
import io.github.sophon.wiki.model.GGSTGroups
import io.github.sophon.wiki.model.Group
import io.github.sophon.wiki.model.KofGroups
import io.github.sophon.wiki.model.MBGroups
import io.github.sophon.wiki.model.MTFSGroups
import io.github.sophon.wiki.model.MizuumiGroups
import io.github.sophon.wiki.model.RoaGroups
import io.github.sophon.wiki.model.SFGroups
import io.github.sophon.wiki.model.UniGroups
import io.github.sophon.wiki.model.WavuGroups
import io.github.sophon.wiki.model.wiki.Game

/**
 * [extras] are Tekken stances - each becomes its own group after the generic ones.
 */
internal class GetGroupsService : GetGroupsUseCase {
    @Suppress("LongMethod")
    override fun invoke(
        game: Game,
        extras: List<String>,
    ): List<Group> {
        val groups = when (game) {
            Game.Tekken8 -> listOf(
                WavuGroups.Heat,
                WavuGroups.Neutral,
                WavuGroups.Forward,
                WavuGroups.DownForward,
                WavuGroups.Down,
                WavuGroups.DownBack,
                WavuGroups.Back,
                WavuGroups.Up,
                WavuGroups.UpBack,
                WavuGroups.Motion,
                WavuGroups.Crouch,
                WavuGroups.WS,
            ) + extras.map { WavuGroups.Stance(it) }

            Game.StreetFighter6 -> listOf(
                SFGroups.Normal,
                SFGroups.Throw,
                SFGroups.Special,
                SFGroups.Drive,
                SFGroups.Super,
                SFGroups.Taunt,
            )
            Game.AVL -> listOf(
                AVLGroups.Normal,
                AVLGroups.Special,
                AVLGroups.Flow,
                AVLGroups.Super,
            )

            Game.KoFXV -> listOf(
                DreamCancelGroups.Normal,
                KofGroups.Rush,
                KofGroups.Throw,
                DreamCancelGroups.Special,
                KofGroups.Climax,
            )
            Game.COTW -> listOf(
                DreamCancelGroups.Normal,
                COTWGroups.Combination,
                COTWGroups.Throw,
                COTWGroups.Rev,
                COTWGroups.FeintDodge,
                DreamCancelGroups.Special,
                COTWGroups.HiddenGear,
            )

            Game.GGST -> listOf(
                GGSTGroups.Normal,
                GGSTGroups.Universal,
                GGSTGroups.Special,
                GGSTGroups.Super,
            )
            Game.BBCF -> listOf(
                BBCFGroups.Normal,
                BBCFGroups.Universal,
                BBCFGroups.Special,
                BBCFGroups.Super,
                BBCFGroups.Exceed,
                BBCFGroups.Astral,
            )
            Game.DBFZ -> listOf(
                DBFZGroups.Normal,
                DBFZGroups.Special,
                DBFZGroups.Assist,
                DBFZGroups.Super,
            )
            Game.GBVSR -> listOf(
                GBVSRGroups.Normal,
                GBVSRGroups.Universal,
                GBVSRGroups.Special,
                GBVSRGroups.Unique,
                GBVSRGroups.Super,
            )
            Game.MTFS -> listOf(
                MTFSGroups.Normal,
                MTFSGroups.Special,
                MTFSGroups.Unique,
                MTFSGroups.Assist,
                MTFSGroups.Super,
                MTFSGroups.Tokon,
            )

            Game.MBTL -> listOf(
                MizuumiGroups.Normal,
                MBGroups.Universal,
                MizuumiGroups.Special,
                MBGroups.Super,
            )
            Game.Uni2 -> listOf(
                UniGroups.Normal,
                UniGroups.Universal,
                UniGroups.Special,
                UniGroups.Super,
            )
            Game.VSAV -> listOf(
                MizuumiGroups.Normal,
                MizuumiGroups.Special,
            )

            Game.ROA2 -> listOf(
                RoaGroups.Normal,
                RoaGroups.Strong,
                RoaGroups.Aerial,
                RoaGroups.Special,
                RoaGroups.Throw,
            )

            Game.MK1,
            Game.Xko -> emptyList()
        }
        return groups
    }
}

