package io.github.sophon.fightingnerd.screens.moveList

import io.github.sophon.fightingnerd.screens.moveList.domain.usecase.FetchCharacterListUseCase
import io.github.sophon.fightingnerd.screens.moveList.ui.MoveListVM
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val moveListModule = module {
    viewModel { params ->
        val charName: String = params.get()
        val wikiQualifier: String = params.get()
        MoveListVM(
            charName = charName,
            fetchMoveListUseCase = get(named(wikiQualifier))
        )
    }

    singleOf(::FetchCharacterListUseCase)
}