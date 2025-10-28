package com.example.cornerman.moveList

import com.example.cornerman.moveList.ui.MoveListVM
import com.example.cornerman.moveList.useCase.FetchMovesForUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun moveListModule() = module {
    viewModelOf(::MoveListVM)

    singleOf(::FetchMovesForUseCase)
}