package io.github.sophon.fightingnerd.screens.moveList

import io.github.sophon.fightingnerd.screens.moveList.domain.usecase.FetchCharacterListUseCase
import io.github.sophon.fightingnerd.screens.moveList.domain.usecase.FetchMoveListUseCase
import io.github.sophon.fightingnerd.screens.moveList.ui.MoveListVM
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val moveListModule = module {
    viewModel { params ->
        val gameId: String = params.get()
        val charName: String = params.get()

        MoveListVM(
            gameId = gameId,
            charName = charName,
            featureRegistry = get(),
            fetchMoveListUseCase = get(),
        )
    }

    singleOf(::FetchMoveListUseCase)
    singleOf(::FetchCharacterListUseCase)
}