package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.inPort.GetFiltersUseCase
import io.github.sophon.wiki.model.BBFilters
import io.github.sophon.wiki.model.Filter
import io.github.sophon.wiki.model.GGFilters
import io.github.sophon.wiki.model.MBFilters
import io.github.sophon.wiki.model.UniFilters
import io.github.sophon.wiki.model.VSAVFilters
import io.github.sophon.wiki.model.WavuFilters
import io.github.sophon.wiki.model.wiki.Game

internal class GetFiltersService : GetFiltersUseCase {
    override fun invoke(game: Game): Set<Filter> {
        val filters = when (game) {
            Game.Tekken8 -> setOf(
                WavuFilters.PowerCrush,
                WavuFilters.Heat,
                WavuFilters.Homing,
                WavuFilters.Throw,
                WavuFilters.Stance,
                WavuFilters.LowCrush,
                WavuFilters.HighCrush,
            )

            Game.BBCF -> setOf(BBFilters.Invincible)
            Game.GGST -> setOf(GGFilters.Invincible)

            Game.MBTL -> setOf(MBFilters.Invincible)
            Game.Uni2 -> setOf(UniFilters.Invincible)
            Game.VSAV -> setOf(VSAVFilters.Invincible)

            Game.StreetFighter6,
            Game.MK1,
            Game.AVL,
            Game.Xko,
            Game.KoFXV,
            Game.COTW,
            Game.DBFZ,
            Game.GBVSR,
            Game.MTFS,
            Game.ROA2 -> emptySet()
        }
        return filters
    }
}
