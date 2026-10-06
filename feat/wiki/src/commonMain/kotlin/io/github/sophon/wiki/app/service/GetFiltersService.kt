package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.inPort.GetFiltersUseCase
import io.github.sophon.wiki.model.Filter
import io.github.sophon.wiki.model.wiki.Game

internal class GetFiltersService : GetFiltersUseCase {
    override fun invoke(game: Game): Set<Filter> {
        TODO("Not yet implemented")
    }
}
