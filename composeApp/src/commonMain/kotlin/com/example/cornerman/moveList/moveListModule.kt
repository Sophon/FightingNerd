package com.example.cornerman.moveList

import com.example.cornerman.moveList.data.InMemoryMoveListDB
import com.example.cornerman.moveList.ui.MoveListVM
import com.example.cornerman.moveList.useCase.FetchMoveListUseCase
import com.example.wikiwavu.data.MoveListDB
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun moveListModule() = module {
    viewModelOf(::MoveListVM)

    singleOf(::FetchMoveListUseCase)

    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()
}