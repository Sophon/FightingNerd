package io.github.sophon.cornerman.screens.moveList

import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchCharacterListUseCase
import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchMoveListUseCase
import io.github.sophon.cornerman.screens.moveList.ui.MoveListVM
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val moveListModule = module {
    viewModelOf(::MoveListVM)

    singleOf(::FetchMoveListUseCase)
    singleOf(::FetchCharacterListUseCase)
}