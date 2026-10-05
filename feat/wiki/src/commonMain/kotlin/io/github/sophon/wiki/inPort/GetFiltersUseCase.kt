package io.github.sophon.wiki.inPort

import io.github.sophon.wiki.model.Filter
import io.github.sophon.wiki.model.wiki.Game

interface GetFiltersUseCase {
    operator fun invoke(game: Game): Set<Filter>
}
